package game_forum_api.forum.vote.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 留言與回覆評分主鍵管理。
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentsVotesPK implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "member_id")
    private Integer memberId;

    @Override
    public int hashCode() {
        return Objects.hash(commentId, memberId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommentsVotesPK other = (CommentsVotesPK) obj;
        return Objects.equals(commentId, other.commentId) && Objects.equals(memberId, other.memberId);
    }

}
