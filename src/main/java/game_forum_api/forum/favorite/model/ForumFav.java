package game_forum_api.forum.favorite.model;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forum_fav")
public class ForumFav {

    @EmbeddedId
    private ForumFavPK forumFavPK;

    /**
     * 討論區 ID 主鍵連結 PK Class。
     */
    @ManyToOne
    @MapsId("forumId")
    @JoinColumn(name = "forum_id")
    private Forums forum;

    /**
     * 會員 ID 主鍵連結 PK Class。
     */
    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

}
