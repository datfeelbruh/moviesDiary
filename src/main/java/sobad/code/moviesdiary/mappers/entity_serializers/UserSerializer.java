package sobad.code.moviesdiary.mappers.entity_serializers;

import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;
import sobad.code.moviesdiary.entities.User;

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
