package com.everyday.api.global.file.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockMultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    @Autowired
    FileService fileService;


    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.jpg", "image/jpg", new FileInputStream("/Users/hoyun/Downloads/022.jpg"));
    }

    @Test
    public void 파일저장_성공() throws Exception {
        //given, when
        String filePath = fileService.save(getMockUploadFile());

        //then
        File file = new File(filePath);
        assertThat(file.exists()).isTrue();

        //finally
        file.delete();//파일 삭제


    }

    @Test
    public void 파일삭제_성공() throws Exception {
        //given, when
        String filePath = fileService.save(getMockUploadFile());
        fileService.delete(filePath);

        //then
        File file = new File(filePath);
        assertThat(file.exists()).isFalse();

    }

}