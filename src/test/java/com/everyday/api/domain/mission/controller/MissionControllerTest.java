package com.everyday.api.domain.mission.controller;

import static org.junit.jupiter.api.Assertions.*;


import com.everyday.api.domain.member.Member;
import com.everyday.api.domain.member.Role;
import com.everyday.api.domain.member.repository.MemberRepository;
import com.everyday.api.domain.mission.Mission;
import com.everyday.api.domain.mission.dto.MissionInfoDto;
import com.everyday.api.domain.mission.dto.MissionPagingDto;
import com.everyday.api.domain.mission.repository.MissionRepository;
import com.everyday.api.global.file.service.FileService;
import com.everyday.api.global.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MissionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired EntityManager em;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MissionRepository missionRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;
    final String USERNAME = "username1";

    private static Member member;


    private void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void signUpMember(){
        member = memberRepository.save(Member.builder().username(USERNAME).password("1234567890").name("USER1").email("USER1@abc.com").role(Role.USER).build());
        clear();
    }

    private String getAccessToken(){
        return jwtService.createAccessToken(USERNAME);
    }


    private MockMultipartFile getMockUploadFile() throws IOException {
        //TODO : name??? ??????
        return new MockMultipartFile("uploadFile", "file.jpg", "image/jpg", new FileInputStream("/Users/hoyun/Downloads/022.jpg"));
    }

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????() throws Exception {
        //given
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "??????");
        map.add("content", "??????");


        //when
        mockMvc.perform(
                        post("/mission")
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isCreated());


        //then
        Assertions.assertThat(missionRepository.findAll().size()).isEqualTo(1);
    }

   /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????_????????????_?????????_??????() throws Exception {
        //given

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "??????");


        //when, then
        mockMvc.perform(
                        post("/mission")
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isBadRequest());

        map = new LinkedMultiValueMap<>();
        map.add("content", "??????");
        mockMvc.perform(
                        post("/mission")
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isBadRequest());

    }




    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_????????????_??????() throws Exception {
        //given
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(member);
        Mission saveMission = missionRepository.save(mission);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_TITLE = "??????";
        map.add("title", UPDATE_TITLE);

        //when
        mockMvc.perform(
                        put("/mission/"+saveMission.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))

                .andExpect(status().isOk());


        //then
        Assertions.assertThat(missionRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITLE);
    }

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_????????????_??????() throws Exception {
        //given
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(member);
        Mission saveMission = missionRepository.save(mission);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/mission/"+saveMission.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(missionRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
    }



    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????????????????_??????() throws Exception {
        //given
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(member);
        Mission saveMission = missionRepository.save(mission);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        final String UPDATE_TITlE = "??????";
        map.add("title", UPDATE_TITlE);
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/mission/"+saveMission.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(missionRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
        Assertions.assertThat(missionRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITlE);
    }

    /**
     ????????? ??????
     */
    @Test
    public void ?????????_??????_?????????????????????_??????() throws Exception {
        //given
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(member);
        Mission saveMission = missionRepository.save(mission);

        MockMultipartFile mockUploadFile = getMockUploadFile();


        //when

        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/mission/" + saveMission.getId());
        requestBuilder.with(request -> {
            request.setMethod(HttpMethod.PUT.name());
            return request;
        });

        mockMvc.perform(requestBuilder
                        .file(getMockUploadFile())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isOk());

        /*mockMvc.perform(multipart("/post/"+savePost.getId())

                                .file(getMockUploadFile())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                               )
                .andExpect(status().isOk());
*/

        //then
        String filePath = missionRepository.findAll().get(0).getFilePath();
        Assertions.assertThat(filePath).isNotNull();
        Assertions.assertThat(new File(filePath).delete()).isTrue();

    }

    /**
     ????????? ??????
     */
    @Autowired private FileService fileService;
    @Test
    public void ?????????_??????_?????????????????????_??????() throws Exception {
        //given
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(member);
        String path = fileService.save(getMockUploadFile());
        mission.updateFilePath(path);
        Mission saveMission = missionRepository.save(mission);

        Assertions.assertThat(missionRepository.findAll().get(0).getFilePath()).isNotNull();


        MockMultipartFile mockUploadFile = getMockUploadFile();


        //when

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        final String UPDATE_TITlE = "??????";
        map.add("title", UPDATE_TITlE);
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/mission/"+saveMission.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(missionRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
        Assertions.assertThat(missionRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITlE);
        Assertions.assertThat(missionRepository.findAll().get(0).getFilePath()).isNull();

    }



    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????_????????????() throws Exception {
        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").email("newMEm@abc.com").role(Role.USER).build());
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(newMember);
        Mission saveMission = missionRepository.save(mission);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        final String UPDATE_TITlE = "??????";
        map.add("title", UPDATE_TITlE);
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/mission/"+saveMission.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isForbidden());


        //then
        Assertions.assertThat(missionRepository.findAll().get(0).getContent()).isEqualTo("???????????????");
        Assertions.assertThat(missionRepository.findAll().get(0).getTitle()).isEqualTo("???????????????");
    }



    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????() throws Exception {
        //given
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(member);
        Mission saveMission = missionRepository.save(mission);

        //when
        mockMvc.perform(
                delete("/mission/"+saveMission.getId())
                        .header("Authorization", "Bearer "+ getAccessToken())
        ).andExpect(status().isOk());


        //then
        Assertions.assertThat(missionRepository.findAll().size()).isEqualTo(0);

    }

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????_????????????() throws Exception {
        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").email("newMEm@abc.com").role(Role.USER).build());
        Mission mission = Mission.builder().title("???????????????").content("???????????????").build();
        mission.confirmWriter(newMember);
        Mission saveMission = missionRepository.save(mission);

        //when
        mockMvc.perform(
                delete("/mission/"+saveMission.getId())
                        .header("Authorization", "Bearer "+ getAccessToken())
        ).andExpect(status().isForbidden());


        //then
        Assertions.assertThat(missionRepository.findAll().size()).isEqualTo(1);

    }


    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????() throws Exception {

        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").email("newMEm@abc.com").role(Role.USER).build());
        Mission mission = Mission.builder().title("title").content("content").build();
        mission.confirmWriter(newMember);
        Mission saveMission = missionRepository.save(mission);

        //when
        MvcResult result = mockMvc.perform(
                get("/mission/" + saveMission.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
        ).andExpect(status().isOk()).andReturn();

        MissionInfoDto missionInfoDto = objectMapper.readValue(result.getResponse().getContentAsString(), MissionInfoDto.class);


        //then
        Assertions.assertThat(missionInfoDto.getMissionId()).isEqualTo(mission.getId());
        Assertions.assertThat(missionInfoDto.getContent()).isEqualTo(mission.getContent());
        Assertions.assertThat(missionInfoDto.getTitle()).isEqualTo(mission.getTitle());


    }


    @Value("${spring.data.web.pageable.default-page-size}")
    private int pageCount;

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????() throws Exception {

        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").email("newMEm@abc.com").role(Role.USER).build());




        final int MISSION_COUNT = 50;
        for(int i = 1; i<= MISSION_COUNT; i++ ){
            Mission mission = Mission.builder().title("title"+ i).content("content"+i).build();
            mission.confirmWriter(newMember);
            missionRepository.save(mission);
        }

        clear();



        //when
        MvcResult result = mockMvc.perform(
                get("/mission")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
        ).andExpect(status().isOk()).andReturn();

        //then
        MissionPagingDto missionList = objectMapper.readValue(result.getResponse().getContentAsString(), MissionPagingDto.class);

        assertThat(missionList.getTotalElementCount()).isEqualTo(MISSION_COUNT);
        assertThat(missionList.getCurrentPageElementCount()).isEqualTo(pageCount);
        assertThat(missionList.getSimpleLectureDtoList().get(0).getContent()).isEqualTo("content50");

    }
}