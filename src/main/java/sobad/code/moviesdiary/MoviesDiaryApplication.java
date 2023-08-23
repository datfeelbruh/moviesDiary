package sobad.code.moviesdiary;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sobad.code.moviesdiary.hibernateSearch.Indexer;

@SpringBootApplication
public class MoviesDiaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoviesDiaryApplication.class, args);
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner buildIndex(Indexer indexer) throws Exception {
        return (ApplicationArguments args) -> {
            indexer.indexData("sobad.code.moviesdiary.entities.Movie");
        };
    }

}
