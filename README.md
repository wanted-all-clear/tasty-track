# TastyTrack

> ## 📝 목차
> 1. [서비스 소개](#서비스-소개)
> 2. [주요 기능](#주요-기능)
> 3. [R&R](#rr)
> 4. [프로젝트 일정](#프로젝트-일정)
> 5. [Github Issue & Jira 를 통한 Task 트래킹 관리 (WBS)](#github-issue--jira-를-통한-task-트래킹-관리-wbs)
> 6. [Discord 이용한 소통 및 PR 알림 봇](#discord-이용한-소통-및-pr-알림-봇)
> 7. [협업 및 커뮤니케이션](#협업-및-커뮤니케이션)
> 8. [프로젝트 환경](#프로젝트-환경)
> 9. [기술 선택 이유](#기술-선택-이유)
> 10. [요구사항 정의서 정리](#요구사항-정의서-정리)
> 11. [API 명세서](#api-명세서)
> 12. [ERD](#erd)
> 13. [트러블 슈팅](#트러블-슈팅)
> 14. [디렉토리 구조](#디렉토리-구조)


<br/>

## 서비스 소개
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
| 담당자       | 담당 업무                                                 |
|:--------------:|----------------------------------------------------------|
| [오예령(팀장)](https://github.com/ohyeryung) | 사용자 기능 구현 (로그인, 회원가입, 회원정보 조회, 회원정보 수정)     |
| [유리빛나](https://github.com/ryuneng)       | 데이터 파이프라인 구축 (서울시 공공데이터 수집, 전처리, 저장, 자동화) |
| [김은정](https://github.com/fkznsha23)       | 맛집 조회 기능 구현 (상세 조회, 위치 기반 맛집 추천, 평가 생성)      |
| [배서진](https://github.com/bsjin1122)       | 맛집 조회 기능 구현 (목록 조회, 지역명 기준 맛집 추천, 시군구 조회)  |

<br>

### 프로젝트 일정
<details>
    <summary><b>프로젝트 과정 타임라인🗓</b></summary>
    - 프로젝트 기간: 2024.08.27 ~ 2024.09.02
    
</details>

<br>

### 협업 및 커뮤니케이션 🗣️ 

<details>
<summary>문서화 작업</summary>
<div markdown="1">
<p align="center">
    <img src="https://github.com/user-attachments/assets/c0cf185c-6c8d-4d0d-94ed-a208d02d3f66" align="center" width="45%">
    <img src="https://github.com/user-attachments/assets/b4b6f352-93a7-4a9d-8491-375868d2f0c6" align="center" width="45%">
</p>
</div>
</details>

<br/>

### Github Issue & Jira 를 통한 Task 트래킹 관리 (WBS) 🏃‍♀️‍➡️ 

<details>
<summary>개발일정 관리</summary>
<div markdown="1">
<img src="https://github.com/user-attachments/assets/718d4ca2-0059-4c69-a89e-b697dbdb0801" alt="Alt text" width="980" height="610"/>

<p align="center">
    <img src="https://github.com/user-attachments/assets/cf2789be-c2aa-46da-aa16-948f6a73807e" align="center" width="40%">
    <img src="https://github.com/user-attachments/assets/3b8c33ac-019d-4f85-be1c-73cbc1b7cb36" align="center" width="40%">
</p>

</div>
</details>

<br/>

### Discord을 활용한 소통 및 PR 알림 봇 🤖 

<details>
<summary>소통 및 PR 알림 확인</summary>
<div markdown="1">

![image](https://github.com/user-attachments/assets/9a329b8b-bec5-4742-b32c-9073edf22f26)
<img src="https://github.com/user-attachments/assets/1c5c0cc7-102b-4924-b0b4-2ba4bd5150e6" alt="Alt text" width="430" height="600"/>

</div>
</details>

<br/>

## 프로젝트 환경

| Stack                                                                | Version            |
|:----------------------------------------------------------------------:|:-----------------:|
| ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)  | Spring Boot 3.3.x |
| ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)    | Gradle 8.8       |
| ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)    | JDK 17           |
| ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)       | MySQL 8.0        |
| ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)    | Redis 6.0        |

<br/>

## 기술 선택 이유

<details>
  <summary><b></b></summary>

</details>

<br>

### 요구사항 정의서 정리


<br/>

### [API 명세서](https://documenter.getpostman.com/view/20456478/2sAXjGcDg4)

 > 자세한 명세는 API 명세서(Postman)를 클릭해 확인 해주세요!

| 대분류   | 기능                  | Http Method | API Path                               | 인증 | 담당자 |
|----------|-----------------------|-------------|----------------------------------------|------|--------|
| 사용자   | 사용자 회원 가입       | `POST`        | /api/users                             | X    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 로그인                 | `POST`        | /api/users/login                       | X    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 회원정보 조회          | `GET`         | /api/users                             | O    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 회원 정보 수정         | `PUT`         | /api/users                             | O    | [오예령](https://github.com/ohyeryung) |
| 사용자   | 위치 기반 맛집 추천 기능 | `POST`        | /api/users/location                    | X    | [김은정](https://github.com/fkznsha23)   |
| 사용자   | AccessToken 재발급     | `POST`        | /api/refresh                           | X    | [오예령](https://github.com/ohyeryung) |
| 맛집 | 맛집 상세 정보 조회    | `POST`        | /api/restaurants/detail                | O    | [김은정](https://github.com/fkznsha23)   |
| 맛집 | 맛집 평가 생성        | `POST`        | /api/reviews                           | O    | [김은정](https://github.com/fkznsha23)   |
| 맛집 | 맛집 목록 조회        | `GET`         | /api/restaurants/list                  | X    | [배서진](https://github.com/bsjin1122)   |
| 맛집 | 지역명 기준 맛집 추천  | `GET`         | /api/restaurants/region?dosi<br>={dosi}&sgg={sgg}&type={type}            | X    | [배서진](https://github.com/bsjin1122)   |
| 맛집 | 시군구 조회           | `GET`         | /api/regions                           | X    | [배서진](https://github.com/bsjin1122)   |
* 데이터 파이프라인은 자동화 시스템을 통해 처리됩니다.



<br>

### ERD


## 트러블 슈팅
<p></p>
<br>

## 디렉토리 구조
<details><summary>디렉토리 구조</summary>

```text

```

<br>
    
```text

``` 
</details>
