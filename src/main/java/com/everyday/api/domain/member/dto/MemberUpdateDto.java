package com.everyday.api.domain.member.dto;

import com.everyday.api.domain.member.Member;
import lombok.*;

import java.util.Optional;


public record MemberUpdateDto(Optional<String> name, Optional<String> email) {
}
