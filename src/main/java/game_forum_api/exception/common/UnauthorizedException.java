package game_forum_api.exception.common;

//401 Unauthorized
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
		super(message);
	}
}