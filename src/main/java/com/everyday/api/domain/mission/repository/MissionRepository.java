package com.everyday.api.domain.mission.repository;

import com.everyday.api.domain.mission.Mission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> , CustomMissionRepository{


    @EntityGraph(attributePaths = {"writer"})
    Optional<Mission> findWithWriterById(Long id);

    @Override
    void delete(Mission entity);
}

