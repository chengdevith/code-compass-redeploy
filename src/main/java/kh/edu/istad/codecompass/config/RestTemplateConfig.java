package kh.edu.istad.codecompass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate judge0RestTemplate(
            @Value("${judge0.protocol}") String protocol,
            @Value("${judge0.domain}") String domain,
            @Value("${judge0.port}") int port,
            @Value("${judge0.auth-token:}") String authToken
    ) {
        // Build base URL
        String baseUrl = String.format("%s://%s:%d", protocol, domain, port);

        // Factory with timeouts
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);

        RestTemplate restTemplate = new RestTemplate(factory);

        // Set base URL handler (no need for manual interceptor rewrite)
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));

        // Interceptors for headers (e.g., auth)
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        if (authToken != null && !authToken.isBlank()) {
            interceptors.add((request, body, execution) -> {
                request.getHeaders().add("X-Auth-Token", authToken);
                return execution.execute(request, body);
            });
        }

        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}


