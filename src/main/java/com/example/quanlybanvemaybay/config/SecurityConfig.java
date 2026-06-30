package com.example.quanlybanvemaybay.config;

import com.example.quanlybanvemaybay.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return identifier -> userRepository.findByIdentifier(identifier)
                .map(u -> {
                    String roleName = u.getRole() != null ? u.getRole().getRoleName() : "USER";
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + roleName)
                    );
                    return User.withUsername(u.getUsername())
                            .password(u.getPassword())
                            .authorities(authorities)
                            .accountLocked(u.getIsLocked() != null && u.getIsLocked())
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + identifier));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                              PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/images/**", "/lib/**", "/webjars/**", "/uploads/**", "/error", "/api/notifications/read/**", "/api/notifications/unread", "/flights/**").permitAll()
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password", "/api/auth/send-otp", "/api/auth/reset-password", "/fix-pass").permitAll()
                        .requestMatchers("/about", "/services", "/projects", "/contact", "/contact/send").permitAll()
                        .requestMatchers("/admin/report", "/admin/bookings/**", "/admin/passengers/**", "/admin/profile/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/booking/**").hasAnyRole("USER", "STAFF", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            jakarta.servlet.http.HttpSession session = request.getSession();
                            String returnUrl = (String) session.getAttribute("returnUrl");
                            
                            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                            boolean isStaff = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

                            
                            if (returnUrl != null && !returnUrl.isBlank()) {
                                session.removeAttribute("returnUrl");
                                if (!returnUrl.contains("login_success=true")) {
                                    returnUrl = returnUrl.contains("?") ? returnUrl + "&login_success=true" : returnUrl + "?login_success=true";
                                }
                                new org.springframework.security.web.DefaultRedirectStrategy().sendRedirect(request, response, returnUrl);
                                return;
                            }
                            
                            
                            org.springframework.security.web.savedrequest.SavedRequest savedRequest = 
                                new org.springframework.security.web.savedrequest.HttpSessionRequestCache().getRequest(request, response);
                            if (savedRequest != null) {
                                String redirectUrl = savedRequest.getRedirectUrl();
                                if (!redirectUrl.contains("/api/")) {
                                    redirectUrl = redirectUrl.replace("?continue", "").replace("&continue", "");
                                    
                                    if (!redirectUrl.contains("login_success=true")) {
                                        redirectUrl = redirectUrl.contains("?") ? redirectUrl + "&login_success=true" : redirectUrl + "?login_success=true";
                                    }
                                    new org.springframework.security.web.DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
                                    return;
                                }
                            }

                            
                            String targetUrl = "/";
                            if (isAdmin) targetUrl = "/admin/report";
                            else if (isStaff) targetUrl = "/admin/bookings";
                            
                            if (!targetUrl.contains("login_success=true")) {
                                targetUrl = targetUrl.contains("?") ? targetUrl + "&login_success=true" : targetUrl + "?login_success=true";
                            }
                            new org.springframework.security.web.DefaultRedirectStrategy().sendRedirect(request, response, targetUrl);
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );

        return http.build();
    }
}
