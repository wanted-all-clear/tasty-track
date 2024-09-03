# 😋 TastyTrack
<div align="center">
    <img src="https://github.com/user-attachments/assets/cec1e214-a1a6-4bfc-8915-401012e281c3">
</div>

> ## 📝 목차
> 1. [서비스 소개](#-서비스-소개)
> 2. [주요 기능](#주요-기능)
> 3. [R&R](#-rr)
> 4. [프로젝트 일정](#프로젝트-일정)
> 5. [협업 및 커뮤니케이션](#협업-및-커뮤니케이션-%EF%B8%8F)
> 6. [Github Issue & Jira 를 통한 Task 트래킹 관리 (WBS)](#github-issue--jira-를-통한-task-트래킹-관리-wbs-%EF%B8%8F%EF%B8%8F)
> 7. [Discord 를 활용한 소통 및 PR 알림 봇](#discord를-활용한-소통-및-pr-알림-봇-)
> 8. [프로젝트 환경](#%EF%B8%8F-프로젝트-환경)
> 9. [기술 선택 이유](#-기술-선택-이유)
> 10. [요구사항 정의서 정리](#-요구사항-정의서-정리)
> 11. [API 명세서](#-api-명세서)
> 12. [ERD](#%EF%B8%8F-erd)
> 13. [트러블 슈팅](#-트러블-슈팅)
> 14. [고민한 흔적](#-고민한-흔적)
> 15. [디렉토리 구조](#%EF%B8%8F-디렉토리-구조)

<br/>

## 🍣 서비스 소개
- 서울시 음식점 공공데이터를 활용하여 서울의 맛집 목록을 자동으로 업데이트하고, 이를 기반으로 서비스를 제공합니다. **사용자의 위치에 맞춰 맛집 및 메뉴를 추천**함으로써 다양한 음식을 더욱 풍부하게 경험할 수 있도록 돕고, 음식을 좋아하는 사람들 간의 소통과 공유를 지원하는 애플리케이션의 API 서버입니다.

- 🔗 **활용한 공공데이터** | [<a href="https://data.seoul.go.kr/dataList/OA-16094/S/1/datasetView.do">https://data.seoul.go.kr/</a>](https://data.seoul.go.kr/dataList/OA-16094/S/1/datasetView.do)

### 주요 기능

> 1. 유저는 본 사이트에 접속해 회원가입 및 내 위치를 지정합니다.<br>
> 2. **A. 내 위치 기반 맛집추천 = (`내 주변보기`)**<br>
    - `도보` 기준 `1km` 이내의 맛집을 추천합니다.<br>
    - `교통수단` 기준 `5km` 이내의 맛집을 추천합니다.<br>
> 3. **B. 지역명 기준 맛집추천(`특정 지역 보기`)**<br>
    - 지정한 `지명(시군구)` 중심위치 기준 `10km` 이내의 맛집을 추천합니다.<br>
> 4. A, B를 다양한 검색기준 (정렬, 필터링 등)으로 맛집 목록을 확인합니다. (`거리순`, `평점순` , `양식`, `중식`)<br>
> 5. 해당 맛집의 상세정보를 확인할 수 있습니다.<br>
> 6. 원하는 맛집의 평가를 등록할 수 있습니다.

<br/>

### 👩🏻‍💻 R&R
| 담당자                                      | 담당 업무                                                        |
|:-------------------------------------------:|------------------------------------------------------------------|
| [오예령(팀장)](https://github.com/ohyeryung) | 사용자 기능 구현 (로그인, 회원가입, 회원정보 조회, 회원정보 수정)     |
| [유리빛나](https://github.com/ryuneng)       | 데이터 파이프라인 구축 (서울시 공공데이터 수집, 전처리, 저장, 자동화) |
| [김은정](https://github.com/fkznsha23)       | 맛집 조회 기능 구현 (상세 조회, 위치 기반 맛집 추천, 평가 생성)      |
| [배서진](https://github.com/bsjin1122)       | 맛집 조회 기능 구현 (목록 조회, 지역명 기준 맛집 추천, 시군구 조회), 디스코드 점심메뉴 추천  |

<br>

### 프로젝트 일정
<details>
    <summary><b>프로젝트 과정 타임라인 🗓</b></summary><br>
    <img src="https://github.com/user-attachments/assets/04066110-4312-4a57-b1df-a065f3f987fc">
</details>

<br>

### 협업 및 커뮤니케이션 🗣️ 

<details>
<summary>문서화 작업</summary>
<div markdown="1">
    <div>
        <h4>☑️ 1) 회의록</h4>
        <img src="https://github.com/user-attachments/assets/c0cf185c-6c8d-4d0d-94ed-a208d02d3f66" align="center" width="60%">
    </div><br>
    <div>
        <h4>☑️ 2) 개인 Daily TODO</h4>
        <img src="https://github.com/user-attachments/assets/af8b2113-4979-4064-ae7f-5acc74f5c003" align="center" width="60%">
    </div><br>
    <div>
        <h4>☑️ 3) 전체 TODO</h4>
        <img src="https://github.com/user-attachments/assets/b4b6f352-93a7-4a9d-8491-375868d2f0c6" align="center" width="60%">
    </div><br>
</div>
</details>

<br/>

### Github Issue & Jira 를 통한 Task 트래킹 관리 (WBS) 🏃‍♀️‍➡️ 

<details>
<summary>개발일정 관리</summary>
<div markdown="1">
    <div>
        <h4>☑️ 1) Github Issues</h4>
        <img src="https://github.com/user-attachments/assets/718d4ca2-0059-4c69-a89e-b697dbdb0801" align="center" width="70%">
    </div><br>
    <div>
        <h4>☑️ 2) Jira</h4>
        <img src="https://github.com/user-attachments/assets/cf2789be-c2aa-46da-aa16-948f6a73807e" align="center" width="70%">
    </div><br>
    <img width="462" alt="image" src="https://github.com/user-attachments/assets/c8b56429-8c3a-4a7a-a474-39ba7d759255">

</div>
</details>

<br/>

### Discord를 활용한 소통 및 PR 알림 봇 🤖 

<details>
<summary>소통 및 PR 알림 확인</summary>
<div markdown="1">
    <img src="https://github.com/user-attachments/assets/9a329b8b-bec5-4742-b32c-9073edf22f26">
    <img src="https://github.com/user-attachments/assets/1c5c0cc7-102b-4924-b0b4-2ba4bd5150e6" alt="Alt text" width="430" height="600"/>
</div>
</details>

<br/>
<br/>

## 🛠️ 프로젝트 환경

| Stack                                                                                                        | Version           |
|:------------------------------------------------------------------------------------------------------------:|:-----------------:|
| ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) | Spring Boot 3.3.x |
| ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)    | Gradle 8.8       |
| ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)    | JDK 17           |
| ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)       | MySQL 8.0        |
| ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)    | Redis 6.0        |

<br/>

### ✅ 기술 선택 이유

- **SpringSecurity** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/SpringSecurity-%EB%8F%84%EC%9E%85"> WIKI 이동 </a>
- **AccessToken과 RefreshToken** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/AccessToken%EA%B3%BC-RefreshToken"> WIKI 이동 </a>
- **ComplatableFuture** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/ComplatableFuture"> WIKI 이동 </a>

<br>

### 📄 요구사항 정의서 정리
| 대분류 | 기능명               | Controller & Service method        | 담당자                                 |
|--------|----------------------|------------------------------------|----------------------------------------|
| 사용자 | 회원가입              | signup                             | [오예령](https://github.com/ohyeryung)  |
| 사용자 | 로그인                | signin                             |  [오예령](https://github.com/ohyeryung) |
| 사용자 | 설정 업데이트         | updateUserInfo                     | [오예령](https://github.com/ohyeryung)  |
| 사용자 | 정보 조회             | getUserInfo                        | [오예령](https://github.com/ohyeryung)  |
| 사용자   | AccessToken 재발급    | refreshAccessToken               | [오예령](https://github.com/ohyeryung) |
| 사용자 | 위치 기반 맛집 추천    | getRestaurantByUserLocation        | [김은정](https://github.com/fkznsha23)  |
| 맛집   | 맛집 평가 생성         | createReview                      | [김은정](https://github.com/fkznsha23)  |
| 맛집   | 맛집 상세 조회         | getRestaurantById                     | [김은정](https://github.com/fkznsha23)  |
| 맛집   | 맛집 목록 조회         | getRestaurantList                 | [배서진](https://github.122)            |
| 맛집   | 지역명 기준 맛집 추천   | getRestuarantSearchByRegion       | [배서진](https://github.122)            |
| 맛집   | 시군구 조회            | getRegionList                     | [배서진](https://github.122)            |
| 데이터 | 맛집 데이터 수집       | fetchAndSaveInitDatas              |  [유리빛나](https://github.com/ryuneng) |
| 데이터 | 맛집 데이터 전처리     | preprocessingAndSaveInitRestaurant | [유리빛나](https://github.com/ryuneng)  |
| 데이터 | 맛집 데이터 저장       | saveRestaurantsFromRawRestaurants  | [유리빛나](https://github.com/ryuneng)  |
| 데이터 | 맛집 데이터 자동화     | fetchAndSaveUpdatedDatas           | [유리빛나](https://github.com/ryuneng)  |

<br/>

### 서비스 아키텍처


<img width="724" alt="image" src="https://github.com/user-attachments/assets/6067de34-e195-4f99-9b62-1e2fbe4527fa">

<br/>


### 🧾 API 명세서

 > 자세한 명세는 <a href="https://documenter.getpostman.com/view/20456478/2sAXjM4Xgs">🔗여기</a> 를 클릭해주세요! `(Postman API)`

| 대분류   | 기능                  | Http Method | API Path                               | 토큰 검증 여부 | 담당자 |
|----------|-----------------------|-------------|----------------------------------------|------|--------|
| 사용자   | 회원 가입              | `POST`        | /api/users                             | X    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 로그인                 | `POST`        | /api/users/login                       | X    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 회원정보 조회          | `GET`         | /api/users                             | O    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 회원 정보 수정         | `PUT`         | /api/users                             | O    | [오예령](https://github.com/ohyeryung) |
| 사용자   | AccessToken 재발급    | `POST`        | /api/refresh                          | X    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 위치 기반 맛집 추천  | `POST`        | /api/users/location                    | X    | [김은정](https://github.com/fkznsha23)   |
| 맛집 | 맛집 상세 정보 조회    | `GET`        | /api/restaurants/{id}                | O    | [김은정](https://github.com/fkznsha23)   |
| 맛집 | 맛집 평가 생성        | `POST`        | /api/reviews                           | O    | [김은정](https://github.com/fkznsha23)   |
| 맛집 | 맛집 목록 조회        | `GET`         | /api/restaurants/list                  | X    | [배서진](https://github.com/bsjin1122)   |
| 맛집 | 지역명 기준 맛집 추천  | `GET`         | /api/restaurants/region?dosi<br>={dosi}&sgg={sgg}&type={type}            | X    | [배서진](https://github.com/bsjin1122)   |
| 맛집 | 시군구 조회           | `GET`         | /api/regions                           | X    | [배서진](https://github.com/bsjin1122)   |
* 데이터 파이프라인은 자동화 시스템을 통해 처리됩니다.

<br>

## ⛓️ ERD
<img src="https://github.com/user-attachments/assets/feca69c7-e72e-4e58-8c41-dbdc79d8026b" width="80%">

<br>
<br>

## 💥 트러블 슈팅
- **위도 경도 데이터 타입 통일 과정 중 발생한 문제** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/%EC%9C%84%EB%8F%84-%EA%B2%BD%EB%8F%84-%EB%8D%B0%EC%9D%B4%ED%84%B0-%ED%83%80%EC%9E%85-%ED%86%B5%EC%9D%BC-%EA%B3%BC%EC%A0%95-%EC%A4%91-%EB%B0%9C%EC%83%9D%ED%95%9C-%EB%AC%B8%EC%A0%9C"> WIKI 이동 </a>
- **토큰 검증을 필요로하지 않는 API 호출시 발생한 문제** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/%ED%86%A0%ED%81%B0-%EA%B2%80%EC%A6%9D%EC%9D%84-%ED%95%84%EC%9A%94%EB%A1%9C%ED%95%98%EC%A7%80-%EC%95%8A%EB%8A%94-API-%ED%98%B8%EC%B6%9C%EC%8B%9C-%EB%B0%9C%EC%83%9D%ED%95%9C-%EB%AC%B8%EC%A0%9C"> WIKI 이동 </a>
- **토큰 검증 테스트 시 발생한 문제** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/%ED%86%A0%ED%81%B0-%EA%B2%80%EC%A6%9D-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%8B%9C-%EB%B0%9C%EC%83%9D%ED%95%9C-%EB%AC%B8%EC%A0%9C"> WIKI 이동 </a>
- **Invalid use of argument matchers!** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/Invalid-use-of-argument-matchers!"> WIKI 이동 </a>

<br>

## 🤔 고민한 흔적
- **프로젝트 서버 분리 고민** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%84%9C%EB%B2%84-%EB%B6%84%EB%A6%AC-%EA%B3%A0%EB%AF%BC"> WIKI 이동 </a>
- **Entity 클래스의 PK 필드 타입 (Long vs int)** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/Entity-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%9D%98-PK-%ED%95%84%EB%93%9C-%ED%83%80%EC%9E%85-(Long-vs-int)"> WIKI 이동 </a>
- **위도, 경도의 데이터 타입 (Double vs Decimal)** - <a href="https://github.com/wanted-all-clear/tasty-track/wiki/%EC%9C%84%EB%8F%84,-%EA%B2%BD%EB%8F%84%EC%9D%98-%EB%8D%B0%EC%9D%B4%ED%84%B0-%ED%83%80%EC%9E%85-(Double-vs-Decimal)"> WIKI 이동 </a>
- **디스코드 점심 메뉴 추천 알림 구현하기** <a href="https://github.com/wanted-all-clear/tasty-track/wiki/%EB%94%94%EC%8A%A4%EC%BD%94%EB%93%9C-%EC%A0%90%EC%8B%AC-%EB%A9%94%EB%89%B4-%EC%B6%94%EC%B2%9C-%EC%95%8C%EB%A6%BC-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0"> WIKI 이동</a>
 
<br>

## 🗂️ 디렉토리 구조
<details><summary>직관적인 구조 파악과 관리를 위해 <b>도메인형 구조</b>를 채택하였습니다. <b>(더보기)</b></summary>

<h3>🗂️ 전체 구조</h3>
    
```text
    Folder PATH listing
Volume serial number is D6B6-EE51
C:.
│   .env
│   .gitignore
│   .gitmessage.txt
│   build.gradle
│   docker-compose.yml
│   Dockerfile
│   gradlew
│   gradlew.bat
│   README.md
│   settings.gradle
│   structure.txt
│   
├───.github
│   │   PULL_REQUEST_TEMPLATE.md
│   ├───ISSUE_TEMPLATE
│   │       issue_form.yml
│   └───workflows
│           create-jira-issue.yml
├───.gradle
│
├───build
├───gradle
│   └───wrapper
│           gradle-wrapper.jar
│           gradle-wrapper.properties
└───src
    ├───main
    │   ├───java
    │   │   └───com
    │   │       └───allclear
    │   │           └───tastytrack
    │   │               │   TastytrackApplication.java
    │   │               │   
    │   │               ├───domain
    │   │               │   ├───auth
    │   │               │   │   └───token
    │   │               │   ├───region
    │   │               │   │   ├───controller
    │   │               │   │   ├───dto
    │   │               │   │   ├───entity
    │   │               │   │   ├───repository
    │   │               │   │   └───service
    │   │               │   │           
    │   │               │   ├───restaurant
    │   │               │   │   ├───controller
    │   │               │   │   ├───coordinate
    │   │               │   │   │   ├───dto
    │   │               │   │   │   └───service
    │   │               │   │   ├───dto
    │   │               │   │   ├───entity
    │   │               │   │   ├───repository
    │   │               │   │   └───service
    │   │               │   ├───review
    │   │               │   │   ├───controller
    │   │               │   │   ├───dto
    │   │               │   │   ├───entity
    │   │               │   │   ├───repository
    │   │               │   │   └───service
    │   │               │   └───user
    │   │               │       ├───controller
    │   │               │       ├───dto
    │   │               │       ├───entity
    │   │               │       ├───repository
    │   │               │       └───service
    │   │               │               
    │   │               └───global
    │   │                   ├───config
    │   │                   ├───domain
    │   │                   ├───entity
    │   │                   └───exception
    │   │                       └───handler
    │   └───resources
    │       │   application.yml
    │       └───db
    │               region.sql
    └───test
        └───java
            └───com
                └───allclear
                    └───tastytrack
                        │   TastytrackApplicationTests.java
                        ├───domain
                        │   ├───region
                        │   │   └───service
                        │   │           RegionServiceImplTest.java
                        │   └───user
                        │       ├───controller
                        │       │       UserControllerTest.java
                        │       └───service
                        │               TokenVerifyTest.java
                        │               UserServiceImplTest.java
                        ├───reivew
                        │       ReviewControllerTest.java
                        │       ReviewServiceImplTest.java
                        └───restaurant
                                RestaurantControllerTest.java
                                RestaurantServiceImplTest.java                                

```

<br>

<h3>📂 상세 구조</h3>

```text
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂allclear
 ┃ ┃ ┃ ┃ ┗ 📂tastytrack
 ┃ ┃ ┃ ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂auth
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂token
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtTokenUtils.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RefreshToken.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RefreshTokenCleanupScheduler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RefreshTokenManager.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RefreshTokenRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TokenProvider.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtAuthenticationFilter.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserAuth.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserAuthImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserDetailsImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserDetailsServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂region
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RegionController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RegionGroupResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RegionResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Region.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RegionRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RegionService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RegionServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂restaurant
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RestaurantController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂coordinate
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Coordinate.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CoordinateAddress.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CoordinateMeta.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CoordinateResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CoordinateRoadAddress.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Documents.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CoordinateService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CoordinateServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜LocalData.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜LocalDataResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RawRestaurantResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RestaurantDetail.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RestaurantListRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RestaurantSearch.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Result.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RawRestaurant.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Restaurant.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RawRestaurantRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RestaurantRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DataProcessingService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RawDataService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RestaurantService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RestaurantServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ReviewController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReviewRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ReviewResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Review.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ReviewRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReviewService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ReviewServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TokenController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜LoginRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserCreateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserInfo.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserLocationInfo.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserUpdateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜User.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂global
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JpaConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SwaggerConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜WebSecurityConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Timestamped.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂handler
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RestApiExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CustomException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorCode.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ErrorResponse.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜TastytrackApplication.java
 ┃ ┗ 📂resources
 ┃ ┃ ┣ 📂db
 ┃ ┃ ┃ ┗ 📜region.sql
 ┃ ┃ ┗ 📜application.yml
 ┗ 📂test
 ┃ ┗ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂allclear
 ┃ ┃ ┃ ┃ ┗ 📂tastytrack
 ┃ ┃ ┃ ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂region
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RegionServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TokenVerifyTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂reivew
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReviewControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ReviewServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂restaurant
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RestaurantControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RestaurantServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜TastytrackApplicationTests.java
```
</details>
