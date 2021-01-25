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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.World;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.persistence.PersistenceManager;

public class WorldStorage {

  @Inject
  private PersistenceManager persistenceManager;
  private World world;
  private JsonObject rootNode;
  @Inject
  private Gson gson;

  public WorldStorage(@NonNull final World world) {
    this.world = world;

    try {
      Reader reader = new FileReader(new File(world.getWorldFolder(), "craftory/blocks.json"));
      rootNode = (JsonObject) JsonParser.parseReader(reader);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void save() throws IOException {
    Writer writer = new FileWriter(new File(world.getWorldFolder(), "craftory/blocks.json"));
    gson.toJson(rootNode, writer);
  }

  private void writeCustomBlock(JsonObject chunkRoot, ComplexCustomBlock customBlock) {
    JsonObject objectRoot;
    //Get JSON node for object or create new
    if (chunkRoot.has(customBlock.getSafeBlockLocation().toString())) {
      objectRoot = chunkRoot.getAsJsonObject(customBlock.getSafeBlockLocation().toString());
    } else {
      objectRoot = new JsonObject();
    }

    //Save each DataKey and Object to objectRoot
    for (Map.Entry<CraftoryDataKey, Object> entry : customBlock.getPersistentData().getData().entrySet()) {
      objectRoot.add(entry.getKey().toString(), gson.toJsonTree(entry.getValue()));
    }
  }

  public Optional<Set<? extends BaseCustomBlock>> getSavedBlocksInChunk(@NonNull final Chunk chunk) {
    return Optional.empty();
  }
}
