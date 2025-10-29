package yeohaenggasijo.tripshot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 상속받는 엔티티에 매핑 정보 제공
@EntityListeners(AuditingEntityListener.class) // 엔티티 변경 감지
public abstract class BaseEntity {

    @CreatedDate // 엔티티 최초 생성 시 자동 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티 값 변경 시 자동 저장
    private LocalDateTime updatedAt;
}
