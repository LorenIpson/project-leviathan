package game_forum_api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.exception.common.UnauthorizedException;

@RestControllerAdvice // 全局異常處理器
public class GlobalExceptionHandler {

	// 400 Bad Request - 表單驗證錯誤
	// 說明：需要在 Bean 或 DTO 內寫參數驗證，搭配 @Valid 或 @Validated 一起使用
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		Map<String, Object> response = new HashMap<>();
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.BAD_REQUEST.value());
		response.put("error", "Validation Error");
		response.put("errors", errors);
		response.put("message", convertErrorsToMessage(errors));

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	private String convertErrorsToMessage(Map<String, String> errors) {
		return errors.entrySet().stream().map(entry -> entry.getKey() + "：" + entry.getValue())
				.collect(Collectors.joining("，"));
	}

	// 400 Bad Request：用戶請求格式錯誤
	// 說明：參數驗證錯誤
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
		return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

	// 401 Unauthorized：未經授權
	// 說明：未登入
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex) {
		return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
	}

	// 403 Forbidden：禁止訪問
	// 說明：沒有權限
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException ex) {
		return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
	}

	// 404 Not Found：資源不存在
	// 說明：找不到資料
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
	}

	// 409 Conflict：資源衝突
	// 說明：重複資料，例如該產品會員 ID 已存在
	@ExceptionHandler(DataConflictException.class)
	public ResponseEntity<Map<String, Object>> handleDataConflictException(DataConflictException ex) {
		return buildErrorResponse(ex, HttpStatus.CONFLICT);
	}

	// 500 Internal Server Error：處理所有未預期的錯誤
	// 說明：系統錯誤都可以使用，例如 JAVA 編譯出現問題或是 SQLException
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
		HttpStatus status = getHttpStatus(ex);
		return buildErrorResponse(ex, status);
	}

	// 這個是讓 handleGeneralException 來判斷 Exception，應當返回甚麼類型的 HttpStatus
	// 如有其他情境，不想要返回 500 狀態，請在這新增
	private HttpStatus getHttpStatus(Exception ex) {
		// 通过检查异常类型来返回不同的 HTTP 状态码
		if (ex instanceof MissingRequestHeaderException) { // 請求 header 異常，返回400
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof IllegalArgumentException) { // 參數異常，返回 400
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof ResourceNotFoundException) {
			return HttpStatus.NOT_FOUND; // 未找到資料，返回 404
		} else {
			return HttpStatus.INTERNAL_SERVER_ERROR; // 其餘默認 500
		}
	}

	private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatus status) {
		Map<String, Object> response = new HashMap<>();
		response.put("timestamp", LocalDateTime.now());
		response.put("status", status.value());
		response.put("error", status.getReasonPhrase());
		response.put("message", ex.getMessage());
		return new ResponseEntity<>(response, status);
	}

	// ------------------------------------------------

}
