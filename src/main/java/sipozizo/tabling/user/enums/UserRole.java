package sipozizo.tabling.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "사용자"),
    OWNER("ROLE_OWNER", "사장님");

    private final String role;
    private final String description;
}