package kh.edu.istad.codecompass.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class KeyCloakSecurityConfig {

    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        // Set all request must be authenticated
        http.authorizeHttpRequests( request ->
                request
                        .requestMatchers(
                                "/api-docs",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/index.html",
                                "/swagger-ui/index.html/**",
                                "/webjars/**",
                                "/media/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/code-compass/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/code-compass/problems/verified").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/code-compass/problems/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/code-compass/hints/**").permitAll()
                        .requestMatchers("/api/v1/code-compass/roles/assign-role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/code-compass/problems/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/code-compass/problems").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/code-compass/problems/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/code-compass/problems/unverified").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "api/v1/code-compass/packages/add-problems").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.PATCH, "api/v1/code-compass/creator-requests").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
        );

        // Disable default form login
        http.formLogin(AbstractHttpConfigurer::disable);

        // Disable CSRF token
        http.csrf(AbstractHttpConfigurer::disable);

        // Set security mechanism
        http.oauth2ResourceServer(
                oauth -> oauth.jwt(Customizer.withDefaults())
        );

        // Set session to stateless
        http.sessionManagement(
          session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter  jwtAuthenticationConverter() {

        Converter<Jwt, Collection<GrantedAuthority>> converter = jwt -> {

            Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
            Collection<String> roles =realmAccess.get("roles");

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        };

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

}
