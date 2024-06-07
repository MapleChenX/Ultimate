package personal.MapleChenX.lsp.config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import personal.MapleChenX.lsp.common.model.dto.UserBasicInfoDTO;
import personal.MapleChenX.lsp.common.model.resp.LoginResp;
import personal.MapleChenX.lsp.common.model.ret.ResponseTemplate;
import personal.MapleChenX.lsp.interceptor.JwtAuthenticationFilter;
import personal.MapleChenX.lsp.service.UserService;
import personal.MapleChenX.lsp.utils.CustomPasswordEncoder;
import personal.MapleChenX.lsp.utils.JwtUtils;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SpringSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }

    @Resource
    JwtUtils utils;

    @Resource
    UserService userService;

    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(conf -> conf
                        .usernameParameter("account")
                        .passwordParameter("password")
                        .loginProcessingUrl("/login")
                        .successHandler(this::doLoginSuccess)
                        .failureHandler(this::doLoginFailure)
                        .permitAll()
                )
                .logout(conf -> conf
                        .logoutUrl("/logout") //自动删除ctx中的Authentication
                        .logoutSuccessHandler(this::doLogoutSuccess)
                )
                .exceptionHandling(conf -> conf
                        .accessDeniedHandler(this::doAccessDenied)
                        .authenticationEntryPoint(this::doAuthenticationException)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT验证
                .build();
    }

    private void doLoginSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        // 获取认证信息
        User user = (User) authentication.getPrincipal();
        // 签发令牌
        String jwt = utils.createJwt(user);
        if(jwt == null) {
            writer.write(ResponseTemplate.forbidden().toJsonString());
        } else {
            String account = user.getUsername();
            UserBasicInfoDTO userInfoByAccount = userService.getUserInfoByAccount(account);
            LoginResp loginResp = new LoginResp();
            BeanUtils.copyProperties(userInfoByAccount, loginResp);
            loginResp.setToken(jwt);
            writer.write(ResponseTemplate.success(loginResp).toJsonString());
        }

    }

    private void doLoginFailure(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(ResponseTemplate.failure(250, "密码错了").toJsonString());
    }

    private void doLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String BearerToken = request.getHeader("Authorization");
        if(utils.invalidToken(BearerToken)) {
            writer.write(ResponseTemplate.success("退出登录成功").toJsonString());
        }else {
            writer.write(ResponseTemplate.failure(250, "已退出登录，不能多次执行").toJsonString());
        }
    }


    private void doAccessDenied(HttpServletRequest request,
                                HttpServletResponse response,
                                AccessDeniedException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("Access denied");
    }

    private void doAuthenticationException(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("验证错误");
    }

}
