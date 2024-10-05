package com.example.taxapi.service;

import com.example.taxapi.domain.*;
import com.example.taxapi.domain.pk.IncomePk;
import com.example.taxapi.domain.pk.TaxCreditPk;
import com.example.taxapi.dto.*;
import com.example.taxapi.jwt.JwtToken;
import com.example.taxapi.jwt.JwtTokenProvider;
import com.example.taxapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ApiServiceImpl implements ApiService {

    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final PensionRepository pensionRepository;
    private final CreditDeductionRepository creditDeductionRepository;
    private final TaxCreditRepository taxCreditRepository;

    private final PasswordEncoder passwordEncoder;
    private final EncryptService encryptService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private static String yearData;

    /**
     * 회원가입
     * Table : Member , PK : userId
     * @param userSignupDto
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> signUp(UserSignupDto userSignupDto) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        //사용 가능한 사용자 정보
        Map<String, String> enableUserMap = getEnableUserList();

        try{
            int tempInt = 0;

            //사용할 수 있는 사용자 체크
            for(String key : enableUserMap.keySet()){
                //사용자 이름 체크
                if(key.equals(userSignupDto.getName())){
                    if(enableUserMap.get(key).equals(userSignupDto.getRegNo())){
                        //이름 - 주민번호 일치함
                        tempInt = 2;
                    } else {
                        //이름 - 주민번호 일치 하지 않음
                        tempInt = 1;
                    }
                }
            }

            if(tempInt == 0){
                resultMap.put("message","no");
                resultMap.put("result","이름 : " + userSignupDto.getName() + " 은 사용할 수 없습니다. ");
                return resultMap;
            }

            if(tempInt == 1){
                resultMap.put("message","no");
                resultMap.put("result","이름 : " + userSignupDto.getName() + "과 주민번호 : " + userSignupDto.getRegNo() +"이 일치하지 않습니다.");
                return resultMap;
            }

            //아이디 중복체크
            Boolean checkId = userRepository.existsById(userSignupDto.getUserId());
            if(checkId){
                resultMap.put("message","no");
                resultMap.put("result","이미 사용중인 아이디 입니다. : " + userSignupDto.getUserId());
                return resultMap;
            }

            userSignupDto.setPassword(passwordEncoder.encode(userSignupDto.getPassword()));
            userSignupDto.setRegNo(encryptService.encryptVo(userSignupDto.getRegNo()));
            userSignupDto.setRoles(new ArrayList<>(Arrays.asList("USER")));

            Member member = userRepository.save(userSignupDto.toEntity());

            resultMap.put("message","ok");
            resultMap.put("result",member);

            return resultMap;

        } catch (Exception e ){
            resultMap.put("message","no");
            resultMap.put("result",e.getMessage());
            return resultMap;
        }
    }

    /**
     * userId, password 요청
     * accessToken 발급
     *
     * @param userLoginDto
     * @return
     */
    @Override
    public Map<String, Object> login(UserLoginDto userLoginDto) throws Exception{

        Map<String, Object> resultMap = new HashMap<>();
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginDto.getUserId(),userLoginDto.getPassword());

            //UserDetailService DB 유저를 조회하여 UserDetail 생성
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // Access Token 발급
            JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

            resultMap.put("message","ok");
            resultMap.put("result",jwtToken);

            return resultMap;

        } catch (Exception e){
            resultMap.put("message","no");
            resultMap.put("result",e.getMessage());
            return resultMap;
        }
    }

    /**
     * AccessToken 인증된 유저 정보로 소득정보 스크래핑
     *
     */
    @Override
    public Map<String, Object> getScrap() throws Exception{
        Map<String, Object> resultMap = new HashMap<>();

        try {
            //로그인된 유저 정보
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId =  userDetails.getUsername();

            //DB에 저장된 유저 정보
            Optional<Member> findMember = userRepository.findByUserId(userId);
            Member member = findMember.get();

            //스크래핑 url
            String url = "https://tax.com/scrap";

            RestTemplate restTemplate = new RestTemplate();

            //스크래핑 요청 Header
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json");
            httpHeaders.add("x-api-key","aXC8zK6puHIf9l53L8TiQg==");

            //스크래핑 요청 Body
            Map<String, Object> map = new HashMap<>();
            String name = member.getName();
            String regNo = encryptService.decryptVo(member.getRegNo());
            map.put("name",name);
            map.put("regNo",regNo);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, httpHeaders);

            //POST 호출
            ResponseEntity<Map> result = restTemplate.exchange(url,HttpMethod.POST,entity,Map.class);

            //스크래핑 결과 데이터
            Map<String, Object> responseMap = (Map)result.getBody().get("data");

            /**
             * 소득공제 처리
             * -신용카드 소득공제, 세액공제, 국민연금 데이터 처리
             *
             */
            Map<String, Object> deductionMap = (Map) responseMap.get("소득공제");

            for(String deductionKey : deductionMap.keySet()){

                if (deductionKey.equals("신용카드소득공제")){

                    Map<String, Object> creditDeductionMap = (Map) deductionMap.get(deductionKey);

                    yearData = String.valueOf(creditDeductionMap.get("year"));
                    processCreditDeduction(member, creditDeductionMap);

                } else if (deductionKey.equals("세액공제")){

                    Map<String, Object> taxCreditMap = new HashMap<>();
                    taxCreditMap.put("taxCreditAmount",deductionMap.get(deductionKey));
                    processTaxCredit(member, taxCreditMap);

                } else if(deductionKey.equals("국민연금")){

                    List<Map<String, Object>> pensionMapList = (List) deductionMap.get(deductionKey);
                    processPension(member,pensionMapList);

                }
            }

            /**
             * 종합소득금액 처리
             */
            Map<String, Object> incomeAmountMap = new HashMap<>();
            incomeAmountMap.put("incomeAmount", responseMap.get("종합소득금액"));
            processIncomeAmount(member, incomeAmountMap);

            resultMap.put("message","ok");
            resultMap.put("result",userId);

            return resultMap;

        } catch (Exception e){
            resultMap.put("message","no");
            resultMap.put("result",e.getMessage());
            return resultMap;
        }
    }

    /**
     * 결정세액 조회
     *
     * @return
     */
    @Override
    public Map<String, Object> getRefund() {

        Map<String, Object> resultMap = new HashMap<>();

        try {

            //로그인된 유저 정보
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId =  userDetails.getUsername();

            //기준년도
            String year = "2023";

            //종합소득금액 조회
            IncomePk incomePk = IncomePk.builder().incomeYear(year).userId(userId).build();
            Optional<Income> findIncome = incomeRepository.findById(incomePk);
            Double incomeAmount = findIncome.get().getIncomeAmount();

            //소득공제 - 국민연금 조회
            PensionSumDto pensionSumDto = pensionRepository.findGroupByUserIdAndYear(userId, year);
            Double pensionAmount = pensionSumDto.getPensionAmountSum();

            //소득공제 - 신용카드 소득공제 조회
            CreditDeductionSumDto creditDeductionSumDto = creditDeductionRepository.findGroupByUserIdAndYear(userId, year);
            Double creditAmount = creditDeductionSumDto.getCreditDeductionAmountSum();

            //산출세액 계산
            Long outputTax = outputTax(incomeAmount, pensionAmount, creditAmount);

            //세액공제 조회
            TaxCreditPk taxCreditPk = TaxCreditPk.builder().userId(userId).taxCreditYear(year).build();
            Optional<TaxCredit> findTaxCredit = taxCreditRepository.findById(taxCreditPk);
            Double taxCreditAmount = findTaxCredit.get().getTaxCreditAmount();

            //결정세액 = 산출세액 - 세액공제
            Long result = outputTax - Math.round(taxCreditAmount);
            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            String resultTax = decimalFormat.format(result);

            resultMap.put("message","ok");
            resultMap.put("result",resultTax);

            return resultMap;

        } catch (Exception e){
            resultMap.put("message","no");
            resultMap.put("result",e.getMessage());
            return resultMap;
        }
    }

    /**
     * 종합소득금액 처리
     * DB 저장
     * Table : Income , PK : incomeYear, userId
     * @param incomeAmountMap
     */
    public void processIncomeAmount(Member member, Map<String, Object> incomeAmountMap){

        IncomeDto incomeDto = new IncomeDto();
        incomeDto.setYear(yearData);
        incomeDto.setUserId(member.getUserId());
        String tempAmount = String.valueOf(incomeAmountMap.get("incomeAmount")).replace(",","");
        incomeDto.setIncomeAmount(Double.parseDouble(tempAmount));

        incomeRepository.save(incomeDto.toEntity());

    }

    /**
     * 소득공제 - 국민연금 처리
     * DB 저장
     * Table : Pension , PK : pensionYear, pensionMonth, userId
     * @param pensionMapList
     */
    public void processPension(Member member, List<Map<String, Object>> pensionMapList){

        PensionDto pensionDto = new PensionDto();
        pensionDto.setUserId(member.getUserId());

        List<Pension> pensionList = new ArrayList<>();

        for(Map<String, Object> pensionMap : pensionMapList){
            for(String pensionKey : pensionMap.keySet()){
                if(pensionKey.equals("월")){
                    String yearMonth = String.valueOf(pensionMap.get(pensionKey));
                    String[] yearMonthArr =  yearMonth.split("-");
                    String year = yearMonthArr[0];
                    String month = yearMonthArr[1];
                    pensionDto.setYear(year);
                    pensionDto.setMonth(month);
                } else if (pensionKey.equals("공제액")){
                    String tempAmount = String.valueOf(pensionMap.get(pensionKey)).replace(",","");
                    pensionDto.setPensionAmount(Double.parseDouble(tempAmount));
                }
            }
            pensionList.add(pensionDto.toEntity());
        }
        pensionRepository.saveAll(pensionList);
    }

    /**
     * 소득공제 - 신용카드소득공제 처리
     * DB 저장
     * Table : CreditDeduction , PK : creditDeductionYear, creditDeductionMonth, userId
     * @param creditDeductionMap
     */
    public void processCreditDeduction(Member member, Map<String, Object> creditDeductionMap){

        CreditDeductionDto creditDeductionDto = new CreditDeductionDto();
        creditDeductionDto.setUserId(member.getUserId());
        creditDeductionDto.setYear(yearData);

        List<CreditDeduction> creditDeductionList = new ArrayList<>();

        for(String creditDeductionKey : creditDeductionMap.keySet()){

            if(creditDeductionKey.equals("month")){
                List<Map<String,Object>> creditDeductionMonthList = (List) creditDeductionMap.get(creditDeductionKey);

                for(Map<String, Object> creditMonthMap : creditDeductionMonthList){

                    for(String creditMonthKey : creditMonthMap.keySet()){
                        String tempAmount = String.valueOf(creditMonthMap.get(creditMonthKey)).replace(",","");
                        creditDeductionDto.setCreditDeductionAmount(Double.parseDouble(tempAmount));
                        creditDeductionDto.setMonth(creditMonthKey);
                    }

                    creditDeductionList.add(creditDeductionDto.toEntity());
                }
            }
        }

        creditDeductionRepository.saveAll(creditDeductionList);
    }

    /**
     * 소득공제 - 세액공제 처리
     * DB 저장
     * Table : TaxCredit , PK : taxCreditYear, userID
     * @param taxCreditMap
     */
    public void processTaxCredit(Member member, Map<String, Object> taxCreditMap){

        TaxCreditDto taxCreditDto = new TaxCreditDto();
        taxCreditDto.setUserId(member.getUserId());
        taxCreditDto.setYear(yearData);
        String tempAmount = String.valueOf(taxCreditMap.get("taxCreditAmount")).replace(",","");
        taxCreditDto.setTaxCreditAmount(Double.parseDouble(tempAmount));

        taxCreditRepository.save(taxCreditDto.toEntity());
    }

    /**
     * 산출세액 계산
     * 계산식 : 산출세액 = 과세표준 * 기본세율
     *
     * @param incomeAmount
     * @param pensionAmount
     * @param creditAmount
     * @return
     */
    public Long outputTax(Double incomeAmount, Double pensionAmount, Double creditAmount){

        Long outputTax = 0L;

        //과세표준 = 종합소득금액 - 소득공제(국민연금 + 신용카드 소득공제)
        Long taxBase = Math.round( incomeAmount - Math.round(pensionAmount + creditAmount) );

        //구간별 산출세액 계산
        if( taxBase <= 14000000) {
             outputTax = Math.round(taxBase * 0.6);
        } else if (14000000 < taxBase && taxBase <= 50000000 ){
            outputTax = 840000 + Math.round( (taxBase - 14000000) * 0.15);
        } else if(50000000 < taxBase && taxBase <= 88000000){
            outputTax = 6240000 + Math.round( (taxBase - 50000000) * 0.24);
        } else if(88000000 < taxBase && taxBase <= 150000000){
            outputTax = 15360000 + Math.round( (taxBase - 88000000) * 0.35);
        } else if(150000000 < taxBase && taxBase <= 300000000){
            outputTax = 37060000 + Math.round(( taxBase - 150000000) * 0.38);
        } else if(300000000 < taxBase && taxBase <= 500000000){
            outputTax = 94060000 + Math.round( (taxBase - 300000000) * 0.4);
        } else if(500000000 < taxBase && taxBase <= 1000000000){
            outputTax = 174060000 + Math.round( (taxBase - 500000000) * 0.42);
        } else if(1000000000 < taxBase) {
            outputTax = 384060000 + Math.round( (taxBase - 1000000000) * 0.45);
        }

        return outputTax;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getEnableUserList(){

        Map<String, String> enableUser = new HashMap<>();

        /**
         * 프로젝트에서 가입할 수 있는 사용자 목록 이름 - 주민번호 조합
         */
        enableUser.put("동탁","921108-1582816");
        enableUser.put("관우","681108-1582816");
        enableUser.put("손권","890601-2455116");
        enableUser.put("유비","790411-1656116");
        enableUser.put("조조","810326-2715702");

        return enableUser;
    }
}
