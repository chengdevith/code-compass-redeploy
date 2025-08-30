package kh.edu.istad.codecompass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient judge0WebClient(
            WebClient.Builder builder,
            @Value("${judge0.protocol}") String protocol,
            @Value("${judge0.domain}") String domain,
//            @Value("${judge0.port}") int port,
            @Value("${judge0.auth-token:}") String authToken
    ) {

        String baseUrl = String.format("%s://%s", protocol, domain);

        WebClient.Builder clientBuilder = builder.baseUrl(baseUrl);

        if (authToken != null && !authToken.isBlank()) {
            clientBuilder.defaultHeader("X-Auth-Token", authToken);
        }

        return clientBuilder.build();
    }

    @Bean
    public WebClient keycloakWebClient(){
        return WebClient.builder()
                .baseUrl("https://keyy.devith.it.com")
                .build();
    }
}