package com.oclock.oclock.controller;

import com.oclock.oclock.config.TestConfig;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.security.WithMockJwtAuthentication;
import com.oclock.oclock.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigInteger;

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
class MemberRestControllerTest {


    private MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
//    @MockBean
//    MemberService memberService;

    @Test
    @WithMockJwtAuthentication
    @DisplayName("내 정보 조회 성공 테스트 (토큰이 올바른 경우)")
    void meSuccessTest() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON)
        );
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberRestController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.name", is("tester00")))
                .andExpect(jsonPath("$.response.email.address", is("test00@gmail.com")))
        ;
    }

//    @DisplayName("회원가입 성공")
//    @Test
//    public void save() throws Exception {
//        JoinRequest request = JoinRequest.builder()
//                .principal("test@gmail.com")
//                .credentials("test123")
//                .build();
//
//        Member resultUser = getMemberEntity();
//
//        Mockito.when(memberService.join(new Email(request.getPrincipal()), request.getCredentials())).thenReturn(resultUser);
//
//        mockMvc.perform(post("/api/member/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success", is(true)))
//                .andExpect(jsonPath("$.response", is("가입완료")));
//    }
    @Test
    void checkEmail() {

    }

    private Member getMemberEntity() {
        return Member.builder()
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
    }
}