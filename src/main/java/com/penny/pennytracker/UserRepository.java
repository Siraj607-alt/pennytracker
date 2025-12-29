package com.penny.pennytracker;


import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // custom queries can go here later
    User findByEmail(String email);
    User findByEmailIgnoreCase(String email);


}