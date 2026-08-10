package com.smartjobai.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Origens permitidas — defina via variável de ambiente CORS_ALLOWED_ORIGINS no Railway.
     * Exemplo: https://meufront.vercel.app,http://localhost:5173
     * Padrão: permite localhost nas portas mais comuns e a URL de produção do Railway.
     */
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8080,https://smartjobai-api-production.up.railway.app}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Origens exatas
        config.setAllowedOrigins(allowedOrigins);
        
        // Origens flexíveis (ex: preview da Vercel ou Railway)
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "https://*.railway.app",
            "https://*.vercel.app"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
