package com.shop.cart.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .anyRequest().permitAll() // Erlaube Zugriff auf alle Seiten ohne Authentifizierung
            )
            .formLogin(formLogin ->
                formLogin
                    .disable() // Deaktiviere Form-Login
            )
            .logout(logout ->
                logout
                    .disable() // Deaktiviere Logout
            )
            .csrf(csrf -> csrf.disable()); // Deaktiviere CSRF-Schutz, falls nötig

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt für Passwort-Hashing
    }
}
