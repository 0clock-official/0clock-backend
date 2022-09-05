package com.oclock.oclock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oclock.oclock.config.TestConfig;
import com.oclock.oclock.dto.JoinRequest;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.security.WithMockJwtAuthentication;
import com.oclock.oclock.service.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigInteger;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes=TestConfig.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor
class MemberRestControllerTest {
    @MockBean
    private MemberService memberService;

    private MockMvc mockMvc;

    final Member member = Member.builder()
            .id(1L)
            .email(new Email("test@gmail.com"))
            .password("test123")
            .chattingRoomId(new BigInteger("0"))
            .chattingTime(1)
            .memberSex(1)
            .matchingSex(2)
            .major(1)
            .nickName("NickName")
            .joinStep(4)
            .build();


    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockJwtAuthentication
    @DisplayName("내 정보 조회 성공 테스트 (토큰이 올바른 경우)")
    void meSuccessTest() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/members/")
                        .accept(MediaType.APPLICATION_JSON)
        );
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberRestController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.email", is("test@gmail.com")))
                .andExpect(jsonPath("$.response.name", is("test")))
        ;
    }

    @DisplayName("회원가입 성공")
    @Test
    public void save() throws Exception {
        JoinRequest request = JoinRequest.builder()
                .principal("test@gmail.com")
                .credentials("test123")
                .build();

        Member resultUser = member;

        Mockito.when(memberService.join(new Email(request.getPrincipal()), request.getCredentials())).thenReturn(resultUser);

        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response", is("가입완료")));
    }
    @DisplayName("이메일 확인 성공")
    @Test
    void checkEmail() throws Exception {
        Email email = new Email("test@gmail.com");
        Mockito.when(memberService.checkEmail(email)).thenReturn(true);
        mockMvc.perform(post("/join/step1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

    }

    @PostMapping(path = "join/step1")
    @ApiOperation(value = "회원가입절차 시작 - 이메일 인증")
    public ResponseEntity<ResponseDto> checkEmail(@RequestBody @ApiParam(value = "example: {\"email\": \"test@test.com\"}") Map<String, String> request
    ) {
        Email email = new Email(request.get("email"));
        ResponseDto<Email> response = ResponseDto.<Email>builder()
                .success(true)
                .response("이메일 인증")
                .data(email)
                .build();
        return ResponseEntity.ok().body(response);
    }

}