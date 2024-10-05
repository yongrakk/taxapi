package com.example.taxapi.repository;

import com.example.taxapi.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Member,String> {
    Optional<Member> findByUserId(String userId);
}
