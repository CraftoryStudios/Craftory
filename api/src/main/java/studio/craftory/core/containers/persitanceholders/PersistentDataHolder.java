package studio.craftory.core.containers.persitanceholders;

import org.bukkit.persistence.PersistentDataContainer;

public interface PersistentDataHolder {

  /**
   * Returns the persistent DataHolder of the object.
   *
   * @return the DataHolder
   */
  PersistentDataContainer getPersistentData();
}
