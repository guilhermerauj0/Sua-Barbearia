package com.barbearia.infrastructure.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração para carregar variáveis do arquivo .env
 * 
 * Esta classe lê o arquivo .env e adiciona as variáveis ao ambiente do Spring
 * antes da inicialização da aplicação.
 * 
 * @author Sua Barbearia Team
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    @Override
    @SuppressWarnings("null")
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            // Lê o arquivo .env da raiz do projeto
            Map<String, Object> envVariables = loadEnvFile(".env");
            
            // Adiciona as variáveis ao ambiente do Spring
            environment.getPropertySources().addFirst(
                new MapPropertySource("dotenv", envVariables)
            );
            
            System.out.println("✅ Variáveis do .env carregadas com sucesso");
        } catch (IOException e) {
            System.err.println("⚠️ Arquivo .env não encontrado ou erro ao ler: " + e.getMessage());
        }
    }
    
    /**
     * Lê o arquivo .env e retorna um mapa com as variáveis
     */
    private Map<String, Object> loadEnvFile(String filePath) throws IOException {
        Map<String, Object> envMap = new HashMap<>();
        
        Files.lines(Paths.get(filePath))
            .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
            .forEach(line -> {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    envMap.put(key, value);
                }
            });
        
        return envMap;
    }
}
