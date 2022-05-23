package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@org.springframework.context.annotation.Configuration
@ComponentScan("app")
public class Config {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BufferedReader getBufferedReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}
