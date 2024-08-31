package com.infoscap.shard.config;

import com.infoscap.shard.router.DynamicDataSourceRouter;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.infoscap.shard.repository",
        entityManagerFactoryRef = "dynamicEntityManagerFactory",
        transactionManagerRef = "dynamicTransactionManager"
)
public class DataSourceConfig {


    @Value("${datasource.shard1.url}")
    private String shard1Url;

    @Value("${datasource.shard1.username}")
    private String shard1Username;

    @Value("${datasource.shard1.password}")
    private String shard1Password;

    @Value("${datasource.shard1.driver-class-name}")
    private String shard1DriverClassName;

    @Value("${datasource.shard2.url}")
    private String shard2Url;

    @Value("${datasource.shard2.username}")
    private String shard2Username;

    @Value("${datasource.shard2.password}")
    private String shard2Password;

    @Value("${datasource.shard2.driver-class-name}")
    private String shard2DriverClassName;








    @Bean(name = "shard1DataSource")
    public DataSource shard1DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(shard1Url);
        dataSource.setUsername(shard1Username);
        dataSource.setPassword(shard1Password);
        dataSource.setDriverClassName(shard1DriverClassName);
        return dataSource;
    }

    @Bean(name = "shard2DataSource")
    public DataSource shard2DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(shard2Url);
        dataSource.setUsername(shard2Username);
        dataSource.setPassword(shard2Password);
        dataSource.setDriverClassName(shard2DriverClassName);
        return dataSource;
    }

    @Primary
    @Bean(name = "dynamicDataSource")
    public DataSource dataSource(
            @Qualifier("shard1DataSource") DataSource shard1DataSource,
            @Qualifier("shard2DataSource") DataSource shard2DataSource) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("shard1", shard1DataSource);
        targetDataSources.put("shard2", shard2DataSource);

        DynamicDataSourceRouter dataSourceRouter = new DynamicDataSourceRouter();
        dataSourceRouter.setTargetDataSources(targetDataSources);
        dataSourceRouter.setDefaultTargetDataSource(shard1DataSource);

        return dataSourceRouter;
    }

    @Primary
    @Bean(name = "dynamicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dynamicEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dynamicDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = builder
                .dataSource(dataSource)
                .packages("com.infoscap.shard.entity")
                .persistenceUnit("dynamic")
                .build();

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "none");
        jpaProperties.put("hibernate.show_sql", "true");
        em.setJpaPropertyMap(jpaProperties);

        return em;
    }

    @Bean(name = "shard1EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard1EntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("shard1DataSource") DataSource dataSource) {

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.show_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.infoscap.shard.entity")
                .persistenceUnit("shard1")
                .properties(jpaProperties) // Use .properties instead of .setJpaProperties
                .build();
    }

    @Bean(name = "shard2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard2EntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("shard2DataSource") DataSource dataSource) {

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.show_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.infoscap.shard.entity")
                .persistenceUnit("shard2")
                .properties(jpaProperties) // Use .properties instead of .setJpaProperties
                .build();
    }

    @Primary
    @Bean(name = "dynamicTransactionManager")
    public PlatformTransactionManager dynamicTransactionManager(
            @Qualifier("dynamicEntityManagerFactory") LocalContainerEntityManagerFactoryBean dynamicEntityManagerFactory) {
        return new JpaTransactionManager(dynamicEntityManagerFactory.getObject());
    }

    @Bean(name = "shard1TransactionManager")
    public PlatformTransactionManager shard1TransactionManager(
            @Qualifier("shard1EntityManagerFactory") LocalContainerEntityManagerFactoryBean shard1EntityManagerFactory) {
        return new JpaTransactionManager(shard1EntityManagerFactory.getObject());
    }

    @Bean(name = "shard2TransactionManager")
    public PlatformTransactionManager shard2TransactionManager(
            @Qualifier("shard2EntityManagerFactory") LocalContainerEntityManagerFactoryBean shard2EntityManagerFactory) {
        return new JpaTransactionManager(shard2EntityManagerFactory.getObject());
    }
}
