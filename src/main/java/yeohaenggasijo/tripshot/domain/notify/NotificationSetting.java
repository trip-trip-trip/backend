package yeohaenggasijo.tripshot.domain.notify;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "notification_settings")
public class NotificationSetting extends BaseEntity {
  @Id
  private Long userId;

  @OneToOne(fetch = FetchType.LAZY) @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "moment_enabled")
  private Boolean momentEnabled;
  private Integer timesPerDay;

  @Column(length = 64)
  private String timezone;

  public Boolean isMomentEnabled() {
    return momentEnabled != null && momentEnabled;
  }

  // timezone에서 시작/종료 시간 추출
  public int getStartHour() {
    try {
      List<Integer> times = new ObjectMapper().readValue(timezone, new TypeReference<List<Integer>>(){});
      return times.get(0);
    } catch (Exception e) {
      return 12; // 기본값
    }
  }

  public int getEndHour() {
    try {
      List<Integer> times = new ObjectMapper().readValue(timezone, new TypeReference<List<Integer>>(){});
      return times.get(1);
    } catch (Exception e) {
      return 17; // 기본값
    }
  }

}
