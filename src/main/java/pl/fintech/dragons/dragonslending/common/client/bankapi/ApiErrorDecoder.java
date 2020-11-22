package pl.fintech.dragons.dragonslending.common.client.bankapi;

import feign.Response;
import feign.codec.ErrorDecoder;
import javassist.NotFoundException;
import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;
import pl.fintech.dragons.dragonslending.common.errors.InternalServerErrorException;

public class ApiErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()){
            case 400:
                return new InvalidEndpointRequestException("Bank api exception", "Not proper bankApi api configuration");
            case 404:
                return new NotFoundException("Account number not found in bankApi");
            default:
                return new InternalServerErrorException("Bank api internal exception");
        }
    }
}