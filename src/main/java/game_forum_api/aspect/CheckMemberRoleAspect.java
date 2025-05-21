package game_forum_api.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import game_forum_api.annotation.CheckMemberRole;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.UnauthorizedException;
import game_forum_api.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class CheckMemberRoleAspect {

	private final HttpServletRequest request;

	private final JwtService jwtService;

	public CheckMemberRoleAspect(HttpServletRequest request, JwtService jwtService) {
		this.request = request;
		this.jwtService = jwtService;
	}

	@Around("@annotation(game_forum_api.annotation.CheckMemberRole)")
	public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {

		// 取得 Authorization Header
		String token = request.getHeader("Authorization");
		if (token == null || token.isEmpty()) {
			throw new UnauthorizedException("缺少 token");
		}

		// 透過 token 解析並取得 memberRole
		Integer memberRole = jwtService.getMemberRoleByHeaderToken(token);
		if (memberRole == 0) {
			throw new ForbiddenException("已被停權");
		}

		// 取得方法上的 `@CheckPermission` 設定
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		CheckMemberRole annotation = method.getAnnotation(CheckMemberRole.class);

		if (annotation != null) {
			int requiredPermission = annotation.value();

			// 檢查權限是否足夠
			if (memberRole < requiredPermission) {
				throw new ForbiddenException("權限不足");
			}
		}

		// 權限通過，繼續執行原方法
		return joinPoint.proceed();
	}
}
