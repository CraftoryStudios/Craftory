package studio.craftory.core.blocks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.data.keys.CustomBlockKey;
import studio.craftory.core.data.safecontainers.SafeBlockLocation;
import studio.craftory.core.utils.Log;

public class WorldStorage {

  private final CustomBlockRegister blockRegister;
  private Gson gson;

  @Getter
  private World world;
  private JsonObject rootNode;

  public WorldStorage(@NonNull final World world, CustomBlockRegister blockRegister) {
    this.world = world;
    this.gson = Craftory.getInstance().getGson();
    this.blockRegister = blockRegister;

    try {
      Reader reader = new FileReader(new File(world.getWorldFolder(), "craftory/blocks.json"));
      rootNode = JsonParser.parseReader(reader).getAsJsonObject();
    } catch (FileNotFoundException | IllegalStateException e) {
      rootNode = new JsonObject();
      rootNode.add("chunks", new JsonObject());
    }
  }

  public void save() throws IOException {
    Writer writer = new FileWriter(new File(world.getWorldFolder(), "craftory/blocks.json"));
    gson.toJson(rootNode, writer);
  }

  @Synchronized
  public void writeChunk(@NonNull final Chunk chunk, @NonNull final Collection<BaseCustomBlock> customBlocks) {
    JsonObject chunks = rootNode.getAsJsonObject("chunks");
    JsonObject chunkData;
    String chunkKey = chunk.getX() + ";" + chunk.getZ();

    if (chunks.has(chunkKey)) {
      chunkData = chunks.getAsJsonObject(chunkKey);
    } else {
      chunkData = new JsonObject();
      chunks.add(chunkKey, chunkData);
    }

    for (BaseCustomBlock customBlock : customBlocks) {
      writeCustomBlock(chunkData, customBlock);
    }
  }

  @Synchronized
  private void writeCustomBlock(@NonNull final JsonObject chunkRoot, @NonNull final BaseCustomBlock customBlock) {
    JsonObject objectRoot = new JsonObject();

    //Save block type
    Optional<CustomBlockKey> key = blockRegister.getKey(customBlock);
    if (!key.isPresent())  {
      Log.warn("Error saving block");
      throw new IllegalStateException("Custom Block can't be saved as it isn't registered");
    }
    objectRoot.addProperty("type", key.get().toString());

    //Save block direction
    objectRoot.addProperty("direction", customBlock.getFacingDirection().label);

    //Save Complex Block Persistent Data
    if (ComplexCustomBlock.class.isAssignableFrom(customBlock.getClass())) {
      JsonObject persistentData = new JsonObject();
      for (Map.Entry<CraftoryDataKey, Object> entry : ((ComplexCustomBlock) customBlock).getPersistentData().getData().entrySet()) {
        persistentData.add(entry.getKey().toString(), gson.toJsonTree(entry.getValue()));
      }
      objectRoot.add("persistentData",persistentData);
    }

    Location location = customBlock.getLocation().getLocation().get();
    chunkRoot.add(((int)location.getX()) + ";" + ((int)location.getY()) + ";" + ((int)location.getZ()), objectRoot);
  }

  @Synchronized
  public void removeCustomBlock(@NonNull final BaseCustomBlock customBlock) {
    JsonObject chunks = rootNode.getAsJsonObject("chunks");
    if (chunks == null) return;
    SafeBlockLocation location = customBlock.getLocation();

    JsonObject chunkData = chunks.getAsJsonObject(location.getChunkX() + ";" + location.getChunkZ());
    if (chunkData == null) return;
    if (chunkData.has(location.getX()+";"+ location.getY()+";"+ location.getZ())) {
      chunkData.remove(location.getX()+";"+ location.getY()+";"+ location.getZ());
    }
    if (chunkData.keySet().isEmpty()) {
      chunks.remove(location.getChunkX() + ";" + location.getChunkZ());
    }
  }

  @Synchronized
  public void removeChunk(@NonNull final Chunk chunk) {
    JsonObject chunks = rootNode.getAsJsonObject("chunks");
    if (chunks == null) return;

    if (chunks.has(chunk.getX() + ";" + chunk.getZ())) {
     chunks.remove(chunk.getX() + ";" + chunk.getZ()) ;
    }
  }

  public Optional<Set<BaseCustomBlock>> getSavedBlocksInChunk(@NonNull final Chunk chunk) {
    JsonObject chunks = rootNode.getAsJsonObject("chunks");
    if (chunks == null) return Optional.empty();
    JsonObject chunkData = chunks.getAsJsonObject(chunk.getX() + ";" + chunk.getZ());
    if (chunkData == null || chunkData.keySet().isEmpty()) return Optional.empty();

    Set<BaseCustomBlock> customBlocks = new HashSet<>();
    JsonObject blockData;
    for (String key : chunkData.keySet()) {
      blockData = chunkData.getAsJsonObject(key);

      //Get Block Values
      CustomBlockKey customBlockKey = new CustomBlockKey(blockData.get("type").getAsString());
      String[] splitKey = key.split(":");
      Location location = new Location(chunk.getWorld(), Double.parseDouble(splitKey[0]), Double.parseDouble(splitKey[1]),
          Double.parseDouble(splitKey[2]));
      CraftoryDirection direction = CraftoryDirection.valueOfLabel(blockData.get("direction").getAsByte());

      Optional<? extends BaseCustomBlock> customBlock = blockRegister.getNewCustomBlockInstance(customBlockKey, location, direction);
      if (!customBlock.isPresent()) return Optional.empty();

      //Inject Persistent Data
      if (ComplexCustomBlock.class.isAssignableFrom(customBlock.getClass())) {
        JsonObject persistentData = blockData.getAsJsonObject("persistentData");
        if (persistentData != null) {
          for (String dataKey : persistentData.keySet()) {
            Optional<CraftoryDataKey> datatype = blockRegister.getDataType(dataKey);
            datatype.ifPresent(craftoryDataKey -> ((ComplexCustomBlock) customBlock.get()).getPersistentData().set(craftoryDataKey,
                gson.fromJson(persistentData.get(dataKey),
                    craftoryDataKey.getDataClass())));

          }
        }

      }

      customBlocks.add(customBlock.get());
    }

    if (customBlocks.isEmpty()) return Optional.empty();

    return Optional.of(customBlocks);
  }
}
