package studio.craftory.core.api;

import java.util.Optional;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegister;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CustomBlockKey;
import studio.craftory.core.utils.Log;

@UtilityClass
public class CustomBlockAPI {

  @Inject
  private CustomBlockRegister customBlockRegister;
  @Inject
  private CustomBlockManager customBlockManager;

  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> customBlock) {
    customBlockRegister.registerCustomBlock(plugin, customBlock);
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull Class<? extends BaseCustomBlock> customBlockClazz,
  @NonNull CraftoryDirection craftoryDirection) {
    Optional<CustomBlockKey> key = customBlockRegister.getKey(customBlockClazz);
    if (!key.isPresent()) {
      Log.warn("Tried to place a custom block that doesn't exist or isn't registered");
      return Optional.empty();
    }
    return customBlockManager.placeCustomBlock(key.get(),location,craftoryDirection);
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull Class<? extends BaseCustomBlock> customBlockClazz) {
    return placeCustomBlock(location, customBlockClazz, CraftoryDirection.NORTH);
  }

}
