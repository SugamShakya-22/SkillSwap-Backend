package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.Profile;
import com.skillswap.skillswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);

    Optional<Profile> findByUserId(Long userId);

}
