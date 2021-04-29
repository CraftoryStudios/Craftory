package studio.craftory.core.data.errors;

public class CraforyException extends RuntimeException {

  public CraforyException(String message) {
    super(message);
  }

  public CraforyException(String message, Throwable og) {
    super(message, og);
  }

}
