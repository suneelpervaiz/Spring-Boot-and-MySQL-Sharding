package com.infoscap.shard.service;

import com.infoscap.shard.config.ShardingAlgorithm;
import com.infoscap.shard.context.DataSourceContextHolder;
import com.infoscap.shard.entity.User;
import com.infoscap.shard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        String dataSource = ShardingAlgorithm.determineDataSource(user.getUserId());
        DataSourceContextHolder.setDataSource(dataSource);
        User savedUser = userRepository.save(user);
        DataSourceContextHolder.clear();
        return savedUser;
    }

    public User getUser(Long id, int userId) {
        String dataSource = ShardingAlgorithm.determineDataSource(userId);
        DataSourceContextHolder.setDataSource(dataSource);
        Optional<User> user = userRepository.findById(id);
        DataSourceContextHolder.clear();
        return user.orElse(null);
    }

    public User updateUser(User user) {
        String dataSource = ShardingAlgorithm.determineDataSource(user.getUserId());
        DataSourceContextHolder.setDataSource(dataSource);
        User updatedUser = userRepository.save(user);
        DataSourceContextHolder.clear();
        return updatedUser;
    }

    public void deleteUser(Long id, int userId) {
        String dataSource = ShardingAlgorithm.determineDataSource(userId);
        DataSourceContextHolder.setDataSource(dataSource);
        userRepository.deleteById(id);
        DataSourceContextHolder.clear();
    }
}

