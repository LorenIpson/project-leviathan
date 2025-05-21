package game_forum_api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http // 防止 XSS（跨站腳本攻擊），限制外部資源的載入。
            .headers(headers -> headers // 設定 HTTP 回應標頭
                .contentSecurityPolicy(csp -> csp // 定義內容安全政策
                    .policyDirectives("script-src 'self' 'unsafe-inline' https://payment-stage.ecpay.com.tw https://*.ecpay.com.tw;")
                ) // 允許 自身的JS ; 允許內嵌腳本 ; 允許 payment 和 *.ecpay 相關腳本。
            )
            .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF 保護，通常在建立 REST API 或非瀏覽器的應用中會這麼做，
            									   // 因為 CSRF 防護主要針對瀏覽器提交表單的攻擊情形。
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 這可以讓應用允許來自特定來源的跨域請求，
            																   // 例如前端應用在不同的域名或埠上執行時
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/api/payment/result").permitAll()
            		.anyRequest().permitAll()); // 表示所有 HTTP 請求都允許存取，不進行身份驗證或授權檢查。

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:5173","https://natural-integral-imp.ngrok-free.app","https://payment-stage.ecpay.com.tw","https://*.ecpay.com.tw")); // 允許的來源，前端開發地址
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允許的 HTTP 方法
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Auth-Token", "X-Member-Id")); // 允許標頭
        configuration.setExposedHeaders(List.of("Authorization", "X-Member-Id")); // 讓前端能夠讀取這些標頭
        configuration.setAllowCredentials(true); // 允許攜帶 Cookie 或 Token

        // 註冊 CORS 配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 所有URL上
        return source;
    }
}
