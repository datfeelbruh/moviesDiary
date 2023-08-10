package sobad.code.moviesdiary;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
class ImageUtilsTest {
    private ImageUtils imageUtils;
    private static final String IMAGE_DIRECTORY = "C:\\Users\\datfe\\Pictures\\service-images";
    private MockMultipartFile file;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        imageUtils = new ImageUtils(IMAGE_DIRECTORY);

        file = new MockMultipartFile(
                "file", "", "image/png", new byte[] {1});
        file.transferTo(new File(
                IMAGE_DIRECTORY + "/" + "username" + "_" + "avatar" + "." + "png"));
    }

    @BeforeAll
    static void beforeAll() {
        try (Stream<Path> entries = Files.walk(Path.of(IMAGE_DIRECTORY))) {
            entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
        }
    }


    @AfterAll
    static void clear() {
        try (Stream<Path> entries = Files.walk(Path.of(IMAGE_DIRECTORY))) {
            entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
        }
    }

    @Test
    void getImage() {
        String link = "username_avatar.png";
        File image = imageUtils.getImage(link);

        assertThat(image).isNotNull();
    }

    @Test
    @SneakyThrows
    void deletePreviousUserImage() {
        String prefix = "username";

        List<File> files = new ArrayList<>();
        Path path = Path.of(IMAGE_DIRECTORY);

        try (Stream<Path> entries = Files.walk(path)) {
            files = entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();
        } catch (IOException e) {
        }

        assertThat(files).isNotEmpty();

        imageUtils.deletePreviousUserImage(prefix);

        try (Stream<Path> entries = Files.walk(path)) {
            files = entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();
        } catch (IOException e) {
        }

        assertThat(files).isEmpty();
    }

    @Test
    void buildFile() {
        String contentType = file.getContentType();
        String username = "username";

        String filepath = imageUtils.buildFile(contentType, username);

        assertThat(filepath).isEqualTo(IMAGE_DIRECTORY + "/" + "username" + "_" + "avatar" + "." + "png");
    }

    @Test
    void isSupportedContentType() {
        String unsupportedContentType = "image/mp4";
        String supportedContentType = "image/gif";

        boolean checkNegative = ImageUtils.isSupportedContentType(unsupportedContentType);
        boolean checkPositive = ImageUtils.isSupportedContentType(supportedContentType);

        assertTrue(checkPositive);
        assertFalse(checkNegative);
    }
}
