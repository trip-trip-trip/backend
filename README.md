# Tripshot Backend

**Tripshot**은 친구들과 함께 여행을 계획하고, 추억을 기록하며 공유할 수 있는 올인원 여행 플랫폼의 백엔드 서버입니다.
Spring Boot를 기반으로 구축되었으며, 안정적인 REST API 서비스와 다양한 소셜 기능을 제공합니다.

## Tech Stack

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.7
- **Build Tool**: Gradle
- **Database**: H2 (Dev), Postgresql (Prod)
- **ORM**: Spring Data JPA, QueryDSL
- **Security**: Spring Security, JWT (Json Web Token)

### Infrastructure & External Services
- **Storage**: Cloudflare R2 (이미지 및 미디어 파일 저장)
- **Authentication**: OAuth 2.0 (Google, Kakao, Naver)
- **Notification**: Web Push (VAPID), SMS Integration
- **DevOps**: Docker (Optional)

## Key Features

* **사용자 인증 및 관리**
    * 자체 회원가입/로그인 및 OAuth 2.0 (구글, 카카오, 네이버) 지원
    * JWT 기반의 보안 인증 (Access/Refresh Token)
    * SMS 본인 인증 및 OTP 검증

* **소셜 네트워크**
    * 친구 맺기 (요청, 수락, 거절), 친구 목록 조회
    * 사용자 검색 및 프로필 조회

* **여행(Trip) 관리**
    * 여행 생성 및 친구 초대
    * 여행 일정 관리 및 참여자 관리
    * 여행 상태 관리 (계획 중, 진행 중, 완료 등)

* **미디어 & 기록**
    * **게시글(Post)**: 여행 중 사진, 위치 정보와 함께 게시글 작성 및 좋아요/댓글 소통
    * **앨범(Album)**: 여행별 사진첩 공유 및 관리
    * **쇼트 릴(Short Reel)**: 짧은 영상 콘텐츠 생성 및 조회
    * **스크랩북(Scrapbook)**: 원하는 정보를 스크랩하여 나만의 가이드북 생성

* **알림 서비스**
    * 실시간 웹 푸시 알림 (Web Push)
    * 알림 설정 관리 (구독/해지)

## Project Structure

```bash
src/main/java/yeohaenggasijo/tripshot
├── config          # 설정 파일 (Security, QueryDSL, R2, WebPush 등)
├── controller      # REST API 컨트롤러
├── domain          # JPA 엔티티 (User, Trip, Post, Media 등)
├── dto             # 데이터 전송 객체 (Request/Response)
├── exception       # 전역 예외 처리
├── repository      # 데이터 접근 계층 (JPA/QueryDSL Repository)
├── security        # 인증/인가 관련 클래스 (JWT, Filter)
├── service         # 비즈니스 로직
└── util            # 유틸리티 클래스 (Sms, File 등)
```
