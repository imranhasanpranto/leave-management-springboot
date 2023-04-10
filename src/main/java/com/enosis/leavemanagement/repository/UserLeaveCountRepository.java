package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.model.UserLeaveCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserLeaveCountRepository extends JpaRepository<UserLeaveCount, Long> {
    public Optional<UserLeaveCount> findByUserIdAndYear(Long userId, Integer year);
}
