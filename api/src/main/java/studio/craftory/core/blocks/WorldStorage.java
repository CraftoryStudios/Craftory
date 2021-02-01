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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.World;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.data.keys.CustomBlockKey;
import studio.craftory.core.persistence.PersistenceManager;
import studio.craftory.core.utils.Log;

public class WorldStorage {

  @Inject
  private PersistenceManager persistenceManager;
  @Inject
  private CustomBlockRegister blockRegister;
  private Gson gson;

  @Getter
  private World world;
  private JsonObject rootNode;

  public WorldStorage(@NonNull final World world) {
    this.world = world;
    this.gson = Craftory.getInstance().getGson();

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
  public void writeChunk(@NonNull final Chunk chunk, @NonNull final List<BaseCustomBlock> customBlocks) {
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
    objectRoot.addProperty("direction", customBlock.getFacingDirection().toString());

    //Save Complex Block Persistent Data
    if (ComplexCustomBlock.class.isAssignableFrom(customBlock.getClass())) {
      JsonObject persistentData = new JsonObject();
      for (Map.Entry<CraftoryDataKey, Object> entry : ((ComplexCustomBlock) customBlock).getPersistentData().getData().entrySet()) {
        persistentData.add(entry.getKey().toString(), gson.toJsonTree(entry.getValue()));
      }
      objectRoot.add("persistentData",persistentData);
    }

    chunkRoot.add(customBlock.getSafeBlockLocation().getLocation().get().toString(), objectRoot);
  }

  @Synchronized
  public void removeCustomBlock(@NonNull final BaseCustomBlock customBlock) {
    JsonObject chunks = rootNode.getAsJsonObject("chunks");
    
  }

  public Optional<Set<? extends BaseCustomBlock>> getSavedBlocksInChunk(@NonNull final Chunk chunk) {
    return Optional.empty();
  }
}
