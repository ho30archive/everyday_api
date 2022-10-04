package com.everyday.api.domain.mission.repository;

import com.everyday.api.domain.mission.Mission;
import com.everyday.api.domain.mission.cond.MissionSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.everyday.api.domain.mission.repository.CustomMissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.everyday.api.domain.member.QMember.member;
import static com.everyday.api.domain.mission.QMission.mission;


@Repository
public class CustomMissionRepositoryImpl implements CustomMissionRepository {

    private final JPAQueryFactory query;

    public CustomMissionRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }


    @Override
    public Page<Mission> search(MissionSearchCondition missionSearchCondition, Pageable pageable) {



        List<Mission> content = query.selectFrom(mission)

                .where(
                        contentHasStr(missionSearchCondition.getContent()),
                        titleHasStr(missionSearchCondition.getTitle())
                )
                .leftJoin(mission.writer, member)

                .fetchJoin()
                .orderBy(mission.createdDate.desc())//최신 날짜부터
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(); //Count 쿼리 발생 X




        JPAQuery<Mission> countQuery = query.selectFrom(mission)
                .where(
                        contentHasStr(missionSearchCondition.getContent()),
                        titleHasStr(missionSearchCondition.getTitle())
                );



        return  PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression contentHasStr(String content) {
        return StringUtils.hasLength(content) ? mission.content.contains(content) : null;
    }


    private BooleanExpression titleHasStr(String title) {
        return StringUtils.hasLength(title) ? mission.title.contains(title) : null;
    }
}


