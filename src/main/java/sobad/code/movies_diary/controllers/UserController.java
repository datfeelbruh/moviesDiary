package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.dto.user.UserDtoResponse;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.service.UserService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с пользователем", description = "API Пользователей")
public class UserController {
    private final UserService userService;
    public static final String USER_CONTROLLER_PATH = "/api/users";

    @Operation(summary = "Зарегистрировать нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Новый пользователь зарегистрирован",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Пользователь с таким именем уже существует",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Пароль и потверждающий пароль не совпадают",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @RequestMapping(value = USER_CONTROLLER_PATH, method = POST)
    public ResponseEntity<?> createUser(@RequestBody AuthRegistrationRequest authRegistrationRequest) {
        UserDtoResponse userDto = userService.createUser(authRegistrationRequest);
        return new ResponseEntity<>(userDto, CREATED);
    }

}
