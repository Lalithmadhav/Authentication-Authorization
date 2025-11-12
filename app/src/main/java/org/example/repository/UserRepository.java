package org.example.repository;

import org.example.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {
    Optional<UserInfo> findByUserName(String userName);
    Boolean existsByUserName(String userName);
}
