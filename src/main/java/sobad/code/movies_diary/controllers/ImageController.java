package sobad.code.movies_diary.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.ImageUtils;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageUtils imageUtils;
    public static final String IMAGE_CONTROLLER_PATH = "/api/image";

    @GetMapping(value = IMAGE_CONTROLLER_PATH + "/{imageName}")
    public ResponseEntity<?> testGetImage(@PathVariable(value = "imageName") String imageName) throws IOException {
        File image = imageUtils.getImage(imageName);
        byte[] bytes = StreamUtils.copyToByteArray(image.toURI().toURL().openStream());
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}

