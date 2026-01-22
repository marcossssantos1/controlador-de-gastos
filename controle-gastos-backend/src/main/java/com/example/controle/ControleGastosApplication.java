package com.example.controle;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "API de Controle de Gastos",
        version = "1.0",
        description = "API REST para gerenciamento de gastos pessoais com autenticação JWT, " +
                     "paginação, filtros avançados, dashboard e relatórios em PDF",
        contact = @Contact(
            name = "Equipe de Desenvolvimento",
            email = "contato@controlegastos.com"
        )
    )
)
@SecurityScheme(
    name = "bearer-jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class ControleGastosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControleGastosApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🚀 Sistema de Controle de Gastos iniciado!");
        System.out.println("📊 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("📄 API Docs: http://localhost:8080/api-docs");
        System.out.println("========================================\n");
    }
}
