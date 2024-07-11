package br.com.estudo.testes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customApi() {
        return new OpenAPI().info(
            new Info()
                .title("Estudo de Testes.")
                .version("v1")
                    .description("Api feita para estudo de testes,\n" +
                        "Testes Unitários com JUnit,\n" +
                        "Estudo de Mocks com Mockito e Hamcrest,\n" +
                        "Estudo de teste em camadas (repositories, services e controllers),\n" +
                        "Testes de integração com Testcontainers.")
                .termsOfService("00.00.00")
                .license(new License().name("00.00.00"))
        );
    }

}
