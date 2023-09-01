package sobad.code.moviesdiary.it;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.ConfigForTests;
import sobad.code.moviesdiary.dtos.ResponseMessage;
import sobad.code.moviesdiary.dtos.user.UserDtoLoginRequest;
import sobad.code.moviesdiary.dtos.authentication.AuthTokenDtoResponse;
import sobad.code.moviesdiary.repositories.DeactivatedTokenRepository;
import sobad.code.moviesdiary.repositories.TokenRepository;
import sobad.code.moviesdiary.repositories.UserRepository;
import sobad.code.moviesdiary.utils.TestUtils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.moviesdiary.ConfigForTests.TEST_PROFILE;
import static sobad.code.moviesdiary.controllers.AuthController.AUTH_CONTROLLER_LOGIN_PATH;
import static sobad.code.moviesdiary.controllers.AuthController.AUTH_CONTROLLER_REFRESH_TOKEN_PATH;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ConfigForTests.class)
@ExtendWith(SpringExtension.class)
@Slf4j
@ActiveProfiles(TEST_PROFILE)
class AuthControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private DeactivatedTokenRepository deactivatedTokenRepository;

    @Test
    @Transactional
    void authCorrectUser() throws Exception {
        testUtils.createSampleUser();

        UserDtoLoginRequest userAuth = TestUtils.readJson(
                TestUtils.readFixture("auth/sampleAuth.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder request = post(AUTH_CONTROLLER_LOGIN_PATH)
                .content(TestUtils.writeJson(userAuth))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(status().isOk());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        AuthTokenDtoResponse tokenResponse = TestUtils.readJson(content, new TypeReference<>() { });
        assertThat(tokenResponse.getAccessToken())
                .isEqualTo(tokenRepository.findByAccessToken(tokenResponse.getAccessToken()).get().getAccessToken());
    }

    @Test
    @Transactional
    void authUserWhichDoesNotRegistered() throws Exception {
        UserDtoLoginRequest userAuth = TestUtils.readJson(
                TestUtils.readFixture("auth/anotherAuth.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(AUTH_CONTROLLER_LOGIN_PATH)
                .content(TestUtils.writeJson(userAuth))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(status().isUnauthorized());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        ResponseMessage appError = TestUtils.readJson(content, new TypeReference<>() { });


        assertThat(appError.getStatusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @Transactional
    void refreshToken() throws Exception {
        testUtils.createSampleUser();

        UserDtoLoginRequest userAuth = TestUtils.readJson(
                TestUtils.readFixture("auth/sampleAuth.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder authRequest = post(AUTH_CONTROLLER_LOGIN_PATH)
                .content(TestUtils.writeJson(userAuth))
                .contentType(APPLICATION_JSON);

        ResultActions authResult = mockMvc.perform(authRequest).andExpect(status().isOk());

        String authContent = authResult.andReturn().getResponse().getContentAsString(UTF_8);
        AuthTokenDtoResponse authTokenResponse = TestUtils.readJson(authContent, new TypeReference<>() { });

        MockHttpServletRequestBuilder refreshRequest = get(AUTH_CONTROLLER_REFRESH_TOKEN_PATH)
                .header(AUTHORIZATION, "Bearer " + authTokenResponse.getAccessToken());

        ResultActions refreshResult = mockMvc.perform(refreshRequest).andExpect(status().isOk());
        String refreshContent = refreshResult.andReturn().getResponse().getContentAsString(UTF_8);

        AuthTokenDtoResponse refreshToken = TestUtils.readJson(refreshContent, new TypeReference<>() { });

        assertThat(refreshToken.getAccessToken()).isNotEqualTo(tokenRepository.findAll().get(0).getAccessToken());
        assertThat(authTokenResponse.getAccessToken())
                .isEqualTo(deactivatedTokenRepository.findAll().get(0).getToken());
    }
}
