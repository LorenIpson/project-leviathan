package game_forum_api.exception.common;

//401 Unauthorized
public class DataConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataConflictException(String message) {
		super(message);
	}
}