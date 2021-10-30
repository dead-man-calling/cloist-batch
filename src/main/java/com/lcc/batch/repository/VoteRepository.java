package com.lcc.batch.repository;

import com.lcc.batch.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByCreatedAtBefore(LocalDateTime localDateTime);
}
