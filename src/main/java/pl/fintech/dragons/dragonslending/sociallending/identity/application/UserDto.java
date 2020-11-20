package pl.fintech.dragons.dragonslending.sociallending.identity.application;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UserDto {
    UUID id;
    String email;
    String username;
    String firstName;
    String lastName;
}
