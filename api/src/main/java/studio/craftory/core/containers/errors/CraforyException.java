package studio.craftory.core.containers.errors;

public class CraforyException extends RuntimeException {

  public CraforyException(String message) {
    super(message);
  }

  public CraforyException(String message, Throwable og) {
    super(message, og);
  }

}
