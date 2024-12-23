# 🏸Hi-Clear(배드민턴 모임 및 코트 예약 서비스)

## [🔤 English Version README link 🔗](README-en.md)

-------------------
<img src="https://github.com/user-attachments/assets/a607b045-d29b-471b-a867-c7703e728ea1" />
<br>

## 📚 목차

-----------------------
1. 프로젝트 개요
2. 도메인 및 설계문서
2. 서비스 아카텍처
3. 핵심기능
4. 트러블슈팅

## 📝 프로젝트 개요

-----------------------
<h3>프로젝트 기간 24.10.21 ~ 24.11.21(1개월)</h3>
<h3>BE - 5명</h3>
<h3>서비스 개요</h3>

- 배드민턴 동호인들이 __정기적 모임__ 과 __일회성 모임__ 으로 배드민턴을 칠 수 있도록 관리해주는 서비스
- __위치기반 체육관, 번개, 클럽 조회기능__ 제공
- 사장님들은 체육관을 등록하고 __코트 예약을__ 받고, 유저들은 사설 체육관 예약가능
- 유저들간 평가를 통해 __실력 및 매너점수 평가__ 기능 제공


## 🖋️ 프로젝트 도메인 및 와이어프레임

-----------------------

- [도메인](http://backend.8-lay-hi-clear.com/)

- [API명세서 링크](https://teamsparta.notion.site/a602640d708e43e6ae316434166dd6f2?v=d3e1ab06b21a40acbd6a2c651bc639ec)  

- [와이어프레임 링크](https://www.figma.com/design/rCMVjHvTEuh08lJ5f1kN2Q/8%EB%A0%88%EC%9D%B4-%EC%99%80%EC%9D%B4%EC%96%B4%ED%94%84%EB%A0%88%EC%9E%84?node-id=0-1&t=ySMEWBCA1OlOHPKA-1)
  
  ![image](https://github.com/user-attachments/assets/242642f1-3e34-4a92-b237-8ebd70bf21be)

- ERD

  <img src="./images/ERD.png" alt="erd" />



## 👨🏻‍🎨 팀원 소개

-----------------------
| 사진                                                                                      | 이름 | 담당 도메인                          | 블로그 링크         | GitHub 링크                                |
|-----------------------------------------------------------------------------------------|------|---------------------------------|----------------|------------------------------------------|
| ![김성주](https://github.com/user-attachments/assets/a3461cec-3b5c-483d-b38c-216d00aee5b3) | 김성주 | 번개, 참여자                       | [블로그](https://velog.io/@sjkimplus09/posts) | [GitHub](https://github.com/sjkimplus)   |
| ![남태혁](https://github.com/user-attachments/assets/f843314b-6991-46bc-a4c2-9e39126497ed) | 남태혁 | 알림, <br/>모임, 모임멤버,<br/> 댓글, 대댓글 | [블로그](https://navyth91.tistory.com/) | [GitHub](https://github.com/taehyeokNam) |
| ![정예지](https://github.com/user-attachments/assets/264328c5-266c-4222-8268-b80b74da983e) | 정예지 | 유저 리뷰, 좋아요,<br/>모임 게시글          | [블로그](https://codingbykugom.tistory.com/) | [GitHub](https://github.com/KUGOM)       |
| ![윤지현](https://github.com/user-attachments/assets/316845c9-0be2-4900-abd0-03406513e492) | 윤지현 | 모임일정, 예약                        | [블로그](https://velog.io/@jhy1/posts) | [GitHub](github.com/KangDongJoon)        |
| ![강동준](https://github.com/user-attachments/assets/d297c605-d6df-4885-b9e9-c6e993c2986e) | 강동준 | 유저, 체유관,<br/>코트, 코트시간           | [블로그](https://djhelloworld.tistory.com/) | [GitHub](https://github.com/JH1Yoon)     |



## 🤖 적용 기술

-----------------------
<div>
<h3>언어 & 프레임워크</h3>
      <div>
        <img src="https://img.shields.io/badge/java-1E8CBE.svg?style=for-the-badge&logo=java&logoColor=white">
        <img src="https://img.shields.io/badge/spring boot-%236DB33F.svg?style=for-the-badge&logo=spring boot&logoColor=white">
        <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=spring security&logoColor=white" alt="spring security Badge">
        <img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=json web tokens&logoColor=white" alt="jwt Badge">
        <img src="https://img.shields.io/badge/jpa-527FFF?style=for-the-badge&logo=jpa&logoColor=white" alt="jpa Badge">
      </div>
<h3>데이터베이스</h3>
      <div>
        <img src="https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white">
        <img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">
        <img src="https://img.shields.io/badge/-ElasticSearch-005571?style=for-the-badge&logo=elasticsearch">
      </div>
<h3>CI / CD</h3>
      <div>
        <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white">
        <img src="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white">
      </div>
<h3>클라우드</h3>
      <div>
        <img src="https://img.shields.io/badge/ecs-FF9900?style=for-the-badge&logo=amazon ecs&logoColor=white" alt="ECS Badge">
        <img src="https://img.shields.io/badge/ec2-FF9900?style=for-the-badge&logo=amazon ec2&logoColor=white" alt="EC2 Badge">
        <img src="https://img.shields.io/badge/ECR-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white" alt="ECR Badge">
        <img src="https://img.shields.io/badge/loadbalancing-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white" alt="awselasticloadbalancing Badge">
        <img src="https://img.shields.io/badge/route53-8C4FFF.svg?style=for-the-badge&logo=amazonroute53&logoColor=white">
        <img src="https://img.shields.io/badge/rds-527FFF?style=for-the-badge&logo=amazon rds&logoColor=white" alt="RDS Badge">
        <img src="https://img.shields.io/badge/s3-569A31?style=for-the-badge&logo=amazon s3&logoColor=white" alt="S3 Badge">
      </div>
<h3>테스트</h3>
      <div>
        <img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="junit5 Badge">
        <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white" alt="postman Badge">
        <img src="https://img.shields.io/badge/locust-006600?style=for-the-badge&logo=locust&logoColor=white" alt="locust Badge">
      </div>
</div>

## 🖼️ 서비스 아키텍쳐 및 CI/CD

-----------------------

### [요약]
1. 깃허브 main 브랜치에 코드를 올리면 workflow 파일을 통해 자동으로 build가 되고, 도커 이미지를 생성
2. 만든 도커 이미를 ECR ‘latest’라는 이미지 테그를 붙여 푸시
3. ECS(fargate)는 workflow에서 지정한 task definition에 따라서 도커 이미지를 pull 해와 배포
4. 유저 회원가입에서 생성되는 이미지는 S3 버켓에서, 메인 데이터 베이스 (mySQL) 관리는 RDS 에서, 그리고 분산락과 캐싱에서 사용하는 redis는 별도의 EC2에서 관리

### [Service Architecture 고려사항]
- __Amazon ECR vs Docker Hub__
    - __Amazon ECR__
        - 장점: AWS IAM과 통합되어 AWS 내 보안 관리가 간편하며, 리포지토리 정책을 통해 이미지 접근을 제어할 수 있음. AWS 서비스와의 연동이 쉬움.
        - 단점: AWS에 종속적이며, AWS 외 다른 환경에서의 사용이 제한적일 수 있음.
    - __Docker Hub__
        - 장점: 특정 클라우드 제공자에 종속되지 않고, 멀티 클라우드 및 온프레미스 환경에서 유연하게 사용 가능. Docker 계정으로 다양한 CI/CD 도구와 잘 호환됨 (예: CircleCI, TravisCI).
        - 단점: 별도의 Docker 유저 인증이 필요하며, 무료 플랜은 제한된 private저장소 및 요청 제한이 있어 비용이 추가될 수 있음.
- __ECS__ __vs.__ __Kubernetes__
    - __ECS__: AWS에 최적화되어 설정과 관리가 비교적 쉬움. AWS 내 서비스와 통합이 좋고 트래픽양에 따른 자동 확장 지원.
    - __Kubernetes__: 클라우드(AWS)와 독립적이고 확장성이 좋고, 다양한 환경에서 일관된 컨테이너 관리를 제공. 초기 설정과 관리가 복잡할 수 있음.
- __Elastic Load Balancing Algorithm: Round Robin 알고리즘__
    - 서버간의 성능이 유사하고 처리하는 작업들이 간단하므로 __순서대로 돌아가며 작업__을 나누어주는 라운드로빈 알고리즘 적용.
### [CI/CD 고려사항]
- __Jenkins vs GitHub Actions__
  - __Jenkins__
      - 온프레미스 환경에서 민감한 데이터를 처리해야 하는 경우 사용에 용이.
      - 복잡한 CI/CD 워크플로와 다양한 외부 도구 통합이 필요한 경우에 용이.
  - __GitHub Actions__
      - GitHub를 주요 코드 저장소로 사용하며, CI/CD 설정을 간단히 하고 싶은 경우에 용이.
      - 클라우드 기반 워크플로를 선호하며, 별도의 서버 유지보수를 할 필요가 없다.
### [결론]
- 소규모 프로젝트인 것과 AWS와 Github을 메인으로 프로젝트를 구축하는 것을 고려하여 CI/CD로는 Github Actions, 서비스 아키텍처 관련 튤은 모두 AWS에서 사용

  <img src="./images/service_architecture.png" alt="service_architecture" width="1000" />

## 🛎️ 핵심 기능

-----------------------

<h3>공간 인덱스를 활용한 검색 기능 향상</h3>

- __[사용기술]__ MySQL의 최소 경계 사각형을 기준으로 구분하는 R-Tree 자료구조 기반의 공간인덱스 사용
- __[도입이유]__
- double값으로 좌표를 받고 계산하게되면 모든 데이터에대해 복잡한 수식을 계산
- 거리계산의 경우 인덱스 미적용 성능저하 발생
- __[수치/결과]__
- 응답속도 567ms → 157ms __(260% 향상)__
- 부하량 증가 시 10953ms → 165ms __(6500% 향상)__

  <img src="https://github.com/user-attachments/assets/059e5a4f-2712-4de3-8431-cf9515b6a60f" width="650">

<h3>Elastic Search를 활용한 번개검색</h3>

- __[사용기술]__ Elastic Search 와 nori 형태소 분석 플러그인
- __[도입이유]__ 번개 제목 및 주소로 유저가 검색시 더 빠른 결과 값을 가져오기 위해
- __[도입방식]__ <br>
  elastic search의 효율을 극대화 하기 위해 index 필터를 여러게 적용시켰다. 감탄사와 부사를 제외 시키고,
  번개들 제목에서 의미가 없는 ‘번개’ 및 유사어들을 stopwords로 등록 시켰다. 또한 nori_tokenizer가 단어로
  캐치하지 못한 ‘민턴’ 과 ‘배민’이라는 단어들을  (‘배드민턴’의 줄인 말)  synonym으로 등록해 주었다.

  <img src="./images/meeting_nori.png" alt="Meeting Nori" width="500" />
- __[수치/결과]__ <br>
  '/v1/search' API가 기본 JPQL %like% 쿼리를 사용한 검색이고
  '/v2/search' API가 Elastic Search를 도입한 쿼리이다.<br>
  Elastic Search의 도입으로 검색 쿼리의 속도가 약 30% 향상되었다.

  <img src="./images/meeting_es_chart.png" alt="Meeting Nori" width="600" /> 

<h3>유저 리뷰 생성 분산락</h3>

- __[사용기술]__ Redis를 사용한 분산락
- __[도입이유]__ 분산락을 사용하여 프로그램이 경쟁 상태에 들어가는것을 방지하고 데이터의 정합성을 유지, 서버 부하를 관리 하기 위해
- __[수치/결과]__ 중복 요청이 방지되어 처리시간이 약 __[83% 증가]__ 하였으며, 리뷰 중복 생성이 감소하였습니다.
- <u>분산락 도입 전</u>
  <img src="https://github.com/user-attachments/assets/6293142e-c578-4079-bf65-d54783eeee0a" style="margin-bottom: 10px;">
- <u>분산락 도입 후</u>
  <img src="https://github.com/user-attachments/assets/2e0e0557-9388-405a-9376-a7f21e8487e4" style="margin-bottom: 10px;">
- <u>RPS 비교</u>

  <img src="https://github.com/user-attachments/assets/ce329118-c10d-4640-9ace-3ce52170fae8" width="400">

<h3>캐싱을 적용한 유저 리뷰 서비스</h3>

- __[사용기술]__ Redis 캐싱
- __[도입이유]__ Redis의 캐싱을 사용하여 불필요한 DB 접근을 방지하여 처리 속도를 크게 향상하고 서버의 부화를 최소화 하기 위해
- __[수치/결과]__ 캐시를 사용함으로써 조회 기능의 응답 시간이 약 __[91% 감소]__ , DB에 걸리는 부하가 감소하였습니다.
- <u>캐싱 도입 전</u>
  <img src="https://github.com/user-attachments/assets/220b911f-1cc5-4537-88f9-34bd1a30a42d" style="margin-bottom: 10px;">
- <u>캐싱 도입 후</u>
  <img src="https://github.com/user-attachments/assets/0373487d-c311-46dc-98c8-6ab9478d82be" style="margin-bottom: 10px;">
- <u>응답 시간</u>

  <img src="https://github.com/user-attachments/assets/732dd939-eeee-4646-b7e0-6d68e10f9aac">
  

<h3>예약 동시성 제어</h3>

- __[사용기술]__  Redis의 SETNX, EXPIRE 기능
- __[도입이유]__ 분산락을 구현하여 동시에 여러 명의 사람들이 예약하려고 할 때 중복 예약 생엉을 제외하기 위해서
- __[수치/결과]__ 분산락을 이용했을 때 **예약 1개만 생성**되고(**2653개 요청, 2652개 실패, 1개 성공**),
    락을 걸기 전보다 RPS(초당 처리할 수 있는 요청 수)가 **149.8->168.9**로 향상되었다.

  ![Requests, Fails, Current RPS](https://github.com/user-attachments/assets/5498cba4-b4e2-40a4-85cf-4a1539831cf4)

<h3>SSE 알림 기능</h3>

- __[사용기술]__
  - SSE : 클라이언트에게 실시간 및 단방향으로 이벤트를 푸시
  - Redis Pub/Sub : 메시지 브로커 역할을 하여 클라이언트와 서버 간의 이벤트를 효율적으로 전달


- __[도입이유]__ 
  - Redis Pub/Sub을 통해 여러 서버가 메시지를 쉽게 공유가 가능하고 서버 간의 메시지 전달을 비동기적으로 처리하며
    클라이언트가 실시간으로 메시지를 전달받을 수 있습니다.

  
- __[수치/결과]__
  - SSE만 사용했을 때보다 Redis를 함께 사용했을 때 1000명 이상의 클라이언트가 접속 시 약 __30~40%__ 성능 향상이 있었습니다. 
  - 중위값
  
    <img src="https://github.com/user-attachments/assets/c838f99e-b72e-4b23-9d90-34bbb9e0a7be" style="margin-bottom: 20px;">

  - 상위 95%
  
    <img src="https://github.com/user-attachments/assets/ab515827-2c8e-4c3f-b113-dfab944a94e5">

<h3>지오코딩</h3>

- __[사용기술]__ 지오코딩
- __[도입이유]__ 사용자가 특정 좌표값을 알고 입력하기 어려움(주소 입력)
- __[수치/결과]__

  - 잘못된 주소 입력

    <img src="https://github.com/user-attachments/assets/5cb0a41e-ec64-4ea4-8e35-0111cc007722" width="400" style="margin-bottom: 10px;">
    
  - 올바른 주소 입력
    
    <img src="https://github.com/user-attachments/assets/6ca96487-1e80-451f-9dc3-7744d164356e" width="400">
    <img src="https://github.com/user-attachments/assets/447f1172-e726-4bd8-a578-91e3e7fbab9d">

## 트러블슈팅(트러블 제목 클릭하여 자세히 보기)

-----------------------
<details>
<summary><strong>[CI/CD] Task Fail Error</strong></summary>

__[요약]__
- <u>도커 이미지를 못찾는 오류</u>: workflow파일에서 docker image를 생성할 때 테그이름과 task deifnition에서 찾는 도커 이미지의 테그 이름을 일치 시켜서 오류 해결
- <u>task definition을 못찾는 오류</u>: AWS ECS에서 최신 task definition을 직접 다운받는 shell command 를 찾아서 해결

__[에러 상황 1]__ <br>
workflow 파일로 deploy 과정 중 테스크가 시작후, 모두 fail, 향후 재시도후 __‘Max attempts exceeded’__ 에러 발생
  <img src="./images/cicd_error1.png" alt="cicd_error1" width="800" />

__[원인 분석 1]__ <br>
ECR에 있는 이미지를 가져오는데 실패를 한것이라 생각하여, image tag를 ‘latest’로 수정 및 task definition에서도 ‘latest’라는 image tag로 이미지를 찾도록 수정
또한, 더 이상 task definition을 직접적인 shell command로 부르지 않고 다시 workflow command를 사용하는 방식으로 수정

  <img src="./images/cicd_solve1.png" alt="cicd_solve1" width="600" />

__[에러 상황 2]__ <br>
수정후, AWS ECS (Elastic Container Service)에서 사용하는 task definition 파일이 지정된 경로에 없다는 에러가 발생

  <img src="./images/cicd_error2.png" alt="cicd_error2" width="800" />

__[원인 분석 2]__ <br>
task definition을 찾을 수 있도록 revision번호(18)를 command에 포함

  <img src="./images/cicd_solve2.png" alt="cicd_solve2" width="600" />

__[에러 상황 3]__ <br>
revision 번호가 표기된 task definition도 못찾는 에러발생
<img src="./images/cicd_error3.png" alt="cicd_error3" width="800" />

__[원인 분석 3]__ <br>
  task definition을 찾을 수 있도록 revision번호(18)를 command에 포함
  공식 홈페이지를 찾아보니 task definition을 리포지토리에 저장하지 않을 경우 AWS에서 다운해가는 shell command 를 쓰면된다고 하여서, 다음과 같이 수정.
```angular2html
- name: Download task definition
  run: |
    aws ecs describe-task-definition --task-definition my-task-definition-family --query taskDefinition > task-definition.json
```
__[Trouble shooting 성공!]__ <br>
<img src="./images/cicd_success.png" alt="cicd_success" width="800" />
</details>


<details>
  <summary><strong>인덱스 적용과정</strong></summary>

- __[문제]__
  - MySQL 공간함수를 사용하여 인덱스를 적용시켜 검색기능을 향샹시키고자함
  - ST_Distance_Sphere를 사용했을 때 인덱스가 적용되지 않음
  - <img src="https://github.com/user-attachments/assets/cab2b659-6d15-4680-97e2-f2251612c3f6" width="600">
- __[과정]__
  - R-Tree 자료구조 기반 공간 인덱스 사용
  - 쿼리 실행계획을 사용하여 인덱스 적용 확인
  - ST_Distance_Sphere를 사용하여 모든 데이터를 필터링 한 후 결과 반환
  - ST_Contains를 사용하여 인덱싱하는 전략 구성
  - <img src="https://github.com/user-attachments/assets/fae5de7e-4f07-4cd7-a654-40878d1a1075" width="600">
- __[결과]__
- 인덱싱 적용 후 응답속도 __최소 260%__ 에서 부하량 증가 시 __최대 6500%__ 향상으로 성능차이 극대화

  <img src="https://github.com/user-attachments/assets/059e5a4f-2712-4de3-8431-cf9515b6a60f" width="450">

</details>


<details>
  <summary><strong>유저리뷰 분산락</strong></summary>

- __[문제]__
  - 리뷰 생성시 중복되는 리뷰 발생을 줄이고자 분산락을 적용시키고자 함
  - 분산락을 걸었으나, 테스트 결과 충복되는 요청이 2~3건 발생하는것을 발견
- __[과정]__
  - 락을 획득하고 해제하는 과정이 너무 빠르게 진행되어 일부 요청이 락을 받지 않은 채 통과되고 있다는 사실을 확인
  - 락 획득 - 해제 과정에서 텀을 주기위해 Thread.sleep(100);을 이용

  - <img src="https://github.com/user-attachments/assets/2d8df524-9515-41d4-82b3-396df80d9e05">
- __[결과]__
  - 분산락 적용 후 처리시간 성능이 약 83% 증가하여 성능 극대화
   <img src="https://github.com/user-attachments/assets/ce329118-c10d-4640-9ace-3ce52170fae8" width="400">


</details>
<br>

## ✏️ 회고

------
 
### 강동준
- DB인덱싱에 대해 심도있게 학습하고 적용 할 수 있어서 좋았습니다, Elasticsearch를 적용시켜봤으나 기초적인 수준으로만 적용해봐서 아쉬웠습니다. 다음번에는 조금 Elastcisearch를 고도화하여 검색 기능의 완성도를 높여보고싶습니다. 
### 김성주
- CI/CD 관련하여 MSA 구조를 가져가지 못한 것 이 아쉽다. 하이클리어 어플이 다양한 서비스를 제공하는 만큼 사장님의 체육과 관리, 모임관리, 번개관리, 유저예약관리 등으로 서버를 세분화 해서 관리하는 아키텍처를 가져으갔면 더 좋았을 것 같다.
### 남태혁
-  알림 서비스를 구현하면서 SSE와 메시지큐, Redis Pub/SUb에 대한 내용 정리와 제가 구축하려는 서비스의 흐름, 구조를 명확히 이해하지 못하기도 했으며
   트러블 슈팅과 코드 리팩토링이 두려워 실제 코드 작성 시작이 늦어졌습니다.
-  계획했던것 보다 구현이 너무 오래 걸렸고 다시 하게된다면 공부한 내용과 내가 구현하려는 서비스의 흐름과 구조를 잘 정리하고 실제 구현 시작을  빠르게 시작하여
   시행착오를 두려워하지 않고 잘 기록해보고싶습니다. 그리고 시도해보지 못한 RabbitMQ와 Kafka 적용을 도전해보고싶습니다.

### 정예지
- Redis를 사용하여 분산락을 적용했을때, 프로젝트의 특성상 무거운 Redisson보다는 가볍고 성능이 좋은 Lettuce를 사용하였는데 기회가 된다면 Redisson을 활용해보고 싶습니다.
### 윤지현
- 동시성 제어를 하기 위해 분산락을 적용했는데 DB 조회할 때 캐시를 이용하여 주 조회되는 데이터를 메모리에 저장하여, 반복적인 DB 조회를 줄여 성능을 향상시키고 싶습니다.

