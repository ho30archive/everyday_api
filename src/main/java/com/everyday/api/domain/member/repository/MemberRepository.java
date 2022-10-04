package com.everyday.api.domain.member.repository;


import com.everyday.api.domain.member.Member;
import com.everyday.api.global.cashe.CacheLogin;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Cacheable(value = CacheLogin.USER, key = "#p0")//String은 p0, p1,...
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Member> findByRefreshToken(String refreshToken);
}
