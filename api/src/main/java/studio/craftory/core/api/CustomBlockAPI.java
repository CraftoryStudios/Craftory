package studio.craftory.core.api;

import java.util.Optional;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegistry;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.blocks.rendering.renderers.DefaultRenderer;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.containers.keys.CraftoryDataKey;
import studio.craftory.core.resourcepack.AssetLinker;
import studio.craftory.core.utils.Log;

public class CustomBlockAPI {


  @Inject
  private CustomBlockRegistry blockRegister;

  @Inject
  private CustomBlockManager customBlockManager;

  @Inject
  private AssetLinker assetLinker;

  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> customBlock,
      @NonNull String[] textures, @NonNull Class<? extends CraftoryRenderer> renderer) {
    CraftoryBlockKey blockKey = blockRegister.registerCustomBlockClass(plugin, customBlock, textures[0]);
    assetLinker.registerBlockAssets(blockKey, renderer, textures);
  }

  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> customBlock,
      @NonNull String[] textures) {
    registerCustomBlock(plugin, customBlock, textures, DefaultRenderer.class);
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull Class<? extends BaseCustomBlock> customBlockClazz,
      @NonNull CraftoryDirection craftoryDirection) {
    Optional<CraftoryBlockKey> key = blockRegister.getBlockKey(customBlockClazz);
    if (key.isEmpty()) {
      Log.warn("Tried to place a custom block that doesn't exist or isn't registered");
      return Optional.empty();
    }
    return customBlockManager.placeCustomBlock(key.get(), location, craftoryDirection);
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull Class<? extends BaseCustomBlock> customBlockClazz) {
    return placeCustomBlock(location, customBlockClazz, CraftoryDirection.NORTH);
  }

  public void registerDataKey(@NonNull CraftoryDataKey craftoryDataKey) {
    blockRegister.registerDataKey(craftoryDataKey.toString(), craftoryDataKey);
  }

}
