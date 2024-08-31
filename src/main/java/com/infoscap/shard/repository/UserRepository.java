package com.infoscap.shard.repository;

import com.infoscap.shard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
