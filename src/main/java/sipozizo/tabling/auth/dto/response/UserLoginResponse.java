package sipozizo.tabling.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sipozizo.tabling.user.entity.User;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

    private String message;
    private String AccessToken;

    public static UserLoginResponse from(User user, String accessToken) {
        return new UserLoginResponse(
                user.getName() + "님 어서오세요",
                accessToken
        );
    }
}
