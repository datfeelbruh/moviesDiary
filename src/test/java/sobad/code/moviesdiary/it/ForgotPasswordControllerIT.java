package sobad.code.moviesdiary.it;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import sobad.code.moviesdiary.ConfigForTests;
import sobad.code.moviesdiary.dtos.ResponseMessage;
import sobad.code.moviesdiary.dtos.ResetPasswordDto;
import sobad.code.moviesdiary.dtos.tokens.ResetPasswordTokenDto;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.repositories.ResetPasswordTokenRepository;
import sobad.code.moviesdiary.repositories.UserRepository;
import sobad.code.moviesdiary.utils.TestUtils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.moviesdiary.ConfigForTests.TEST_PROFILE;
import static sobad.code.moviesdiary.controllers.ForgotPasswordController.API_FORGOT_PASSWORD_RESET;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ConfigForTests.class)
@ExtendWith(SpringExtension.class)
@Slf4j
@ActiveProfiles(TEST_PROFILE)
class ForgotPasswordControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    void setUp() {
        resetPasswordTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createResetPasswordToken() throws Exception {
        testUtils.createSampleUser();
        User user = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder request = get(API_FORGOT_PASSWORD_RESET)
                .param("email", user.getEmail());

        ResultActions resultActions = mockMvc.perform(request).andExpect(status().isOk());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        ResetPasswordTokenDto resetPasswordTokenResponse = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(resetPasswordTokenRepository.findByToken(resetPasswordTokenResponse.getResetToken())).isNotNull();
    }

    @Test
    void updatePassword() throws Exception {
        testUtils.createSampleUser();
        User user = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder request = get(API_FORGOT_PASSWORD_RESET)
                .param("email", user.getEmail());

        ResultActions resultActions = mockMvc.perform(request).andExpect(status().isOk());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        ResetPasswordTokenDto resetPasswordTokenResponse = TestUtils.readJson(content, new TypeReference<>() { });
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto("new password");

        MockHttpServletRequestBuilder updatePassword = post(API_FORGOT_PASSWORD_RESET)
                .param("token", resetPasswordTokenResponse.getResetToken())
                .content(TestUtils.writeJson(resetPasswordDto))
                .contentType(APPLICATION_JSON);

        ResultActions updateResult = mockMvc.perform(updatePassword).andExpect(status().isOk());
        String updateContent = updateResult.andReturn().getResponse().getContentAsString(UTF_8);
        ResponseMessage responseMessage = TestUtils.readJson(updateContent, new TypeReference<>() { });

        assertThat(responseMessage).isNotNull();
        assertThat(userRepository.findAll().get(0).getPassword()).isNotEqualTo(user.getPassword());
    }
}
