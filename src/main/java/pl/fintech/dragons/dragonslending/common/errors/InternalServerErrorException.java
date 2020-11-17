package pl.fintech.dragons.dragonslending.common.errors;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }
}
