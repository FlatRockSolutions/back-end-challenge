package com.payprovider.withdrawal.repository;

import com.payprovider.withdrawal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
