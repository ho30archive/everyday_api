package com.everyday.api.domain.mission.repository;

import com.everyday.api.domain.mission.Mission;
import com.everyday.api.domain.mission.cond.MissionSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMissionRepository {

    Page<Mission> search(MissionSearchCondition missionSearchCondition, Pageable pageable);
}

