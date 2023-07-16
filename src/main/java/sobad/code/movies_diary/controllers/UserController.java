package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.dto.user.UserDtoResponse;
import sobad.code.movies_diary.service.UserService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с пользователем")
public class UserController {
    private final UserService userService;
    public static final String USER_CONTROLLER_PATH = "/api/users";

    @RequestMapping(value = USER_CONTROLLER_PATH, method = POST)
    public ResponseEntity<?> createUser(@RequestBody AuthRegistrationRequest authRegistrationRequest) {
        UserDtoResponse userDto = userService.createUser(authRegistrationRequest);
        return new ResponseEntity<>(userDto, CREATED);
    }


}
