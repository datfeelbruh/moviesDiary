package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieInfoDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieShortInfoDto;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.jwts.Token;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieList.MovieList;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.TestUtilsIT;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.ExternalApiController.EXTERNAL_API_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Transactional
public class ExternalApiControllerIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TestUtilsIT testUtilsIT;

    @Test
    void getExternalApiMovieList() throws Exception {
        testUtilsIT.createSampleUser();
        testUtilsIT.authSampleUser();

        User user = userRepository.findAll().get(0);
        String requestParam = "паук";

        MockHttpServletRequestBuilder request = get(EXTERNAL_API_CONTROLLER_PATH)
                .param("name", requestParam);

        ResultActions resultActions = testUtilsIT.performSecuredRequest(request, user);

        resultActions.andExpect(status().isOk());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        List<KinopoiskMovieShortInfoDto> response = TestUtilsIT.readJson(content, new TypeReference<>(){});

        response
                .forEach(e -> assertThat(e.getName()).contains(requestParam));
    }

    @Test
    void getExternalApiMovieInfo() throws Exception {
        testUtilsIT.createSampleUser();
        testUtilsIT.authSampleUser();

        final Long randomId = 561L;

        User user = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder request = get(EXTERNAL_API_CONTROLLER_PATH)
                .param("kpId", String.valueOf(randomId));

        ResultActions resultActions = testUtilsIT.performSecuredRequest(request, user);

        resultActions.andExpect(status().isOk());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        KinopoiskMovieInfoDto response = TestUtilsIT.readJson(content, new TypeReference<>(){});

        assertThat(response.getKpId()).isEqualTo(randomId);
    }
}
