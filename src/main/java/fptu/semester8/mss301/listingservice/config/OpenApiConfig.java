package fptu.semester8.mss301.listingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI listingServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Listing Service API")
                .version("v1")
                .description("API for managing buildings, rooms, and their rental listings."));
    }
}
