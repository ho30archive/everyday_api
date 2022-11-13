package com.everyday.api.domain.comment.service;


import com.everyday.api.domain.comment.dto.CommentSaveDto;
import com.everyday.api.domain.comment.dto.CommentUpdateDto;
import com.everyday.api.domain.comment.exception.CommentException;


public interface CommentService {

    void save(Long missionId , CommentSaveDto commentSaveDto);
    void saveReComment(Long missionId, Long parentId , CommentSaveDto commentSaveDto);

    void update(Long id, CommentUpdateDto commentUpdateDto);

    void remove(Long id) throws CommentException;
}
