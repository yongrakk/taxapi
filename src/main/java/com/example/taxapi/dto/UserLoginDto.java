package com.example.taxapi.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UserLoginDto {

    @NotBlank(message = "사용자 아이디를 입력해주세요.")
    @Schema(description = "비밀번호", defaultValue = "kw68")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", defaultValue = "123456")
    private String password;

}
