package game_forum_api.exception.common;

//403 Forbidden
public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ForbiddenException(String message) {
		super(message);
	}
}