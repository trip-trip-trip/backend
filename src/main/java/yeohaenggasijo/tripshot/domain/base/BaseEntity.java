package yeohaenggasijo.tripshot.domain.base;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreationTimestamp
    @Column(updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 시 필요
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 시 필요
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss") // 원하는 형태의 LocalDateTime 설정
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 시 필요
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 시 필요
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss") // 원하는 형태의 LocalDateTime 설정
    @UpdateTimestamp
    protected LocalDateTime updatedAt;
}
