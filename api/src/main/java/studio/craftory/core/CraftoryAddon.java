package studio.craftory.core;

import java.net.URL;

public interface CraftoryAddon {

  URL getAddonResources();

  default void craftoryOnEnable() {

  }
}
