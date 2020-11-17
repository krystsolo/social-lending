package pl.fintech.dragons.dragonslending.identity.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import pl.fintech.dragons.dragonslending.identity.application.UserDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "user_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    private UUID id;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @Length(min = 3, max = 255)
    @Column(unique = true)
    private String username;

    @Length(min = 1, max = 255)
    @Column(name = "first_name")
    private String firstName;

    @Length(min = 1, max = 255)
    @Column(name = "last_name")
    private String lastName;

    @Length(min = 3, max = 255)
    private String password;

    User(String email, String username, String firstName,
                String lastName, String password) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public UserDto toDto() {
        return UserDto.builder()
                .firstName(firstName)
                .email(email)
                .lastName(lastName)
                .id(id)
                .username(username)
                .build();
    }
}
