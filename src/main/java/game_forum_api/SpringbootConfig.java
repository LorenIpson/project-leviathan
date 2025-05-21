package game_forum_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import game_forum_api.jwt.JsonWebTokenInterceptor;
import game_forum_api.resolver.MemberIdArgumentResolver;

@Configuration
public class SpringbootConfig implements WebMvcConfigurer {

	@Autowired
	private JsonWebTokenInterceptor jsonWebTokenInterceptor;

	@Autowired
	private MemberIdArgumentResolver memberIdArgumentResolver;

	// 用來處理允許跨網域呼叫 api
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**").allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD");
		registry.addMapping("/chat.sendMessage").allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD");
		registry.addMapping("/topic/**public").allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD");
		
	}

	// 開啟用來對 api 進行 存於 request header 的 Authorization token 驗證
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(jsonWebTokenInterceptor).addPathPatterns("/members/**");
	}

	// 用來處理每個 controller 的參數檢查是否有 @MemberId
	// 如有使用的話，可根據 token 取得 memberId
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(memberIdArgumentResolver);
	}
}