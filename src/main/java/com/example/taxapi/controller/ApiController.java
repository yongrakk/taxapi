package com.example.taxapi.controller;

import com.example.taxapi.domain.Member;
import com.example.taxapi.dto.UserLoginDto;
import com.example.taxapi.dto.UserSignupDto;
import com.example.taxapi.jwt.JwtToken;
import com.example.taxapi.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ApiController {

    private final ApiService apiService;

    /**
     * 회원가입 API
     * RequestBody - userId, password, name, regNo
     *
     * @param userSignupDto
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @Tag(name="1.user" , description = "회원가입 API , 로그인 API")
    @Operation(summary = "회원가입 API", description = "userId, password, name, regNo을 이용하여 회원가입을 합니다.")
    @PostMapping(value = "/api/signup")
    public ResponseEntity signUp(@Valid @RequestBody UserSignupDto userSignupDto, BindingResult bindingResult) throws Exception{

        //RequestParameter valid 체크
        if(bindingResult.hasErrors()){
            StringBuilder sb = validErrorMessage(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());
        }

        Map<String, Object> resultMap = apiService.signUp(userSignupDto);

        if(resultMap.get("message").equals("no")){
            return ResponseEntity.ok(resultMap.get("result").toString());
        }

        Member resultMember = (Member) resultMap.get("result");

        return ResponseEntity.ok("회원가입 완료 userId : " + resultMember.getUserId() + ", name : " + resultMember.getName());
    }

    /**
     * 로그인 API
     * RequestBody - userId, password
     * @param userLoginDto
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @Tag(name="1.user")
    @Operation(summary = "로그인 API", description = "userId, password 를 이용하여 토큰을 발급합니다.")
    @PostMapping("/api/login")
    public ResponseEntity login(@Valid @RequestBody UserLoginDto userLoginDto, BindingResult bindingResult) throws Exception{

        //RequestParameter valid 체크
        if(bindingResult.hasErrors()){
            StringBuilder sb = validErrorMessage(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());
        }

        Map<String, Object> resultMap = apiService.login(userLoginDto);

        if(resultMap.get("message").equals("no")){
            return ResponseEntity.ok(resultMap.get("result").toString());
        }

        JwtToken jwtToken = (JwtToken) resultMap.get("result");

        return ResponseEntity.ok(jwtToken);
    }

    /**
     * 소득정보 조회 API
     * Request Header - Authorization : Bearer
     *
     * @return
     * @throws Exception
     */
    @Tag(name="2.Authorization" , description = "스크래핑 API, 결정세액 조회 API")
    @Operation(summary = "스크래핑 API", description = "토큰 정보로 해당하는 유저의 소득정보를 저장합니다.")
    @PostMapping("/api/scrap")
    public ResponseEntity scrap() throws Exception{

        Map<String, Object> resultMap = apiService.getScrap();

        if(resultMap.get("message").equals("no")){
            return ResponseEntity.ok(resultMap.get("result").toString());
        }

        String userId = String.valueOf(resultMap.get("result"));

        return ResponseEntity.ok("스크래핑 완료 : " + userId +" 의 소득정보를 저장했습니다.");
    }

    /**
     * 결정세액 조회 API
     * Request Header - Authorization : Bearer
     *
     * @return
     */
    @Tag(name="2.Authorization")
    @Operation(summary = "결정세액 조회 API", description = "토큰 정보로 해당하는 유저의 결정세액을 조회합니다.")
    @GetMapping("/api/refund")
    public ResponseEntity refund(){

        Map<String, Object> resultMap = apiService.getRefund();

        if(resultMap.get("message").equals("no")){
            return ResponseEntity.ok(resultMap.get("result").toString());
        }

        String resultTax = String.valueOf(resultMap.get("result"));
        Map<String, String> tax = new HashMap<>();

        tax.put("결정세액", resultTax);

        return ResponseEntity.ok(tax);
    }

    /**
     * Request Parameter 오류 메시지 생성
     * @param bindingResult
     * @return
     */
    public StringBuilder validErrorMessage(BindingResult bindingResult){
        StringBuilder sb = new StringBuilder() ;
        bindingResult.getAllErrors().forEach(objectError -> {
            FieldError fieldError = (FieldError) objectError;
            String message = objectError.getDefaultMessage();

            sb.append(" field : " + fieldError.getField());
            sb.append(" , ");
            sb.append("message : " + message);

        });

        return sb;
    }


}
