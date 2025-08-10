package com.tastyhouse.core.repository;

import com.tastyhouse.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    List<User> findAll();
}
