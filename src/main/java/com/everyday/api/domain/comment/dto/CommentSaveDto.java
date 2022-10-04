package com.everyday.api.domain.comment.dto;

import com.everyday.api.domain.comment.Comment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record CommentSaveDto (String content,
                              Optional<MultipartFile> uploadFile ){

    public Comment toEntity() {
        return Comment.builder().content(content).build();
    }
}
