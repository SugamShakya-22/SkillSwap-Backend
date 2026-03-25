package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.helper.MatchStatus;
import com.skillswap.skillswap.model.MatchRequest;
import com.skillswap.skillswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    // Fetch all requests sent to a specific user
    List<MatchRequest> findByToUser(User toUser);

    // Optional: Fetch all requests made by a user
    List<MatchRequest> findByFromUser(User fromUser);

    @Query("""
        SELECT mr 
        FROM MatchRequest mr
        WHERE (mr.fromUser = :user OR mr.toUser = :user)
        AND mr.status IN :allowedStatuses
    """)
    List<MatchRequest> findChatEligibleRequests(
            @Param("user") User user,
            @Param("allowedStatuses") List<MatchStatus> allowedStatuses
    );
}
