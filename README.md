# Mini Shop

<div align="center">
  <img width="300" height="300" alt="minishop_logo" src="https://github.com/user-attachments/assets/7343ddb3-d12e-4bcc-99e7-bdc73c79e7fb" />
</div>

## 🚀프로젝트 소개

이 프로젝트는 취업을 위해 **프로젝트 관리**와 **기술 스택**, 그리고 **API 개발** 능력을 향상시키고자 시작한 개인 프로젝트입니다.<br>
판매자는 상품을 판매하고, 구매자는 상품을 구매하는 간단한 **C to C** 서비스입니다.

<hr>

### 💡프로젝트 과정
- [프로젝트 과정 정리 Notion](https://young-hair-9ab.notion.site/Spring-Boot-JPA-REST-API-21f86702809c806c8d15e93f1f5f4450?source=copy_link)

<hr>

### 📱개발 기간

- 2025.07.01 ~ 2025.09.02, 이후 기능 확장과 성능 개선 진행

<hr>

### 🛠️기술 스택

- **Environment**: IntelliJ IDEA, GitHub, Notion
- **Backend**: Java, Spring Boot, Spring Security, JPA, JWT
- **Database**: H2 Database

<hr>

### ✨주요 기능

- JWT 기반 회원 관리
- 상품 등록 및 수정 기능
- 상품 주문 기능

<hr>

### 📦아키텍쳐

#### 디렉토리 구조

```
├──src/
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
