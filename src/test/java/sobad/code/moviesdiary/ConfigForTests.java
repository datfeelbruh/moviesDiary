package sobad.code.moviesdiary;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import static sobad.code.moviesdiary.ConfigForTests.TEST_PROFILE;

@Configuration
@Profile(TEST_PROFILE)
@ComponentScan(basePackages = "sobad.code")
@PropertySource(value = "classpath:/config/application.yaml")
public class ConfigForTests {
    public static final String TEST_PROFILE = "test";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
