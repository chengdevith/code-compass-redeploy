package kh.edu.istad.codecompass.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
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
                request.anyRequest().permitAll()
        );

        // Disable default form login
        http.formLogin(AbstractHttpConfigurer::disable);

        // Disable CSRF token
        http.csrf(AbstractHttpConfigurer::disable);

//        Disable CrossOrigin
        http.cors(AbstractHttpConfigurer::disable);

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
