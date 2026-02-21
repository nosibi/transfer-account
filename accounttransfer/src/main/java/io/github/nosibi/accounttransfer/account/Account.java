package io.github.nosibi.accounttransfer.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계좌번호, 비밀번호 정규식으로 검증 필요
 * ex) 계좌번호는 000-000-000000 / 비밀번호는 8~12자리 숫자, 특수문자, 문자 포함
 * 상태 변경은 엔티티 자체적으로 처리하도록 설계
 * 암호변경, 상태변경, 암호검증, 상태검증은 엔티티에서 자체 처리
 * setter를 없애고 행위 메서드를 추가하여 무결성 유지(계좌가 해지 상태인데 setter로 잔고를 변경해버리면 안됨)
 * 잔고 증감은 setter가 아닌 행위 메서드를 별도로 정의하여 구현
 */
@Entity
@Getter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{5}$",
            message = "계좌번호 형식은 000-000-000000이어야 합니다.")
    private String accountNumber;

    @Column
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$",
            message = "비밀번호는 8~12자리 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @Column
    @Positive
    private Long balance;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    public Account(String accountNumber, String password){
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = 0L;
        this.status = Status.ACTIVE;
    }

    public void changePassword(String oldPassword,String newPassword){
        validatePassword(oldPassword);
        validateActive();
        this.password = newPassword;
    }

    public void withdraw(Long amount){
        validateActive();
        if(this.balance < amount){
            throw new IllegalStateException("잔액 부족");
        }
        this.balance -= amount;
    }

    public void deposit(Long amount){
        validateActive();
        this.balance += amount;
    }

    public void freeze() {
        validateActive();
        this.status = Status.FREEZE;
    }

    public void delete(){
        validateActive();
        this.status = Status.CLOSED;
    }

    public void validatePassword(String password){
        if(!this.password.equals(password)){
            throw new IllegalStateException("비밀번호 불일치");
        }
    }

    public void validateActive(){
        if(this.status == Status.CLOSED){
            throw new IllegalStateException("해지된 계좌입니다.");
        }

        if (this.status == Status.FREEZE){
            throw new IllegalStateException("정지된 계좌입니다.");
        }
    }
}