package studio.craftory.core.data.errors;

public class CraftoryItemHasNoMetaException extends CraforyException{

  public CraftoryItemHasNoMetaException(String message) {
    super(message);
  }

  public CraftoryItemHasNoMetaException(String message, Throwable og) {
    super(message, og);
  }

}
