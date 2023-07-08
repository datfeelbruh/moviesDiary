package sobad.code.movies_diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.dto.MovieDtoResponse;
import sobad.code.movies_diary.dto.PictureDto;
import sobad.code.movies_diary.dto.UserDtoResponse;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с пользователем")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    public static final String USER_CONTROLLER_USER_PROFILE = "/api/user/{username}";

    @Operation(summary = "Получить профиль пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя", content =
            @Content(schema =
            @Schema(implementation = UserDtoResponse.class))
            )
    })
    @GetMapping(USER_CONTROLLER_USER_PROFILE)
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        try {
            UserDtoResponse userDtoResponse = userService.getUserProfile(username);
            return new ResponseEntity<>(userDtoResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            String.format(
                                    "Такого пользователя '%s' не существует",
                                    username
                            )),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
