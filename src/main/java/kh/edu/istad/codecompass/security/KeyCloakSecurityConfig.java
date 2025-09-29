package kh.edu.istad.codecompass.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity(prePostEnabled = true)
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
                                "/media/**",
                                "/api/v1/ws-comments/**"
                        ).permitAll()

//                        auth
                        .requestMatchers("/api/v1/auth/register").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/api/v1/auth/reset-password").hasRole("ADMIN")
                        .requestMatchers("/api/v1/auth/request-reset-password").hasAnyRole("ADMIN", "CREATOR", "SUBSCRIBER")

//                        badges
                        .requestMatchers("/api/v1/badges/verified").permitAll()
                        .requestMatchers( "/api/v1/badges/add-to-package").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers("/api/v1/badges").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers("/api/v1/badges/unverified").hasRole("ADMIN")
                        .requestMatchers("/api/v1/badges/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/badges").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/badges").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/badges/me").hasAnyRole("ADMIN", "CREATOR")

//                        creator requests
                        .requestMatchers(HttpMethod.POST,"/api/v1/creator-requests").hasRole("SUBSCRIBER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/creator-requests").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/creator-requests").hasRole("ADMIN")

//                        hints
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/hints/*").hasAnyRole("CREATOR", "SUBSCRIBER")

//                        submissions
                        .requestMatchers(HttpMethod.POST, "/api/v1/submissions/run/batch").hasAnyRole("CREATOR", "SUBSCRIBER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/submissions/batch").hasAnyRole("CREATOR", "SUBSCRIBER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/submissions").hasAnyRole("CREATOR", "SUBSCRIBER")

//                        leader board
                        .requestMatchers("/api/v1/leaderboard/me").hasAnyRole("ADMIN" ,"CREATOR", "SUBSCRIBER")

//                        packages
                        .requestMatchers("/api/v1/packages/add-problems").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/packages").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/packages/me").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/packages/unverified").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/packages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/packages/verified").permitAll()
                        .requestMatchers( HttpMethod.PUT,"/api/v1/packages/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/packages/*").hasRole("ADMIN")

//                        problems
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/verified").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/*").permitAll() // For {problemId} - public access
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/unverified").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/problems/*/verification").hasRole("ADMIN") // For verification
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/problems/*").hasAnyRole("ADMIN", "CREATOR") // For updates
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/problems").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/*/me").authenticated() // For specific user access
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/me").hasAnyRole("ADMIN", "CREATOR")


//                        roles
                        .requestMatchers(HttpMethod.PUT, "/api/v1/roles/assign-role").hasRole("ADMIN")

//                        solutions
                        .requestMatchers(HttpMethod.POST, "/api/v1/solutions").hasAnyRole("ADMIN", "CREATOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/solutions/problem/*").hasAnyRole("ADMIN", "CREATOR", "SUBSCRIBER") // REMOVED trailing slash

//                        users
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/by-email/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/search").hasAnyRole("ADMIN", "CREATOR", "SUBSCRIBER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/update/*").hasAnyRole("ADMIN", "CREATOR", "SUBSCRIBER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")

//                        submission histories
                        .requestMatchers(HttpMethod.GET, "/api/v1/submission-histories/").hasAnyRole("ADMIN", "CREATOR", "SUBSCRIBER")

                        .anyRequest().authenticated()
        );

        // Disable default form login
        http.formLogin(AbstractHttpConfigurer::disable);

        // Disable CSRF token
        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

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
