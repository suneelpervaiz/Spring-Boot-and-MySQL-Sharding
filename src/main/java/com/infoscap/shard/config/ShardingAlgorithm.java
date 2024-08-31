package com.infoscap.shard.config;

public class ShardingAlgorithm {

    public static String determineDataSource(int userId) {
       // int hash = userId.hashCode();

        int hash = userId;

        System.out.println("******************************"+hash);


        return (hash % 2 == 0) ? "shard1" : "shard2";
    }
}