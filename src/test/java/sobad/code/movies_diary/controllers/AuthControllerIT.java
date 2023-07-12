//package sobad.code.movies_diary.controllers;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import sobad.code.movies_diary.AppError;
//import sobad.code.movies_diary.authentication.AuthLoginRequest;
//import sobad.code.movies_diary.authentication.AuthTokenResponse;
//import sobad.code.movies_diary.entities.User;
//import sobad.code.movies_diary.jwts.Token;
//import sobad.code.movies_diary.repositories.TokenRepository;
//import sobad.code.movies_diary.repositories.UserRepository;
//import sobad.code.movies_diary.utils.TestUtilsIT;
//
//import java.util.List;
//import java.util.Optional;
//
//import static java.nio.charset.StandardCharsets.UTF_8;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//import static org.springframework.http.HttpStatus.UNAUTHORIZED;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_LOGIN_PATH;
//import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_REFRESH_TOKEN_PATH;
//
//
//@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfig.class)
//public class AuthControllerIT {
//    @Autowired
//    private TestUtilsIT testUtilsIT;
//
//    @Autowired
//    private TokenRepository tokenRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    void authCorrectUser() throws Exception {
//        testUtilsIT.createSampleUser();
//
//        AuthLoginRequest userAuth = TestUtilsIT.readJson(
//                TestUtilsIT.readFixture("auth/sampleAuth.json"),
//                new TypeReference<>() {
//                }
//        );
//
//        MockHttpServletRequestBuilder request = post(AUTH_CONTROLLER_LOGIN_PATH)
//                .content(TestUtilsIT.writeJson(userAuth))
//                .contentType(APPLICATION_JSON);
//
//        ResultActions resultActions = testUtilsIT.performUnsecuredRequest(request);
//
//        resultActions.andExpect(status().isOk());
//
//        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
//        AuthTokenResponse tokenResponse = TestUtilsIT.readJson(content, new TypeReference<>(){});
//
//        Optional<Token> tokenFromDb = tokenRepository.findByAccessToken(tokenResponse.getAccessToken());
//
//        assertThat(tokenFromDb).isPresent();
//        assertThat(tokenFromDb.get().getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
//        assertThat(tokenFromDb.get().expired).isFalse();
//        assertThat(tokenFromDb.get().revoked).isFalse();
//    }
//
//    @Test
//    void authUserWhichDoesNotRegistered() throws Exception {
//        AuthLoginRequest userAuth = TestUtilsIT.readJson(
//                TestUtilsIT.readFixture("auth/anotherAuth.json"),
//                new TypeReference<>() {}
//        );
//
//        MockHttpServletRequestBuilder request = post(AUTH_CONTROLLER_LOGIN_PATH)
//                .content(TestUtilsIT.writeJson(userAuth))
//                .contentType(APPLICATION_JSON);
//
//        ResultActions resultActions = testUtilsIT.performUnsecuredRequest(request);
//
//        resultActions.andExpect(status().isUnauthorized());
//
//        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
//        AppError appError = TestUtilsIT.readJson(content, new TypeReference<>(){});
//
//        List<Token> tokens = tokenRepository.findAll();
//
//        assertThat(tokens).isEmpty();
//        assertThat(appError.getMessage()).isEqualTo("Не удалось авторизироваться с такими данными");
//        assertThat(appError.getStatusCode()).isEqualTo(UNAUTHORIZED.value());
//    }
//
//    @Test
//    void testGetNewAccessTokenByRefreshToken() throws Exception {
//        testUtilsIT.createSampleUser();
//
//        User user = userRepository.findAll().get(0);
//
//        AuthLoginRequest userAuth = TestUtilsIT.readJson(
//                TestUtilsIT.readFixture("auth/sampleAuth.json"),
//                new TypeReference<>() {}
//        );
//
//        MockHttpServletRequestBuilder requestPost = post(AUTH_CONTROLLER_LOGIN_PATH)
//                .content(TestUtilsIT.writeJson(userAuth))
//                .contentType(APPLICATION_JSON);
//
//        ResultActions resultActionsPost = testUtilsIT.performUnsecuredRequest(requestPost);
//
//        String content = resultActionsPost.andReturn().getResponse().getContentAsString(UTF_8);
//        AuthTokenResponse tokenResponse = TestUtilsIT.readJson(content, new TypeReference<>(){});
//
//        MockHttpServletRequestBuilder requestGet = get(AUTH_CONTROLLER_REFRESH_TOKEN_PATH)
//                .header(AUTHORIZATION, "Bearer " + tokenResponse.getRefreshToken());
//
//        ResultActions resultActionsGet = testUtilsIT.performUnsecuredRequest(requestGet);
//
//        resultActionsGet.andExpect(status().isOk());
////        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
////
////        AuthTokenResponse authTokenResponse = TestUtilsIT.readJson(content, new TypeReference<>(){});
////
////        token = tokenRepository.findAll().get(1);
////
////        assertThat(authTokenResponse.getAccessToken()).isEqualTo(token.getAccessToken());
////        assertThat(authTokenResponse.getRefreshToken()).isEqualTo(token.getRefreshToken());
//
//    }
//
//}
