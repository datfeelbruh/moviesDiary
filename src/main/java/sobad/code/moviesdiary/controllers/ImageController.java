package sobad.code.moviesdiary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.moviesdiary.ImageUtils;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;
import sobad.code.moviesdiary.exceptions.AppError;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageUtils imageUtils;
    public static final String IMAGE_CONTROLLER_PATH = "/api/image";
    @Operation(summary = "Эндпоинт получение аватара пользователя.", description = """
            Эндпоинт предназначен для получение аватара пользователя по уникальной ссылке на аватар.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Страница аватара пользователя.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserDtoResponse.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "422",
            description = "Нет аватара для данной ссылки.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @GetMapping(value = IMAGE_CONTROLLER_PATH + "/{imageName}")
    public ResponseEntity<byte[]> testGetImage(@PathVariable(value = "imageName")
                                          @Parameter(description = "Ссылка на аватар пользователя",
                                                  example = "{username}_avatar.ext")
                                              String imageName) throws IOException {
        File image = imageUtils.getImage(imageName);
        byte[] bytes = StreamUtils.copyToByteArray(image.toURI().toURL().openStream());
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}

