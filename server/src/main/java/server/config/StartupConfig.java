package server.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

    private final OpenApiSpecService openApiSpecService;

    /**
     * Constructor for the StartupConfig class.
     *
     * @param openApiSpecService the OpenApiSpecService instance
     */
    public StartupConfig(OpenApiSpecService openApiSpecService) {
        this.openApiSpecService = openApiSpecService;
    }

    /**
     * Generates a YAML file from a JSON file.
     *
     * @return a CommandLineRunner instance
     */
    @Bean
    public CommandLineRunner generateYamlFromJsonRunner() {
        return args -> openApiSpecService.generateYamlFromJson();
    }
}
