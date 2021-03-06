package pl.fintech.dragons.dragonslending.sociallending.identity.application.web;

import lombok.Value;
import lombok.With;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Value
@With
public class UserRegisterRequest {
    @Email
    @Length(min = 3, max = 255)
    @NotNull
    String email;

    @Length(min = 3, max = 255)
    @NotNull
    String username;

    @Length(min = 3, max = 255)
    @NotNull
    String password;

    @Length(min = 1, max = 255)
    @NotNull
    String firstName;

    @Length(min = 1, max = 255)
    @NotNull
    String lastName;
}
