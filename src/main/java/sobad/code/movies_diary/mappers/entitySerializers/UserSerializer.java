package sobad.code.movies_diary.mappers.entitySerializers;

import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.user.UserDtoResponse;
import sobad.code.movies_diary.entities.User;

import java.util.function.Function;

@Component
public class UserSerializer implements Function<User, UserDtoResponse> {
    @Override
    public UserDtoResponse apply(User user) {
        return UserDtoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .about(user.getAbout())
                .avatar(user.getAvatar())
                .build();
    }
}
