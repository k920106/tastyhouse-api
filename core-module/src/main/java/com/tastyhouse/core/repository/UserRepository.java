package com.tastyhouse.core.repository;

import com.tastyhouse.core.entity.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();
}
