package com.lambdaschool.zoos.repository;

import com.lambdaschool.zoos.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long>
{

    User findByUsername(String toLowerCase);

    List<User> findByUsernameContainingIgnoreCase(String toUpperCase);
}
