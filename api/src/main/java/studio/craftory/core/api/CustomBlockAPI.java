package studio.craftory.core.api;

import java.util.Optional;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegister;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CustomBlockKey;

@UtilityClass
public class CustomBlockAPI {

  @Inject
  private CustomBlockRegister customBlockRegister;
  @Inject
  private CustomBlockManager customBlockManager;

  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> customBlock) {
    customBlockRegister.registerCustomBlock(plugin, customBlock);
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull CustomBlockKey customBlockKey,
  @NonNull CraftoryDirection craftoryDirection) {
    throw new NotImplementedException();
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull CustomBlockKey customBlockKey) {
    return placeCustomBlock(location, customBlockKey, CraftoryDirection.NORTH);
  }

}
