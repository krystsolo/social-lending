package pl.fintech.dragons.dragonslending.identity.application.web;

import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Value
public class UserRegisterRequest {
    @Email
    @Length(min = 3, max = 255)
    String email;

    @Length(min = 3, max = 255)
    String username;

    @Length(min = 3, max = 255)
    String password;

    @Length(min = 1, max = 255)
    String firstName;

    @Length(min = 1, max = 255)
    String lastName;
}
