package pl.fintech.dragons.dragonslending.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
public final class HeaderUtil {

  public static HttpHeaders createAlert(String applicationName, String message, String param) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(String.format("X-%s-alert", applicationName), message);
    headers.add(String.format("X-%s-params", applicationName), param);
    return headers;
  }

  public static HttpHeaders createEntityCreationAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
    String message = enableTranslation ? String.format("%s.%s.created",applicationName,entityName) : String.format("A new %s is created with identifier %s", entityName, param);
    return createAlert(applicationName, message, param);
  }

  public static HttpHeaders createEntityUpdateAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
    String message = enableTranslation ? String.format("%s.%s.updated", applicationName, entityName) : String.format("A %s is updated with identifier %s", entityName, param);
    return createAlert(applicationName, message, param);
  }

  public static HttpHeaders createEntityDeletionAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
    String message = enableTranslation ? String.format("%s.%s.deleted", applicationName, entityName) : String.format("A %s is deleted with identifier %s", entityName, param);
    return createAlert(applicationName, message, param);
  }

  public static HttpHeaders createFailureAlert(String applicationName, boolean enableTranslation, String entityName, String errorKey, String defaultMessage) {
    log.error("Entity processing failed, {}", defaultMessage);
    String message = enableTranslation ? "error." + errorKey : defaultMessage;
    HttpHeaders headers = new HttpHeaders();
    headers.add(String.format("X-%s-error", applicationName), message);
    headers.add(String.format("X-%s-params", applicationName), entityName);
    return headers;
  }
}

