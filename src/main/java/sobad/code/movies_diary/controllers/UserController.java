package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sobad.code.movies_diary.dtos.user.UserDtoAboutRequest;
import sobad.code.movies_diary.dtos.user.UserRegistrationDtoRequest;
import sobad.code.movies_diary.dtos.user.UserDtoResponse;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.services.UserService;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с пользователем", description = "API Пользователей")
public class UserController {
    private final UserService userService;
    public static final String USER_CONTROLLER_PATH = "/api/users";
    public static final String USER_CONTROLLER_PATH_RESET_PASSWORD = "/api/users/reset";

    @Operation(summary = "Зарегистрировать нового пользователя", description = """
            С помощью этого метода можно зарегистрировать нового пользователя.
            """)
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
            description = "Не удалось создать пользователя.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @RequestMapping(value = USER_CONTROLLER_PATH, method = POST)
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDtoRequest authRegistrationRequest) {
        UserDtoResponse userDto = userService.createUser(authRegistrationRequest);
        return new ResponseEntity<>(userDto, CREATED);
    }

    @PostMapping(value = USER_CONTROLLER_PATH + "/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        return new ResponseEntity<>(userService.uploadImage(multipartFile), OK);
    }

    @PutMapping(value = USER_CONTROLLER_PATH + "/{userId}")
    public ResponseEntity<?> updateAbout(@PathVariable(value = "userId") Long userId,
                                         @RequestBody UserDtoAboutRequest aboutRequest) {
        return new ResponseEntity<>(userService.updateAbout(userId, aboutRequest), OK);
    }

    @PostMapping(value = USER_CONTROLLER_PATH_RESET_PASSWORD)
    public ResponseEntity<?> updateAbout(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok(email);
    }
}
