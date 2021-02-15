package studio.craftory.core.api;

import studio.craftory.core.items.CustomItem;
import studio.craftory.core.items.CustomItemManager;

public class CustomItemAPI {

  /* Registering */
  public static void registerCustomItem(CustomItem item) {
    CustomItemManager.registerCustomItem(item);
  }
}
