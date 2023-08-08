package sobad.code.moviesdiary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sobad.code.moviesdiary.dtos.user.UserDtoAboutRequest;
import sobad.code.moviesdiary.dtos.user.UserRegistrationDtoRequest;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;
import sobad.code.moviesdiary.exceptions.AppError;
import sobad.code.moviesdiary.services.UserService;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с пользователем", description = "API Пользователей")
public class UserController {
    private final UserService userService;
    public static final String USER_CONTROLLER_PATH = "/api/users";

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
    @PostMapping(value = USER_CONTROLLER_PATH)
    public ResponseEntity<UserDtoResponse> createUser(@RequestBody UserRegistrationDtoRequest authRegistrationRequest) {
        UserDtoResponse userDto = userService.createUser(authRegistrationRequest);
        return new ResponseEntity<>(userDto, CREATED);
    }

    @Operation(summary = "Получить пользователя", description = """
            Эндпоинт для получения информации о пользователе.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Существующий пользователь",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Пользователь не найден.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @GetMapping(value = USER_CONTROLLER_PATH + "/{userId}")
    public ResponseEntity<UserDtoResponse> getUser(@PathVariable(value = "userId")
                                         @Parameter(description = "ID пользователя", example = "1") Long userId) {
        UserDtoResponse user = userService.getUserById(userId);
        return new ResponseEntity<>(user, OK);
    }

    @Operation(summary = "Загрузить аватар пользователя.", description = """
            Эндпоинт предназначен для загрузки изображение через form-data. Поддерживает форматы: jpeg, jpg, png, gif.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Обновленная информация о пользователе.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Не удалось загрузить изображение.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @PostMapping(value = USER_CONTROLLER_PATH + "/avatar", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDtoResponse> uploadAvatar(@RequestPart("image") MultipartFile multipartFile) throws IOException {
        return new ResponseEntity<>(userService.uploadImage(multipartFile), OK);
    }

    @Operation(summary = "Обновить описание пользователя.", description = """
            Эндпоинт предназначен для обновление поля about в сущности пользователя.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Обновленная информация о пользователе.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Не удалось обновить информацию о пользователе.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @PutMapping(value = USER_CONTROLLER_PATH + "/{userId}")
    public ResponseEntity<UserDtoResponse> updateAbout(@PathVariable(value = "userId")
                                         @Parameter(description = "ID пользователя", example = "1") Long userId,
                                         @RequestBody UserDtoAboutRequest aboutRequest) {
        return new ResponseEntity<>(userService.updateAbout(userId, aboutRequest), OK);
    }
}
