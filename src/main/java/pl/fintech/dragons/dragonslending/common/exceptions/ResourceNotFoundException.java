package pl.fintech.dragons.dragonslending.common.exceptions;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException() {
    super("Not found resource");
  }

  public ResourceNotFoundException(String msg) {
    super(msg);
  }

}
