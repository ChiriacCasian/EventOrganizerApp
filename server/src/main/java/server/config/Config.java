/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Configuration
public class Config {

    /**
     * Provides a bean for generating random numbers.
     * This bean is used to create an instance of the Random class.
     *
     * @return a Random instance for generating random numbers
     */
    @Bean
    public Random getRandom() {
        return new Random();
    }

    /**
     * Provides a bean for the RestTemplate class.
     *
     * @return a RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Provides a bean for the YAMLMapper class.
     *
     * @return a YAMLMapper instance
     */
    @Bean
    public YAMLMapper yamlMapper() {
        return new YAMLMapper(new YAMLFactory());
    }


    /**
     * Provides a bean for the ObjectMapper class.
     *
     * @return an ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}