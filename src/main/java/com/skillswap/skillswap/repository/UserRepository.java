package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    //Fetch all users with profile and userSkills
    @NonNull
    @EntityGraph(attributePaths = {"profile", "userSkills", "userSkills.skill"})
    List<User> findAll();
}
