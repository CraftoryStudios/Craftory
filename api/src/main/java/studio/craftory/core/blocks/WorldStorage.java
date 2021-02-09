package studio.craftory.core.blocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
  private ObjectMapper mapper;

  @Getter
  private World world;
  private ObjectNode rootNode;
  private File file;

  public WorldStorage(@NonNull final World world, CustomBlockRegister blockRegister) {
    this.world = world;
    this.mapper = Craftory.getInstance().getMapper();
    this.blockRegister = blockRegister;
    file = new File(world.getWorldFolder(), "craftory");

    if (!file.exists()) {
      file.mkdirs();
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    file = new File(file, "blocks.json");

    try {
      rootNode = (ObjectNode) mapper.readTree(file);
    } catch (IOException e) {
      //Doesn't exist so create
      rootNode = mapper.createObjectNode();
    }
  }

  public void save() throws IOException {
    mapper.writeValue(file, rootNode);
  }

  @Synchronized
  public void writeChunk(@NonNull final Chunk chunk, @NonNull final Collection<BaseCustomBlock> customBlocks) {
    ObjectNode chunkData;
    String chunkKey = chunk.getX() + ";" + chunk.getZ();

    if (rootNode.with("chunks").has(chunkKey)) {
      chunkData = (ObjectNode) rootNode.with("chunks").get(chunkKey);
    } else {
      chunkData = mapper.createObjectNode();
      rootNode.with("chunks").set(chunkKey, chunkData);
    }

    for (BaseCustomBlock customBlock : customBlocks) {
      writeCustomBlock(chunkData, customBlock);
    }
  }

  @Synchronized
  private void writeCustomBlock(@NonNull final ObjectNode chunkRoot, @NonNull final BaseCustomBlock customBlock) {
    ObjectNode objectRoot = mapper.createObjectNode();

    //Save block type
    Optional<CustomBlockKey> key = blockRegister.getKey(customBlock);
    if (!key.isPresent())  {
      Log.warn("Error saving block");
      throw new IllegalStateException("Custom Block can't be saved as it isn't registered");
    }
    objectRoot.put("type", key.get().toString());

    //Save block direction
    objectRoot.put("direction", customBlock.getFacingDirection().label);

    //Save Complex Block Persistent Data
    if (ComplexCustomBlock.class.isAssignableFrom(customBlock.getClass())) {
      ObjectNode persistentData = mapper.createObjectNode();
      for (Map.Entry<CraftoryDataKey, Object> entry : ((ComplexCustomBlock) customBlock).getPersistentData().getData().entrySet()) {
        persistentData.set(entry.getKey().toString(), mapper.valueToTree(entry.getValue()));
      }
      objectRoot.set("persistentData",persistentData);
    }

    Location location = customBlock.getLocation().getLocation().get();
    chunkRoot.set(((int)location.getX()) + ";" + ((int)location.getY()) + ";" + ((int)location.getZ()), objectRoot);
  }

  @Synchronized
  public void removeCustomBlock(@NonNull final BaseCustomBlock customBlock) {
    SafeBlockLocation location = customBlock.getLocation();

    ObjectNode chunkData = (ObjectNode) rootNode.with("chunks").get(location.getChunkX() + ";" + location.getChunkZ());
    if (chunkData == null) return;
    if (chunkData.has(location.getX()+";"+ location.getY()+";"+ location.getZ())) {
      chunkData.remove(location.getX()+";"+ location.getY()+";"+ location.getZ());
    }
    if (chunkData.isEmpty()) {
      removeChunk(location.getChunk().get());
    }
  }

  @Synchronized
  public void removeChunk(@NonNull final Chunk chunk) {
    if (rootNode.with("chunks").has(chunk.getX() + ";" + chunk.getZ())) {
      rootNode.with("chunks").remove(chunk.getX() + ";" + chunk.getZ()) ;
    }
  }

  public Optional<Set<BaseCustomBlock>> getSavedBlocksInChunk(@NonNull final Chunk chunk) {
    ObjectNode chunkData = (ObjectNode) rootNode.with("chunks").get(chunk.getX() + ";" + chunk.getZ());
    if (chunkData == null || chunkData.isEmpty()) return Optional.empty();

    Set<BaseCustomBlock> customBlocks = new HashSet<>();
    Iterator<Map.Entry<String, JsonNode>> fields = chunkData.fields();
    Map.Entry<String, JsonNode> field;
    while (fields.hasNext()) {
      field = fields.next();
      //Get Block Values
      CustomBlockKey customBlockKey = new CustomBlockKey(field.getValue().get("type").asText());
      String[] splitKey = field.getKey().split(":");
      Location location = new Location(chunk.getWorld(), Double.parseDouble(splitKey[0]), Double.parseDouble(splitKey[1]),
          Double.parseDouble(splitKey[2]));
      CraftoryDirection direction = CraftoryDirection.valueOfLabel((byte) field.getValue().get("direction").asInt());

      Optional<? extends BaseCustomBlock> customBlock = blockRegister.getNewCustomBlockInstance(customBlockKey, location, direction);
      if (!customBlock.isPresent()) return Optional.empty();

      //Inject Persistent Data
      if (ComplexCustomBlock.class.isAssignableFrom(customBlock.getClass())) {
        JsonNode persistentData = field.getValue().get("persistentData");
        if (persistentData != null) {
          Iterator<Map.Entry<String, JsonNode>> iterator = chunkData.fields();
          Map.Entry<String, JsonNode> data;
          while (iterator.hasNext()) {
            data = iterator.next();
            Optional<CraftoryDataKey> datatype = blockRegister.getDataType(data.getKey());
            Map.Entry<String, JsonNode> finalData = data;
            datatype.ifPresent(craftoryDataKey -> {
              try {
                ((ComplexCustomBlock) customBlock.get()).getPersistentData().set(craftoryDataKey,
                    mapper.treeToValue(finalData.getValue(), craftoryDataKey.getDataClass()));
              } catch (JsonProcessingException e) {
                e.printStackTrace();
              }
            });

          }
        }

      }

      customBlocks.add(customBlock.get());
    }

    if (customBlocks.isEmpty()) return Optional.empty();

    return Optional.of(customBlocks);
  }
}
