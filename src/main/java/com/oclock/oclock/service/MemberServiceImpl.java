package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.dto.StudentCardDto;
import com.oclock.oclock.dto.response.ErrorMessage;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.model.Verification;
import com.oclock.oclock.repository.MemberRepository;
import com.oclock.oclock.repository.RefreshTokenRepository;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    @Value("${uploadPath}")
    private String uploadPath;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Member join(MemberDto memberDto) {
        if(memberRepository.checkVerification(memberDto.getEmail())) {
            return memberRepository.join(memberDto);
        }else {
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .code(401)
                    .message("인증 되지 않은 이메일 입니다.").build();
            throw new OClockException(errorMessage);
        }
    }

    @Override
    public void editMyself(long memberId,Map<String, String> body) {
        try {
            if (body.get("nickname") != null && body.get("nickname").equals("")) {
                memberRepository.updateNickname(memberId, body.get("nickname"));
            }
            if (body.get("chattingTime") != null && body.get("chattingTime").equals("")) {
                memberRepository.updateChattingTime(memberId, Integer.parseInt(body.get("chattingTime")));
            }
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("개인정보 수정에 실패하였습니다.")
                    .code(400)
                    .build();
            throw new OClockException(errorMessage);
        }
    }

    @Override
    public void updateFcm(long memberId,String fcmToken) {
        try {
            memberRepository.updateFcm(memberId,fcmToken);
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("알수 없는 이유로 토큰 갱신에 실패하였습니다.")
                    .code(500)
                    .build();
            throw new OClockException(errorMessage);
        }
    }
    
    //TODO 학생증 업로드 구현하기
    @Override
    public void updateEmailStudentCard(StudentCardDto studentCardDto,Member member) throws IOException {
        String uploadFolder = uploadPath;
        File uploadPath = new File(uploadFolder);
        if(uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }
        uploadPath.createNewFile();
        LoggerFactory.getLogger(this.getClass()).info(studentCardDto.toString());
        String img = studentCardDto.getIdCard();
        String[] imgAndExp = img.split(",");
        img = imgAndExp[1];
        String fileType = imgAndExp[0].split("/")[1].split(";")[0];
        byte[] decodedBytes = Base64.getDecoder().decode(img);
        FileUtils.writeByteArrayToFile(new File(uploadFolder +File.separator+member.getId()+"."+fileType), decodedBytes);
    }

    @Override
    public Member findById(Long id,RowMapper<Member> rowMapper) {
        try {
            checkArgument(id != null, "Id must be provided.");
            return memberRepository.selectMemberById(id, rowMapper);
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("개인정보 조회에 실패하였습니다.")
                    .code(500)
                    .build();
            throw new OClockException(errorMessage);
        }
    }

    @Override
    public Member findByEmail(Email email) {
        try {
            checkArgument(email != null, "email must be provided.");
            return memberRepository.findByEmail(email);
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("등록되지 않은 이메일 입니다.")
                    .code(401)
                    .build();
            throw new OClockException(errorMessage);
        }
    }


    @Override
    public void deleteAccount(Long id) {
        memberRepository.deleteAccount(id);
    }

    @Override
    public Member login(Email email, String password) {
        checkArgument(password != null, "password must be provided.");
        Member member = memberRepository.findByEmail(email);
        member.login(passwordEncoder, password);
        return member;
    }

    @Override
    public void checkVerification(String email, String verification) {
        List<Verification> verifications = memberRepository.getVerification(email);
        if(verifications.size() == 1 && verifications.get(0).getVerification().contentEquals(verification)){
            memberRepository.certVerification(email);
        }else{
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("이메일 인증에 실패하였습니다.")
                    .code(400).build();
            throw new OClockException(errorMessage);
        }
    }

    @Override
    public void renewVerification(String email, String verification) {
        try {
            List<Verification> verifications = memberRepository.getVerification(email);
            if (!verifications.isEmpty()) {
                memberRepository.updateVerification(email, verification);
                return;
            }
            memberRepository.insertVerification(email, verification);
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("인증코드 전송에 실패하였습니다.")
                    .code(400)
                    .build();
            throw new OClockException(errorMessage);
        }
    }

    @Override
    public String createRandomCode() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG");
        int randomInt = secureRandom.nextInt(999999);
        return String.format("%06d", randomInt);
    }

    //TODO 회원정보 수정 
    @Override
    public void updateMember(Member member) {

    }
    //TODO 패스워드 재설정
    @Override
    public void resetPassword(String email) {

    }
    @Override
    public void mergeToken(long id, String verification) {
        if (refreshTokenRepository.existsById(id)) {
            refreshTokenRepository.updateRefreshToken(verification, id);
        }
        else refreshTokenRepository.insertRefreshToken(id,verification);
    }

    @Override
    public boolean checkIdCard(Email email) {
        return false;
    }
}
