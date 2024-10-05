package com.example.taxapi.dto;

import com.example.taxapi.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Data
public class UserSignupDto {

    @NotBlank(message = "사용자 아이디를 입력해주세요.")
    @Schema(description = "사용자 아이디", defaultValue = "kw68")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", defaultValue = "123456")
    private String password;

    @NotBlank(message = "사용자 이름을 입력해주세요")
    @Schema(description = "사용자 이름", defaultValue = "관우", allowableValues = {"동탁","관우","손권","유비","조조"})
    private String name;

    @NotBlank(message = "주민번호를 입력해주세요")
    @Schema(description = "YYYYMMDD-gabcdef", defaultValue = "681108-1582816")
    @Pattern(regexp = "^([0-9]{6})(-[0-9]{7})$",message = "주민번호 형식과 맞지 않습니다.")
    private String regNo;

    @Schema(hidden = true)
    private List<String> roles = new ArrayList<>();

    @Builder
    public Member toEntity(){
        Member member = Member.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .regNo(regNo)
                .roles(roles)
                .build();
        return member;
    }

}
