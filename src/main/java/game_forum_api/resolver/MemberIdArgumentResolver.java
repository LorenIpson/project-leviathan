package game_forum_api.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import game_forum_api.annotation.MemberId;
import game_forum_api.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	private JwtService jwtService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// 判斷參數是否標註了 @MemberId，且型別是否為 Integer
		return parameter.hasParameterAnnotation(MemberId.class) && parameter.getParameterType().equals(Integer.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		String token = request.getHeader("Authorization");

		// 返回從 token 解析的 memberId
		return jwtService.getMemberIdByHeaderToken(token);
	}

}
