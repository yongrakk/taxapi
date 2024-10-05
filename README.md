## 세금계산 샘플 프로젝트(Spring boot + H2 + Jwt + Swagger)


### ⚙개발 환경
- JAVA 17
- JDK 17
- **IDE** : Intellij   
- **Framework** : Spring boot 3.3
- **ORM** : JPA
- **Database** : H2
- **Library** : Hibernate, Spring Security, lombok, jwt, Swagger3
- **Build Tool** : Gradle 8.7

### 🔑 프로젝트 설명
**application.yml 설정 주요 사항**

- **H2**
   **ID :** test
   **PASSWORD :** 1234
   **CONSOLE :** /h2-console

- **JWT**
   **Header :** Authorization

- **SWAGGER**
   **path :** /api/swagger.html

**Swagger로 프로젝트 테스트 진행 설명**
* 접속 url : http://localhost:8080/api/swagger.html
* 1.swagger 화면에 1.user 태그에 해당하는 회원가입 API, 로그인 API 는 인증없이 사용 가능하도록 구현
* 2.swagger 화면에 2.Authorization 태그에 해당하는 스크래핑 API, 결정세액 조회 API 는 로그인 API 실행시에
*   발급받은 accessToken 인증 후 사용 가능하도록 구현
* 3.accessToken 인증은 swagger 화면 Authorize🔒 버튼에 입력 ❗ 인증타입(Bearer) 은 내부에서 처리 하므로 입력 불필요

  
### 📌주요 기능 
* 회원가입 ("/szs/signup")
  - 입력받은 유저 정보로 DB에 저장 Table:Member, PK:userId
  - 아이디(userId), 비밀번호(password), 이름(name), 주민등록번호(regNo) 를 입력받음
  - 각 입력값이 null 인 경우 가입 불가
  - 주민등록번호는 "6자리 숫자-7자리 숫자" 형식에 맞게만 입력 가능
  - 회원가입 가능한 유저 정보로만 가입 가능
  - 회원가입 가능한 유저 이름과 주민번호가 일치 할 경우만 가입가능
  - 아이디 중복 체크
  - 비밀번호 단방향 암호화, 주민등록번호 양방향 암호화 하여 저장

* 로그인 ("/szs/login")
  - 입력받은 유저 정보로 토큰 발급
  - 아이디(userId), 비밀번호(password) 를 입력받음
  - DB에 저장된 정보와 일치할 경우만 토큰 발급
 
* 스크래핑 ("/szs/scrap")
  - 요청 Header - Authorization 
  - 로그인 후에 발급한 토큰을 Authorization에서 전달받아 인증정보 확인
  - 인증된 토큰에 해당하는 유저를 DB에서 조회하여 스크래핑에 요청
  - restTemplate을 사용하여 스크래핑 정보 가져옴
  - 스크래핑한 데이터를 각 DB에 저장
  - 종합소득금액(소득연도,유저아이디,소득금액) - Table: Income
  - 국민연금(기준년도, 기준월, 유저아이디, 기준액) - Table: Pension
  - 신용카드 소득공제(기준년도, 기준월 , 유저아이디, 기준액) - Table: CreditDeduction
  - 세액공제 (공제년도,유저아이디, 공제액) - Table: TaxCredit

* 결정세액 조회 ("/szs/refund")
 - 요청 Header - Authorization
 - 로그인 후에 발급한 토큰을 Authorization에서 전달받아 인증정보 확인
 - 인증된 토큰에 해당하는 유저를 DB에서 조회
 - 각 DB 테이블에서 년도별 합계금액 조회
 - 과세표준, 구간별 산출세액 계산 후 결정세액 금액 확정


      
### 🎄프로젝트 트리
```
   src
├─main                                
│├─java
││└─com
││  └─example
││    └─taxapi
││      │ TaxapiApplication.java           #실행 어플리케이션
││      │      
││      ├─auth
││      │    CustionUserDetailService.java #UserDetail 생성을 위한 service
││      │
││      ├─config
││      │    SecurityConfig.java           #Spring Security 설정 Bean 등록
││      │    SwaggerConfig.java            #Swagger 설정 Bean 등록
││      │
││      ├─controller
││      │    ApiContoller.java             #API 컨트롤러(회원가입, 로그인, 스크래핑, 결정세액 조회)
││      │      
││      ├─dto
││      │    CreditDeductionDto.java       #신용카드 소득공제Dto
││      │    CreditDeductionSumDto.java    #신용카드 소득공제 년도별 합계Dto
││      │    incomeDto.java                #종합소득Dto
││      │    PensionDto.java               #국민연금Dto
││      │    PensionSumDto.java            #국민연금 년도별 합계Dto
││      │    TaxCreditDto.java             #세액공제Dto
││      │    UserLoginDto.java             #로그인 유저Dto
││      │    UserSignupDto.java            #회원가입 유저Dto
││      │
││      ├─jwt
││      │    JwtAuthenticationFilter.java            #JWT 인증 설정
││      │    JwtToken.java                           #JWT vo
││      │    JwtTokenProvider.java                   #토큰 발급, 검증
││      │
││      ├─domain
││      ││───pk
││      ││        CreditDeductionPk.java                  #신용카드 소득공제 테이블 PK
││      ││        IncomePK.java                           #종합소득금액 테이블 PK
││      ││        PensionPk.java                          #국민연금 테이블 PK
││      ││        TaxCreditPk.java                        #세액공제 테이블 PK
││      │    CreditDeduction.java                    #신용카드 소득공제 Entity
││      │    Income.java                             #종합소득금액 Entity
││      │    Member.java                             #유저 Entity
││      │    Pension.java                            #국민연금 Entity
││      │    TaxCredit.java                          #세액공제 Entity
││      │
││      ├─repository
││      │    CreditDeductionRepository.java          #신용카드 소득공제 repository
││      │    IncomeRepository.java                   #종합소득금액 repository
││      │    PensionRepository.java                  #국민연금 repository
││      │    TaxCreditRepository.java                #세액공제 repository
││      │    UserRepository.java                     #유저 repository
││      │      
││      └─service
││           EncryptService.java                     #암,복호화 사용 Service
││           ApiService.java                         #Api 처리 Service Interface
││           APiServiceImpl.java                     #Api 처리 Service
││                      
│└─resources
││  
│├─application.yml                                  #프로젝트 설정
││  
```

![image](https://github.com/user-attachments/assets/8689824f-ed73-4856-a13e-ecb5621a8501)
