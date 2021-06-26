package studio.craftory.core.containers.errors;

public class CraftoryItemHasNoMetaException extends CraforyException{

  public CraftoryItemHasNoMetaException(String message) {
    super(message);
  }

  public CraftoryItemHasNoMetaException(String message, Throwable og) {
    super(message, og);
  }

}
