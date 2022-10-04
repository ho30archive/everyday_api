package com.everyday.api.domain.comment.service;

import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.comment.dto.CommentSaveDto;
import com.everyday.api.domain.comment.dto.CommentUpdateDto;
import com.everyday.api.domain.comment.exception.CommentException;

import java.util.List;

public interface CommentService {

    void save(Long postId , CommentSaveDto commentSaveDto);
    void saveReComment(Long postId, Long parentId , CommentSaveDto commentSaveDto);

    void update(Long id, CommentUpdateDto commentUpdateDto);

    void remove(Long id) throws CommentException;
}
