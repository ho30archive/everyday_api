package com.everyday.api.global.login;


import com.everyday.api.domain.member.Member;
import com.everyday.api.domain.member.Role;
import com.everyday.api.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired MockMvc mockMvc;

    @Autowired MemberRepository memberRepository;

    @Autowired EntityManager em;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "username";
    private static String PASSWORD = "123456789";

    private static String LOGIN_RUL = "/login";


    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    private void clear(){
        em.flush();
        em.clear();
    }


    @BeforeEach
    private void init(){
        memberRepository.save(Member.builder()
                .username(USERNAME)
                .password(delegatingPasswordEncoder.encode(PASSWORD))
                .name("Member1")
                .email("member@abc.om")
                .role(Role.USER)
                .build());
        clear();
    }

    private Map getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }


    private ResultActions perform(String url, MediaType mediaType, Map usernamePasswordMap) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(usernamePasswordMap)));

    }



    @Test
    public void ?????????_??????() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        //then
        assertThat(result.getResponse().getHeader(accessHeader)).isNotNull();
        assertThat(result.getResponse().getHeader(refreshHeader)).isNotNull();
    }


    @Test
    public void ?????????_??????_???????????????() throws Exception {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("username",USERNAME+"123");
        map.put("password",PASSWORD);


        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                //.andExpect(status().isOk())//TODO ??????????????????
                .andExpect(status().isBadRequest())
                .andReturn();


        //then
        assertThat(result.getResponse().getHeader(accessHeader)).isNull();
        assertThat(result.getResponse().getHeader(refreshHeader)).isNull();

    }

    @Test
    public void ?????????_??????_??????????????????() throws Exception {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("username",USERNAME);
        map.put("password",PASSWORD+"123");


        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                //.andExpect(status().isOk())//TODO ??????????????????
                .andExpect(status().isBadRequest())
                .andReturn();


        //then
        assertThat(result.getResponse().getHeader(accessHeader)).isNull();
        assertThat(result.getResponse().getHeader(refreshHeader)).isNull();

    }


    @Test
    public void ?????????_?????????_?????????_FORBIDDEN() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when, then
        perform(LOGIN_RUL+"123", APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isForbidden());

    }



    // ?????????_???????????????_JSON???_?????????_200
    @Test
    public void ?????????_???????????????_JSON???_?????????_400() throws Exception {

        //given
        Map<String, String> map = new HashMap<>();
        map.put("username",USERNAME);
        map.put("password",PASSWORD);


        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                //.andExpect(status().isOk())  TODO ??????????????????
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).isEqualTo(null);
    }


    @Test
    public void ?????????_HTTP_METHOD_GET??????_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_RUL)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    public void ??????_?????????_HTTP_METHOD_PUT??????_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(LOGIN_RUL)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }




}