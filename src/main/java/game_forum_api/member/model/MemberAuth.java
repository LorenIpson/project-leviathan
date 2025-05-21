package game_forum_api.member.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member_auth")
public class MemberAuth {

	@Id
	private Integer id;

	@Column(name = "password")
	private String password;

	@Column(name = "official_account", nullable = false)
	private Integer officialAccount; // 0: 非官方帳號, 1: 官方帳號

	@Column(name = "failed_attempts", nullable = false)
	private Integer failedAttempts = 0; // 紀錄當前登入失敗次數

	@Column(name = "lock_time")
	private LocalDateTime lockTime; // 紀錄帳號鎖定時間

	@Column(name = "last_login")
	private LocalDateTime lastLogin; // 紀錄最後成功登入時間
}
