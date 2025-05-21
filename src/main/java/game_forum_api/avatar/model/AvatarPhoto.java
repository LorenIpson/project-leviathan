package game_forum_api.avatar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "avatar_photo")
public class AvatarPhoto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "member_id", nullable = false)
	private Integer memberId; // 會員ID

	@Lob
	@Column(name = "body_photo")
	private byte[] bodyPhoto; // 全身圖片路徑

	@Lob
	@Column(name = "face_photo")
	private byte[] facePhoto; // 頭像圖片路徑
}