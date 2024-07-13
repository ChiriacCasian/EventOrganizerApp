package server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class OpenApiSpecService {

    @Value("${server.host}")
    private String serverHost;
    @Value("${server.port}")
    private String serverPort;

    private final RestTemplate restTemplate;
    private final YAMLMapper yamlMapper;

    /**
     * Constructor for the OpenApiSpecService class.
     *
     * @param restTemplate the RestTemplate instance
     * @param yamlMapper   the YAMLMapper instance
     */
    public OpenApiSpecService(RestTemplate restTemplate, YAMLMapper yamlMapper) {
        this.restTemplate = restTemplate;
        this.yamlMapper = yamlMapper;
    }

    /**
     * Generates a YAML file from a JSON file.
     *
     * @throws IOException if an I/O error occurs
     */
    @EventListener(ApplicationReadyEvent.class)
    public void generateYamlFromJson() throws IOException {
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serverHost +
                ":" + serverPort + "/v3/api-docs", String.class);
        JsonNode jsonNodeTree = new ObjectMapper().readTree(response.getBody());
        String yaml = yamlMapper.writeValueAsString(jsonNodeTree);
        File file = new File("openapi.yaml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(yaml);
        }
    }
}