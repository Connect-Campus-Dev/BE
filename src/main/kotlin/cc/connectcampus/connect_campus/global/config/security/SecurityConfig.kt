package cc.connectcampus.connect_campus.global.config.security

import lombok.extern.slf4j.Slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.util.AntPathMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.handler.HandlerMappingIntrospector


@Configuration
@EnableWebSecurity
@Slf4j
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        introspector: HandlerMappingIntrospector,
        entryPoint: JwtAuthenticationEntryPoint
    ): SecurityFilterChain {
        val mvcMatcherBuilder = MvcRequestMatcher.Builder(introspector)

        http
            .headers { it -> it.frameOptions { it.sameOrigin().disable() } }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it
//                    .anyRequest().permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/signup")).anonymous()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/login")).anonymous()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/post")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/h2-console/**")).permitAll()

                    //채팅 파트는 다 열어두고, stompHandler에서 검증
                    .requestMatchers(AntPathRequestMatcher("/topic/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/app/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/chat/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/user/**")).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilter(corsFilter())
            .addFilterBefore(   //Filter 실행 순서 지정, 앞의 필터에서 실행이 성공하면 뒤의 필터는 실행하지 않음
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling { it.authenticationEntryPoint(entryPoint) }

        return http.build()
    }

    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("http://localhost:5173")
        config.addAllowedOrigin("http://192.168.0.5:5173")
        config.addAllowedOrigin("https://connect-campus.cc")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.exposedHeaders = listOf("Authorization")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

}