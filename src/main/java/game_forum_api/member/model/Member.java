package game_forum_api.member.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "account_id", unique = true, nullable = false, length = 20)
	private String accountId;

	@Column(name = "username", nullable = false, length = 20)
	private String username;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "email", unique = true, length = 255)
	private String email;

	@Column(name = "address", length = 255)
	private String address;

	@Column(name = "birthdate", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date birthdate;

	@Column(name = "role", nullable = false)
	private Integer role; // 權限等級（0: 停權, 1: 一般會員, 2: 管理員, 3: 最高管理員）

	@Column(name = "created_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date(); // 註冊日期

	@Lob
	@Column(name = "photo")
	private byte[] photo;

}
