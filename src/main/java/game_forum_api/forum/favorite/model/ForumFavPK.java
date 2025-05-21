package game_forum_api.forum.favorite.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumFavPK implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "forum_id")
    private Integer forumId;

    @Override
    public int hashCode() {
        return Objects.hash(forumId, memberId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ForumFavPK other = (ForumFavPK) obj;
        return Objects.equals(forumId, other.forumId) && Objects.equals(memberId, other.memberId);
    }

}
