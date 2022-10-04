package com.everyday.api.global.file.service;

import com.everyday.api.global.file.exception.FileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    //저장된 파일 경로 반환
    String save(MultipartFile multipartFile) throws FileException;

    void delete(String filePath);

}
