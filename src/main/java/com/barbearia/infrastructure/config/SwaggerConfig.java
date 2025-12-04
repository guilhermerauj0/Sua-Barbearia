package com.barbearia.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI 3.0
 * 
 * MIGRADO PARA ANNOTATIONS: Documentação agora via @Operation/@Schema nos
 * controllers
 * 
 * Benefícios:
 * - Documentação próxima ao código (melhor manutenibilidade)
 * - Menos código boilerplate (de 3087 para ~50 linhas)
 * - Auto-discovery de endpoints
 * - Validação em tempo de compilação
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(apiInfo())
        .servers(apiServers())
        .addSecurityItem(new SecurityRequirement().addList("Bearer"))
        .components(new io.swagger.v3.oas.models.Components()
            .addSecuritySchemes("Bearer", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer token para autenticação")));
  }

  private Info apiInfo() {
    return new Info()
        .title("API Sua Barbearia")
        .version("2.0.0")
        .description(
            "API REST para sistema de gestão de barbearias com agendamentos, avaliações e dashboard profissional")
        .contact(new Contact()
            .name("Time Sua Barbearia")
            .email("contato@suabarbearia.com")
            .url("https://github.com/guilhermerauj0/Sua-Barbearia"))
        .license(new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT"));
  }

  private List<Server> apiServers() {
    Server devServer = new Server()
        .url("http://localhost:8080")
        .description("Servidor de Desenvolvimento");

    Server prodServer = new Server()
        .url("https://sua-barbearia.onrender.com")
        .description("Servidor de Produção (Render)");

    Server azureServer = new Server()
        .url("https://sua-barbearia-g7f7ftc4f6ewbkch.centralus-01.azurewebsites.net/")
        .description("Servidor de Produção (Azure)");

    return List.of(devServer, prodServer, azureServer);
  }
}
