package game_forum_api.member.dto;

import java.util.Date;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

	@NotBlank(message = "不能為空")
	private String accountId;
	
	@NotBlank(message = "不能為空")
	private String password;
	
	@NotBlank(message = "不能為空")
	private String username;
	
	private String phone;
	
	@NotBlank(message = "不能為空")
	private String email;
	
	private String address;
	
	@NotNull(message = "不能為空")
	@Temporal(TemporalType.DATE)
	private Date birthdate;
	
	private byte[] photo;
}
