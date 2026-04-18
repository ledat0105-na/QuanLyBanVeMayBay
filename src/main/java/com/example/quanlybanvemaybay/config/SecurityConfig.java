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
        return username -> userRepository.findByUsernameWithRole(username)
                .map(u -> {
                    String roleName = u.getRole() != null ? u.getRole().getRoleName() : "USER";
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + roleName)
                    );
                    return User.withUsername(u.getUsername())
                            .password(u.getPassword())
                            .authorities(authorities)
                            .accountLocked(false)
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));
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
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/lib/**", "/webjars/**", "/uploads/**", "/error").permitAll()
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password", "/fix-pass").permitAll()
                        .requestMatchers("/about", "/services", "/projects", "/contact", "/contact/send").permitAll()
                        .requestMatchers("/admin/report", "/admin/bookings/**", "/admin/passengers/**", "/admin/profile/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/booking/**").hasAnyRole("USER", "STAFF")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(new org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, org.springframework.security.core.Authentication authentication) throws java.io.IOException, jakarta.servlet.ServletException {
                                boolean isAdmin = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                                boolean isStaff = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
                                if (isAdmin) {
                                    this.setDefaultTargetUrl("/admin/report");
                                } else if (isStaff) {
                                    this.setDefaultTargetUrl("/admin/bookings");
                                } else {
                                    this.setDefaultTargetUrl("/");
                                }
                                super.onAuthenticationSuccess(request, response, authentication);
                            }
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
