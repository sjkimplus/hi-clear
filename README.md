# 🏸hi-clear

-------------------
![image](https://github.com/user-attachments/assets/4938b0b0-1197-4f3c-b746-7e141c1ae030)

> 배드민턴 동호인들이 **정기적 모임**과 **일회성 모임**으로 배드민턴을 칠 수 있도록 관리해주는 서비스


## 서비스 아키텍쳐 & 적용 기술

-----------------------
### 1. 서비스 아키텍쳐

![image](https://github.com/user-attachments/assets/82395ad8-024e-4754-b0e7-a2c43cbab441)

# 2. 적용 기술

**(1). 기본 프레임워크/기술스택**

<div align=left><h2>📚 기술스택</h2></div>

<div><h3>언어 및 프레임워크</h3>
 <div style="display: grid; grid-template-columns: repeat(3, 80px); gap: 0px;">
  <!-- Java -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=java&theme=light" alt="Java Icon" />
    </a>
    <div>Java</div>
  </div>

  <!-- Spring -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=spring&theme=light" alt="Spring Icon" />
    </a>
    <div>Spring</div>
  </div>
</div>

<div><h3>데이터베이스</h3>
 <div style="display: grid; grid-template-columns: repeat(3, 80px); gap: 0px;">
  <!-- MySQL -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=mysql&theme=light" alt="MySQL Icon" />
    </a>
    <div>MySQL</div>
  </div>

  <!-- Redis -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=redis&theme=light" alt="Redis Icon" />
    </a>
    <div>Redis</div>
  </div>

  <!-- Elasticsearch -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=elasticsearch&theme=light" alt="Elasticsearch Icon" />
    </a>
    <div>Elasticsearch</div>
  </div>
</div>


<div><h3>CI/CD 및 형상기억</h3>
 <div style="display: grid; grid-template-columns: repeat(4, 80px); gap: 0px;">
  <!-- Docker -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=docker&theme=light" alt="MySQL Icon" />
    </a>
    <div>Docker</div>
  </div>

  <!-- Githubactions -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=githubactions&theme=light" alt="Redis Icon" />
    </a>
    <div>Githubactions</div>
  </div>

  <!-- Git -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=git&theme=light" alt="Elasticsearch Icon" />
    </a>
    <div>Git</div>
  </div>

  <!-- Github -->
  <div style="text-align: center;">
    <a href="https://skillicons.dev">
      <img src="https://skillicons.dev/icons?i=github&theme=light" alt="Elasticsearch Icon" />
    </a>
    <div>Github</div>
  </div>
</div>

<div>
  <h3>클라우드</h3>
  <div style="display: grid; grid-template-columns: repeat(2, 80px); gap: 0px;">
    <!-- AWS Icon -->
    <div style="text-align: center;">
      <a href="https://skillicons.dev">
        <img src="https://skillicons.dev/icons?i=aws&theme=light" alt="AWS Icon" />
      </a>
      <div>AWS</div>
    </div>
    <div style="display: grid; grid-template-columns: repeat(2, 60px); gap: 10px;">
      <img src="https://img.shields.io/badge/ecs-FF9900?style=for-the-badge&logo=amazon ecs&logoColor=white" alt="ECS Badge">
      <img src="https://img.shields.io/badge/ec2-FF9900?style=for-the-badge&logo=amazon ec2&logoColor=white" alt="EC2 Badge">
      <img src="https://img.shields.io/badge/rds-527FFF?style=for-the-badge&logo=amazon rds&logoColor=white" alt="RDS Badge">
      <img src="https://img.shields.io/badge/s3-569A31?style=for-the-badge&logo=amazon s3&logoColor=white" alt="s3 Badge">
    </div>
  </div>
</div>




- Github
- Java 17
- Spring boot 3.3.4
- Spring Security
- JWT
- Gradle Multi Module
- MySQL
- Spring Data JPA
- Postman
- JUnit

**(2). 핵심 기술스택**

<details>
  <summary>Caching (Redis)</summary>

- Redis는 메모리 기반의 저장소로, 디스크 기반의 DB보다 훨씬 빠른 속도로 로직을 처리하여 데이터의 읽기/쓰기가 빠르기 때문에 Redis를 채택
- 단순한 키값 저장에 집중하고 있는 다른 방법들에 반해 Redis는 다양한 리스트, 세트, 해시 등을 지원하여 대규모 시스템이도 쉽게 확장이 가능하여 Redis를 선택

</details>

<details>
  <summary>알람기능 (SSE, Redis Pub/Sub)</summary>

- SSE
    - 서버의 이벤트를 클라이언트로 보내는 방법으로 Polling, Long-Polling, WebSocket, SSE(Server-Sent-Event) 있습니다.
    - Poliing과 Long-Polling은 클라이언트가 매 번 요청을 하여 서버의 이벤트를 받는 방법이라 자주 요청하게되면 그만큼 서버에 부담이 됩니다.
    - WebSocket은 양방향 통신이라는 장점이 있지만 알림 서비스에서는 꼭 양방향일 필요가 없으며 서버와 클라이언트가 지속적으로 유지하는데 비용이 생기므로 부적절했습니다
    - 하지만 SSE는 HTTP 기반이라는 점이 편리합니다. 구독이라는 시스템으로 한번 요청한 이후로는 서버로부터 단방향, 실시간으로 이벤트를 받을 수 있어 알림 서비스에 최적이라 생각하여 SSE를 채택했습니다.
- Redis Pub/Sub
    - Kafka, RabbitMQ에 비해 설정 및 구현이 간단하여 초기 진입 장벽이 낮아 빠르게 구현할 수 있다는 장점이 있습니다.
    - 여러 서버를 사용할 시 알림 데이터를 Redis에 메시지를 발행함으로써 서버 간의 동기화 문제를 해결할 수 있었습니다.
</details>

<details>
<summary>부하 테스트 (Locust)</summary>

- JMeter보다 복잡한 시나리오 구현을 유연하게 실행할 수 있고 Gatling의 무료버전 보다 더 많은 테스팅 기술을 제공하여 선정
</details>

<details>
<summary>인프라</summary>

- 비교적 소규모 프로젝트인 것과 **AWS**와 Github을 메인으로 프로젝트를 구축하는 것을 고려하여 CI/CD로는 **Github Actions**, 서비스 아키텍처 관련 튤은 모두 AWS에서 선택하였다.
    <details>
        <summary>[Service Architecture 고려사항]</summary>

    - **Amazon ECR vs Docker Hub**

        - **Amazon ECR**
            - **장점**: AWS IAM과 통합되어 AWS 내 보안 관리가 간편하며, 리포지토리 정책을 통해 이미지 접근을 제어할 수 있음. AWS 서비스와의 연동이 쉬움.
            - **단점**: AWS에 종속적이며, AWS 외 다른 환경에서의 사용이 제한적일 수 있음.
        - **Docker Hub**
            - **장점**: 특정 클라우드 제공자에 종속되지 않고, 멀티 클라우드 및 온프레미스 환경에서 유연하게 사용 가능. Docker 계정으로 다양한 CI/CD 도구와 잘 호환됨 (예: CircleCI, TravisCI).
            - **단점**: 별도의 Docker 유저 인증이 필요하며, 무료 플랜은 제한된 private저장소 및 요청 제한이 있어 비용이 추가될 수 있음.
        - **ECS** **vs.** **Kubernetes**
            - **ECS**: AWS에 최적화되어 설정과 관리가 비교적 쉬움. AWS 내 서비스와 통합이 좋고 트래픽양에 따른 자동 확장 지원.
            - **Kubernetes**: 클라우드(AWS)와 독립적이고 확장성이 좋고, 다양한 환경에서 일관된 컨테이너 관리를 제공. 초기 설정과 관리가 복잡할 수 있음.
        - **Elastic Load Balancing Algorithm: Round Robin 알고리즘**
            - 서버간의 성능이 유사하고 처리하는 작업들이 간단하므로 **순서대로 돌아가며 작업**을 나누어주는 라운드로빈 알고리즘 적용.
      </details>

      <details>
          <summary>[CI/CD 고려사항]</summary>

        - Jenkins의 고려점
            - 온프레미스 환경에서 민감한 데이터를 처리해야 하는 경우.
            - 복잡한 CI/CD 워크플로와 다양한 외부 도구 통합이 필요한 경우.
        - GitHub Actions 고려점
            - GitHub를 주요 코드 저장소로 사용하며, CI/CD 설정을 간단히 하고 싶은 경우.
            - 클라우드 기반 워크플로를 선호하며, 별도의 서버 유지보수를 원하지 않는 경우.
   </details>
</details>

# 와이어 프레임

# ERD



## 🎯 프로젝트 주요 기능

-----------------------

### 위치기반 체육관 검색 기능

- **[사용기술]** MySQL Spatial Index(공간인덱스) 적용
- **[도입이유]** 위치기반 인덱스는 일반 인덱스보다 R-Tree 자료구조를 활용한 인덱스가 더 효율적이라고 판단해 적용
- **[사용방법/원리]** 인증유저(AuthUser)의 등록된 위치 기반으로 입력값(거리)에 따른 체육관 목록을 반환
- **[수치/결과]** DB 인덱싱을 적용한 검색기능 최적화(응답속도 576ms → 157ms로 약 3.6배 향상)
- 결과 비교표

![image](https://github.com/user-attachments/assets/95f67cae-d57e-4196-a057-aa8c89a34f3e)

### 유저 매너점수 및 등급(실력) 표현 시스템

- **[사용기술]** Redis를 이용해 분산락과 캐싱을 사용
- **[도입이유]** 분산락을 사용해 동시다발적으로 들어오는 중복된 리뷰 발생을 방지하고, 시스템의 안정성을 높임 / 캐싱과 TTL을 사용하여 데이터베이스에서 불러와 연산하는 과정을 보다 빠르게 개선

### 알림 서비스

- **[사용 기술]** SSE와 Redis Pub/Sub 사용
- **[도입이유]** 여러 사용자에게 실시간으로 사용자와 관련된 모임의 소식(회원가입, 게시글 작성, 댓글 작성 등) 받을 수 있다.
- **[수치/결과]** 클라이언트가  로그인 후 서버를 구독하면 알림을 받을 수 있다.

### 예약 기능

- **[사용기술]**  Redis의 Lock 기능, `@Scheduled` 적용
- **[도입이유]** 분산락을 구현하여 중복 예약 생성 제외, `@Scheduled`를 통해 만료된 데이터 삭제 자동화하여 데이터 관리
- **[수치/결과]** 중복 예약 생성 제외 및 매일 자정에 만료된 데이터 자동 삭제


## 트러블 슈팅

-----------------------

<details>
<summary>인프라</summary>

- 비교적 소규모 프로젝트인 것과 **AWS**와 Github을 메인으로 프로젝트를 구축하는 것을 고려하여 CI/CD로는 **Github Actions**, 서비스 아키텍처 관련 튤은 모두 AWS에서 선택하였다.
    <details>
        <summary>[Service Architecture 고려사항]</summary>
