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
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.resourcepack.AssetLinker;
import studio.craftory.core.terrian.retro.RetroGeneration;
import studio.craftory.core.utils.Log;

public class CustomBlockAPI {

  @Inject
  private CustomBlockRegistry blockRegister;
  @Inject
  private CustomBlockManager customBlockManager;
  @Inject
  private AssetLinker assetLinker;
  @Inject
  private RetroGeneration retroGeneration;

  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> customBlock,
      @NonNull String[] textures, @NonNull Class<? extends CraftoryRenderer> renderer) {
    CraftoryBlockKey blockKey = blockRegister.registerCustomBlockClass(plugin, customBlock);
    assetLinker.registerBlockAssets(blockKey, renderer, textures);
  }

  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> customBlock,
      @NonNull String[] textures) {
    registerCustomBlock(plugin, customBlock, textures, DefaultRenderer.class);
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull Class<? extends BaseCustomBlock> customBlockClazz,
  @NonNull CraftoryDirection craftoryDirection) {
    Optional<CraftoryBlockKey> key = blockRegister.getBlockKey(customBlockClazz);
    if (!key.isPresent()) {
      Log.warn("Tried to place a custom block that doesn't exist or isn't registered");
      return Optional.empty();
    }
    return customBlockManager.placeCustomBlock(key.get(),location,craftoryDirection);
  }

  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull Location location, @NonNull Class<? extends BaseCustomBlock> customBlockClazz) {
    return placeCustomBlock(location, customBlockClazz, CraftoryDirection.NORTH);
  }

  public void registerDataKey(@NonNull CraftoryDataKey craftoryDataKey) {
    blockRegister.registerDataKey(craftoryDataKey.toString(), craftoryDataKey);
  }

  public RetroGeneration getRetroGeneration() {
    return retroGeneration;
  }

}
