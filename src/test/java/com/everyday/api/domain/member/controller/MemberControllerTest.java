package com.everyday.api.domain.member.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.everyday.api.domain.member.Member;
import com.everyday.api.domain.member.Role;
import com.everyday.api.domain.member.dto.MemberInfoDto;
import com.everyday.api.domain.member.dto.MemberSignUpDto;
import com.everyday.api.domain.member.dto.MemberUpdateDto;
import com.everyday.api.domain.member.exception.MemberExceptionType;
import com.everyday.api.domain.member.repository.MemberRepository;
import com.everyday.api.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {


    @Autowired MockMvc mockMvc;
    @Autowired EntityManager em;
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    PasswordEncoder passwordEncoder;

    private static String SIGN_UP_URL = "/signUp";

    private String username = "username";
    private String password = "password1234@";
    private String name = "shinD";
    private String email = "shinD@abc.com";



    private void signUpFail(String signUpData) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isBadRequest());
    }


    private void clear(){
        em.flush();
        em.clear();
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());
    }


    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer ";

    private String getAccessToken() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);


        MvcResult result = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }



    @Test
    public void ????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));

        //when
        signUp(signUpData);

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(member.getName()).isEqualTo(name);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }




    @Test
    public void ????????????_??????_?????????_??????() throws Exception {
        //given
        String noUsernameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(null, password, name, email));
        String noPasswordSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, null, name, email));
        String noNameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, null, email));
        String noNickNameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, null));

        //when, then
/*        signUp(noUsernameSignUpData);//????????? ?????????????????? ??????????????? 200
        signUp(noPasswordSignUpData);//????????? ?????????????????? ??????????????? 200
        signUp(noNameSignUpData);//????????? ?????????????????? ??????????????? 200
        signUp(noNickNameSignUpData);//????????? ?????????????????? ??????????????? 200
        signUp(noAgeSignUpData);//????????? ?????????????????? ??????????????? 200*/

        signUpFail(noUsernameSignUpData);//????????? ???????????? ??????????????? 400
        signUpFail(noPasswordSignUpData);//????????? ???????????? ??????????????? 400
        signUpFail(noNameSignUpData);//????????? ???????????? ??????????????? 400
        signUpFail(noNickNameSignUpData);//????????? ???????????? ??????????????? 400

        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }




    @Test
    public void ??????????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));

        signUp(signUpData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("name",name+"??????");
        map.put("email",email+"??????");
        String updateMemberData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/member")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateMemberData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(member.getName()).isEqualTo(name+"??????");
        assertThat(member.getEmail()).isEqualTo( email+"??????");
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

    }



    @Test
    public void ??????????????????_????????????????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("name",name+"??????");
        String updateMemberData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/member")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateMemberData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(member.getName()).isEqualTo(name+"??????");
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

    }



    @Test
    public void ??????????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password);
        map.put("toBePassword",password+"!@#@!#@!#");

        String updatePassword = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password+"!@#@!#@!#", member.getPassword())).isTrue();
    }




    @Test
    public void ??????????????????_??????_?????????????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password+"1");
        map.put("toBePassword",password+"!@#@!#@!#");

        String updatePassword = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password+"!@#@!#@!#", member.getPassword())).isFalse();
    }




    @Test
    public void ??????????????????_??????_????????????_????????????_??????_??????????????????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password);
        map.put("toBePassword","123123");

        String updatePassword = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                //.andExpect(status().isOk());
                .andExpect(status().isBadRequest());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("123123", member.getPassword())).isFalse();
    }




    @Test
    public void ????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        assertThrows(Exception.class, () -> memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????")));
    }




    @Test
    public void ????????????_??????_??????????????????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password+11);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(member).isNotNull();


    }



    @Test
    public void ????????????_??????_???????????????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader,BEARER+accessToken+"1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isForbidden());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(member).isNotNull();
    }




    @Test
    public void ???????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();


        //when
        MvcResult result = mockMvc.perform(
                        get("/member")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(member.getUsername()).isEqualTo(map.get("username"));
        assertThat(member.getName()).isEqualTo(map.get("name"));
        assertThat(member.getEmail()).isEqualTo(map.get("email"));

    }



    @Test
    public void ???????????????_??????_JWT??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();


        //when,then
        mockMvc.perform(
                        get("/member")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken+1))
                .andExpect(status().isForbidden());

    }



    /**
     * ?????????????????? ??????
     * ?????????????????? ?????? -> ???????????????
     * ?????????????????? ?????? -> ???????????????
     */
    @Test
    public void ??????????????????_??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Long id = memberRepository.findAll().get(0).getId();

        //when

        MvcResult result = mockMvc.perform(
                        get("/member/"+id)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("????????? ????????????"));
        assertThat(member.getUsername()).isEqualTo(map.get("username"));
        assertThat(member.getName()).isEqualTo(map.get("name"));
        assertThat(member.getEmail()).isEqualTo(map.get("email"));
    }



    @Test
    public void ??????????????????_??????_??????????????????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();


        //when
        MvcResult result = mockMvc.perform(
                        get("/member/2211")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();

        //then
        Map<String, Integer> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(map.get("errorCode")).isEqualTo(MemberExceptionType.NOT_FOUND_MEMBER.getErrorCode());//??? ?????????
    }



    @Test
    public void ??????????????????_??????_JWT??????() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();


        //when,then
        mockMvc.perform(
                        get("/member/1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken+1))
                .andExpect(status().isForbidden());

    }

}