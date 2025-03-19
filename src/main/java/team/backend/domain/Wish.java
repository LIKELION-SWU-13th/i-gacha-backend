package team.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import team.backend.apiPayload.code.status.ErrorStatus;
import team.backend.apiPayload.exception.handler.EventHandler;
import team.backend.domain.common.BaseEntity;

@Entity
@Getter @Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class Wish extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;

    public void setEvent(Event event){
        if(this.event != null)
            event.getWishList().remove(this);
        this.event = event;
        event.getWishList().add(this);
    }

    public Wish update(String name, String link, String imageUrl) {
        if (name != null) this.name = name;
        if (link != null) this.link = link;
        if (imageUrl != null && !this.imageUrl.equals(imageUrl)) { // 변경 감지 강제 적용
            this.imageUrl = imageUrl;
        }else{
            throw new EventHandler(ErrorStatus._LINK_ERROR);
        }
        return this;
    }

}