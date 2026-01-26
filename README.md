# Mini Shop

<div align="center">
  <img width="300" height="300" alt="minishop_logo" src="https://github.com/user-attachments/assets/7343ddb3-d12e-4bcc-99e7-bdc73c79e7fb" />
</div>

## 🚀프로젝트 소개

API 개발 능력, 클라우드 인프라 구축 경험, CI/CD 자동화 경험, 부하 테스트를 통한 성능 개선 경험을 쌓기 위해 진행하였고,

Spring Boot 기반 C2C 커머스 API 서버를 개발하고, 부하 테스트를 통해 인프라 확장 없이 애플리케이션 레벨 최적화만으로 성능 병목을 개선한 개인 프로젝트입니다.

<hr>

### 💡프로젝트 과정
- [프로젝트 과정 정리 Notion](https://young-hair-9ab.notion.site/Spring-Boot-JPA-REST-API-21f86702809c806c8d15e93f1f5f4450?source=copy_link)

<hr>

### 📱개발 기간

- 2025.07.01 ~ 성능 개선 진행 중
- 이후 로컬 환경과 클라우드(AWS) 환경에서 모니터링 & 성능 개선 진행 중

<hr>

### 🛠️기술 스택

- **Environment**
  - IntelliJ IDEA
  - GitHub
  - Notion
- **Backend**
  - Java 21
  - Spring Boot 3.x
  - Spring Security 6.x
  - Spring Data JPA
  - JWT Authentication
- **Database**
  - H2 Database(개발 환경)
  - MySQL 8.x (AWS RDS - 운영 환경, 부하 테스트)
- **Infra & DevOps**
  - AWS
  - Docker
  - GitHub Actions
  - Terraform 1.x

<hr>

### ✨주요 기능

- JWT 기반 회원 인증/인가
  - 회원 가입, 로그인, JWT Access/Refresh Token 발급
- 상품 등록 및 수정 기능
  - 판매자가 상품을 등록하고 관리할 수 있는 기능 제공
- 상품 주문 기능
  - 구매자가 상품을 주문하고 주문 내역을 조회할 수 있는 기능 제공

<hr>

### 📦아키텍쳐

#### 디렉토리 구조

```
├──src/
|   ├── docs/  # API 문서
│   ├── main/
│   │   ├── java/
│   │   │   └── mini/minishop/
│   │   │       ├── api/        # API 컨트롤러 및 서비스
│   │   │       ├── config/     # JWT, Security 등 설정
│   │   │       ├── domain/     # Entity, Repository 등
│   │   │       ├── exception/  # 커스텀 예외 처리
│   │   │       └── filter/     # JWT 필터
│   │   └── resources/          # 설정 파일
│   └── test/
├── build.gradle
└── README.md
```
