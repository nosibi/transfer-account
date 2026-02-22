# 💳 Account Transfer – 트랜잭션 전파 실험

## 📌 프로젝트 소개

이 프로젝트는 단순한 계좌이체 구현이 목적이 아니다.

Spring의 `@Transactional`이 실제로 어떻게 동작하는지,  
특히 **트랜잭션 전파(Propagation)와 롤백 동작을 직접 실험하고 테스트로 검증하는 것**이 목표였습니다.

> "REQUIRES_NEW는 정말 물리적으로 트랜잭션이 분리될까?"  
> "RuntimeException이 발생하면 정확히 어디까지 롤백될까?"  
> "예외를 잡아도 롤백이 될까?"

이 질문들에 대한 답을 코드와 테스트로 확인하기 위해 만들었습니다.

---

## 🎯 학습 목표

- REQUIRED 전파에서 RuntimeException 발생 시 전체 롤백 확인
- REQUIRES_NEW 전파를 통한 물리 트랜잭션 분리
- 계좌이체는 롤백되지만 실패 로그는 남는 구조 구현
- 트랜잭션은 메서드 종료 시점에 커밋된다는 점을 테스트로 검증(모든 작업 종료 시)
- 예외를 다시 던지지 않으면 롤백되지 않는다는 점 확인

---

## 🏗 트랜잭션 구조

### 1️⃣ 계좌이체 트랜잭션

- `TransferService.transfer()`  
  → `@Transactional` (REQUIRED)

이 트랜잭션 안에서 다음 작업이 하나의 트랜잭션으로 동작합니다:

- 출금 (`withdraw`)
- 입금 (`deposit`)
- 로그 저장

---

### 2️⃣ 로그 트랜잭션 분리

- `TransferLogService.saveSuccessLog()` ---> 모든 작업을 정상적으로 수행할 경우
- `TransferLogService.saveFailLog()` ---> 작업 도중 RuntimeException이 발생할 경우

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
