package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.initializer;

import org.springframework.http.ResponseEntity;
import pl.fintech.dragons.dragonslending.common.errors.InternalServerErrorException;

import java.net.URI;
import java.util.UUID;

class IdentityLocationHeaderRetriever {

    UUID retrieve(ResponseEntity<Void> responseEntity) {
        URI location = responseEntity.getHeaders().getLocation();
        if (location == null) {
            throw new InternalServerErrorException("Could not retrieve accountId during account creation process");
        }
        String locationUrl = location.getRawPath();
        String id = locationUrl.substring(locationUrl.lastIndexOf("/") + 1);
        return UUID.fromString(id);
    }
}
