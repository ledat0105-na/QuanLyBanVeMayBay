package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByIsActiveTrue();
}
