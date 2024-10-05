package com.example.taxapi.service;

import com.example.taxapi.dto.UserLoginDto;
import com.example.taxapi.dto.UserSignupDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ApiService {

    public Map<String, Object> signUp(UserSignupDto userSignupDto) throws Exception;

    public Map<String, Object> login(UserLoginDto userLoginDto) throws Exception;

    public Map<String, Object> getScrap() throws Exception;

    public Map<String, Object> getRefund();

}
