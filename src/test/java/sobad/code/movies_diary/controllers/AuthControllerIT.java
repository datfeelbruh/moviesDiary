package sobad.code.movies_diary.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.movies_diary.TestConfig;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_LOGIN_PATH;
import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_REG_PATH;
import static sobad.code.movies_diary.utils.TestUtils.ANOTHER_USER_AUTH;
import static sobad.code.movies_diary.utils.TestUtils.SAMPLE_USER_AUTH;
import static sobad.code.movies_diary.utils.TestUtils.SAMPLE_USER_REG;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestConfig.class)
public class AuthControllerIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    public void beforeEach() {
        testUtils.clearAll();
        testUtils.setRoles();
    }

    @Test
    public void registeringUser() throws Exception {
        MockHttpServletRequestBuilder reg = post(AUTH_CONTROLLER_REG_PATH)
                .content(TestUtils.writeJson(SAMPLE_USER_REG))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(reg).andExpect(status().isCreated());

        assertThat(userRepository.findByUsername(SAMPLE_USER_REG.getUsername()).get().getUsername())
                .isEqualTo(SAMPLE_USER_REG.getUsername());
        assertThat(userRepository.findByUsername(SAMPLE_USER_REG.getUsername()).get()).isNotNull();
    }

    @Test
    public void authRegisteredUser() throws Exception {
        testUtils.regSampleUser();

        MockHttpServletRequestBuilder reg = post(AUTH_CONTROLLER_LOGIN_PATH)
                .content(TestUtils.writeJson(SAMPLE_USER_AUTH))
                .contentType(APPLICATION_JSON);

        MockHttpServletResponse response = mockMvc.perform(reg).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void authUnregisteredUser() throws Exception {
        testUtils.regSampleUser();

        MockHttpServletRequestBuilder reg = post(AUTH_CONTROLLER_LOGIN_PATH)
                .content(TestUtils.writeJson(ANOTHER_USER_AUTH))
                .contentType(APPLICATION_JSON);

        MockHttpServletResponse response = mockMvc.perform(reg).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(401);
    }

}
