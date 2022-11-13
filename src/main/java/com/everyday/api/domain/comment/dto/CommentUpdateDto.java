package com.everyday.api.domain.comment.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record CommentUpdateDto (
        Optional<String> content,
        Optional<MultipartFile> uploadFile
){ }
