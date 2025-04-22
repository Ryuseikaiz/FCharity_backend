package fptu.fcharity.config.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod nếu cần dùng
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Cấu hình cho WebSocket trước (nếu có)
        // http.securityMatcher("/ws/**") // Cân nhắc lại dòng này, authorizeHttpRequests bên dưới đã xử lý
        //         .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // --- DANH SÁCH CÁC ENDPOINT CÔNG KHAI ---
                        .requestMatchers(
                                "/auth/**",          // Xác thực (login, signup, verify, reset pwd...)
                                "/oauth2/**",        // OAuth2 callback endpoints (nếu dùng)
                                "/tags",             // Lấy danh sách tags
                                "/categories",       // Lấy danh sách categories
                                "/payment/webhook",  // PayOS webhook (cần công khai)
                                "/topic/**", "/app/**", "/ws/**" // WebSocket endpoints
                        ).permitAll()
                        // Cho phép truy cập công khai MỘT SỐ API GET cụ thể
                        .requestMatchers(HttpMethod.GET,
                                "/requests", "/requests/active", "/requests/{id:[0-9a-fA-F\\-]+}", // Regex UUID
                                "/projects", "/projects/{id:[0-9a-fA-F\\-]+}", "/projects/org/{orgId:[0-9a-fA-F\\-]+}", "/projects/wallet/{walletId:[0-9a-fA-F\\-]+}",
                                "/posts", "/posts/{id:[0-9a-fA-F\\-]+}",
                                "/comments/post/{postId:[0-9a-fA-F\\-]+}",
                                "/api/files/{filename:.+}", // Regex cho filename có dấu chấm
                                "/api/organizations", "/api/organizations/{organizationId:[0-9a-fA-F\\-]+}"
                        ).permitAll()
                        // --- KẾT THÚC ENDPOINT CÔNG KHAI ---

                        // --- CÁC ENDPOINT YÊU CẦU XÁC THỰC ---
                        // Ví dụ: API chat yêu cầu đăng nhập
                        .requestMatchers("/api/chat/gemini").authenticated()
                        // Ví dụ: API admin yêu cầu quyền Admin
                        .requestMatchers("/api/admin/**").hasAuthority("Admin") // Hoặc .hasRole("ADMIN") tùy cấu hình UserDetails
                        // --- Tất cả các request khác CHƯA được permitAll hoặc quy định cụ thể sẽ yêu cầu xác thực ---
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Dùng JWT nên là STATELESS
                )
                // Cấu hình OAuth2 Login (giữ lại nếu bạn có luồng đăng nhập bằng Google từ backend)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google") // Trang xử lý đăng nhập Google (thường là của Spring Security)
                        .defaultSuccessUrl("/home", true) // Chuyển hướng sau khi login thành công
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Thêm filter JWT

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:8080", // Thường không cần nếu backend và frontend khác port
                "http://localhost:3001",
                "http://localhost:3002",
                "https://fcharity.azurewebsites.net",
                "https://fcharitywebapp.azurewebsites.net",
                "http://localhost:4000"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // OPTIONS là cần thiết cho preflight
        configuration.setAllowedHeaders(List.of("*")); // Cho phép tất cả headers
        configuration.setAllowCredentials(true); // Cho phép gửi cookie/token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả đường dẫn
        return source;
    }
}