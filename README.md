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
  
  `@Transactional(propagation = Propagation.REQUIRED)`

이 트랜잭션 안에서 다음 작업이 하나의 트랜잭션으로 동작합니다:

- 출금 (`withdraw`)
- 입금 (`deposit`)
- 로그 저장

---


### 2️⃣ 로그 트랜잭션 분리

- `TransferLogService.saveSuccessLog()` ---> 모든 작업을 정상적으로 수행할 경우
- `TransferLogService.saveFailLog()` ---> 작업 도중 RuntimeException이 발생할 경우

`@Transactional(propagation = Propagation.REQUIRES_NEW)`



---
## 🔥 실패 시나리오 실험

이 프로젝트의 핵심은 **계좌이체는 롤백되지만, 실패 로그는 남는 구조**를 검증하는 것입니다.

### 📌 시나리오

1. 출금 수행 (`withdraw`)
2. 입금 직전에 `RuntimeException` 발생
3. `transfer()` 트랜잭션 전체 롤백
4. `saveFailLog()`는 `REQUIRES_NEW` 트랜잭션으로 별도 커밋

---

### 🔁 트랜잭션 흐름

TransferService.transfer() (REQUIRED)
1. withdraw()
2. ❗ RuntimeException 발생
3.  rollback

TransferLogService.saveFailLog() (REQUIRES_NEW)
1. commit


---

### ✅ 기대 결과

- 보내는 계좌 잔고 → 변동 없음
- 받는 계좌 잔고 → 변동 없음
- 로그 상태 → `FAIL` 로 저장됨

위 결과를 테스트 코드로 직접 검증했습니다.(TransferServiceTest/failLogTest)

---

## 🧪 테스트를 통해 확인한 사실

이번 프로젝트를 통해 다음을 명확히 확인했습니다.

- 트랜잭션은 **메서드 종료 시점**에 커밋된다.
- `RuntimeException` 발생 시 기본적으로 롤백된다.
- 예외를 `catch`만 하고 다시 던지지 않으면 롤백되지 않는다.
- `REQUIRES_NEW`는 기존 트랜잭션과 **물리적으로 분리**된다.

---

## 💡 설계 의도

- 엔티티 내부에 비즈니스 로직을 위치시켜 도메인 중심 설계 지향
- setter 대신 행위 메서드 사용 (`withdraw`, `deposit`)
- 트랜잭션 경계를 Service 레이어에서 명확히 구분
- 성공/실패 저장 로그를 별도 트랜잭션으로 분리
- 실패 케이스를 반드시 테스트하여 트랜잭션 동작을 검증

---

## 📚 이 프로젝트를 통해 얻은 깨달음

`@Transactional`은 단순히 붙이면 끝나는 애노테이션이 아니었습니다.

- 어디에 트랜잭션 경계를 둘 것인가?
- 어떤 로직을 같은 트랜잭션으로 묶어야 하는가?
- 로그는 왜 분리해야 하는가?
- 예외를 어떻게 처리해야 롤백이 유지되는가?

트랜잭션은 기능 구현을 넘어,  
**설계의 영역이라는 것을 체감한 프로젝트였습니다.**

---

## 🏁 마무리

이 프로젝트는 단순한 계좌이체 구현이 아니라,  
**Spring 트랜잭션의 실제 동작을 이해하기 위한 실험 기록**입니다.

