package studio.craftory.core.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.keys.CustomBlockKey;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.executors.interfaces.Tickable;
import studio.craftory.core.utils.Log;

/**
 * Manages internally the registered CustomBlock types.
 */
/** Class based on LogisticsCraft's Logistics-API (MIT) and the LogisticsTypeRegister class **/
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CustomBlockRegister {

  @Inject
  private AsyncExecutionManager asyncExecutionManager;
  @Inject
  private SyncExecutionManager syncExecutionManager;

  private Map<CustomBlockKey, Class<? extends BaseCustomBlock>> blockTypes = new HashMap<>();

  @Synchronized
  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> block) {
    if (blockTypes.putIfAbsent(new CustomBlockKey(plugin, block), block) == null) {

      if (Tickable.class.isAssignableFrom(block)) {
        registerCustomBlockTickables((Class<? extends Tickable>) block);
      }

      Log.debug("CustomBlock Registert: " + block.getName());
      //Bukkit.getPluginManager().callEvent(new BlockRegisterEvent(new CustomBlockKey(plugin, name), block));
    } else {
      Log.warn("Trying to reregister known key: " + new CustomBlockKey(plugin, block).getName());
    }
  }

  private void registerCustomBlockTickables(Class<? extends Tickable> block) {
    asyncExecutionManager.registerTickableClass(block);
    syncExecutionManager.registerTickableClass(block);
  }

  @Synchronized
  public boolean isBlockRegistered(@NonNull BaseCustomBlock block) {
    return blockTypes.containsValue(block.getClass());
  }

  @Synchronized
  public Optional<CustomBlockKey> getKey(@NonNull BaseCustomBlock block) {
    return getKey(block.getClass());
  }

  @Synchronized
  public Optional<CustomBlockKey> getKey(@NonNull Class<? extends BaseCustomBlock> block) {
    for (Entry<CustomBlockKey, Class<? extends BaseCustomBlock>> entry : blockTypes.entrySet()) {
      if (entry.getValue() == block) {
        return Optional.of(entry.getKey());
      }
    }
    return Optional.empty();
  }
}
