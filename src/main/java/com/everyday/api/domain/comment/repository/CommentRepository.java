package com.everyday.api.domain.comment.repository;


import com.everyday.api.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
