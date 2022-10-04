package com.everyday.api.domain.comment.controller;

import com.everyday.api.domain.comment.dto.CommentSaveDto;
import com.everyday.api.domain.comment.dto.CommentUpdateDto;
import com.everyday.api.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {


    private final CommentService commentService;

    @PostMapping("/comment/{missionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void commentSave(@PathVariable("missionId") Long missionId, CommentSaveDto commentSaveDto){
        commentService.save(missionId, commentSaveDto);
    }


    @PostMapping("/comment/{missionId}/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reCommentSave(@PathVariable("missionId") Long missionId,
                              @PathVariable("commentId") Long commentId,
                              CommentSaveDto commentSaveDto){
        commentService.saveReComment(missionId, commentId, commentSaveDto);
    }


    @PutMapping("/comment/{commentId}")
    public void update(@PathVariable("commentId") Long commentId,
                       CommentUpdateDto commentUpdateDto){
        commentService.update(commentId, commentUpdateDto);
    }


    @DeleteMapping("/comment/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId){
        commentService.remove(commentId);
    }
}

