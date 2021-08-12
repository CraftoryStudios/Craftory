package studio.craftory.core.blocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryDataKey;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Log;

public class WorldDataStorage {

  private static final String CHUNKS_KEY = "chunks";
  private final CustomBlockRegistry blockRegister;
  private final ObjectMapper mapper;

  @Getter
  private World world;
  private ObjectNode rootNode;
  private File file;

  public WorldDataStorage(@NonNull final World world, CustomBlockRegistry blockRegister, ObjectMapper mapper) {
    this.world = world;
    this.blockRegister = blockRegister;
    this.mapper = mapper;
    file = new File(world.getWorldFolder(), "craftory/blocks.json");
    setupSaveFile();
  }

  private void setupSaveFile() {
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      try {
        if(!file.createNewFile()) {
          Log.error("Couldn't create save file");
        }
      } catch (IOException e) {
        Log.error("Couldn't create save file");
        Log.error(e.toString());
      }
      rootNode = mapper.createObjectNode();
    } else {
      try {
        rootNode = (ObjectNode) mapper.readTree(file);
      } catch (IOException e) {
        //Doesn't exist so create
        rootNode = mapper.createObjectNode();
      }
    }
  }

  public void save() {
    try {
      mapper.writeValue(file, rootNode);
    } catch (IOException e) {
      Log.error("Failed to save world: "+ world.getName());
      Log.debug(e.toString());
    }
  }

  @Synchronized
  public void writeChunk(@NonNull final Chunk chunk, @NonNull final Collection<BaseCustomBlock> customBlocks) {
    ObjectNode chunkData = getChunkNode(chunk);

    for (BaseCustomBlock customBlock : customBlocks) {
      writeCustomBlock(chunkData, customBlock);
    }
  }

  @Synchronized
  public void writeCustomBlock(@NonNull final BaseCustomBlock customBlock) {
    ObjectNode chunkData = getChunkNode(customBlock.getLocation().getChunk());
    writeCustomBlock(chunkData, customBlock);
  }

  @Synchronized
  private void writeCustomBlock(@NonNull final ObjectNode chunkRoot, @NonNull final BaseCustomBlock customBlock) {
    //Get block type key
    Optional<CraftoryBlockKey> key = blockRegister.getBlockKey(customBlock);
    if (!key.isPresent())  {
      Log.warn("Error saving block");
      throw new IllegalStateException("Custom Block can't be saved as it isn't registered");
    }

    //Write Custom Block data to JSON Node
    StringJoiner blockData = new StringJoiner(";");
    blockData.add(key.get().toString());
    blockData.add(String.valueOf(customBlock.getFacingDirection().label));

    if (ComplexCustomBlock.class.isAssignableFrom(customBlock.getClass())) {
      //TODO ensure persistent data doesn't contain ; symbol
      String temp = null;
      try {
        temp = mapper.writeValueAsString(persistentDataToNode((ComplexCustomBlock) customBlock));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      Log.error(temp);
      blockData.add(temp);
    } else {
      blockData.add("{}");
    }

    chunkRoot.put(getLocationKey(customBlock.getLocation()), blockData.toString());
  }

  @Synchronized
  public void removeCustomBlock(@NonNull final BaseCustomBlock customBlock) {
    Location location = customBlock.getLocation();

    ObjectNode chunkData = (ObjectNode) rootNode.with(CHUNKS_KEY).get(getChunkKey(location.getChunk()));
    if (chunkData == null) return;
    chunkData.remove(getLocationKey(location));

    if (chunkData.isEmpty()) {
      removeChunk(location.getChunk());
    }
  }

  @Synchronized
  public void removeChunk(@NonNull final Chunk chunk) {
    rootNode.with(CHUNKS_KEY).remove(getChunkKey(chunk)) ;
  }

  public Optional<Set<BaseCustomBlock>> getSavedBlocksInChunk(@NonNull final Chunk chunk) {
    ObjectNode chunkData = (ObjectNode) rootNode.with(CHUNKS_KEY).get(getChunkKey(chunk));
    if (chunkData == null || chunkData.isEmpty()) return Optional.empty();

    Set<BaseCustomBlock> customBlocks = new HashSet<>();
    Iterator<Map.Entry<String, JsonNode>> fields = chunkData.fields();
    Entry<String, JsonNode> field;
    while (fields.hasNext()) {
      field = fields.next();
      String[] blockData = field.getValue().textValue().split(";");

      //TODO recover corrupted data
      if (blockData.length < 3) {
        Log.warn("Block data corrupted for location " + field.getKey());
        continue;
      }

      //Get Block Values
      CraftoryBlockKey craftoryBlockKey = new CraftoryBlockKey(blockData[0]);
      String[] splitKey = field.getKey().split(";");
      Location location = new Location(chunk.getWorld(), Integer.parseInt(splitKey[0]), Integer.parseInt(splitKey[1]),
          Integer.parseInt(splitKey[2]));
      CraftoryDirection direction = CraftoryDirection.valueOfLabel((byte) Integer.parseInt(blockData[1]));

      Optional<? extends BaseCustomBlock> customBlock = blockRegister.getNewCustomBlockInstance(craftoryBlockKey, location, direction);
      if (!customBlock.isPresent()) return Optional.empty();

      //Inject Persistent Data
      if (ComplexCustomBlock.class.isAssignableFrom(customBlock.get().getClass())) {

        JsonNode persistentData = null;
        try {
          persistentData = mapper.readTree(blockData[2]);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }

        if (persistentData != null) {
          Iterator<Map.Entry<String, JsonNode>> iterator = persistentData.fields();
          Map.Entry<String, JsonNode> data;
          while (iterator.hasNext()) {
            data = iterator.next();
            Optional<CraftoryDataKey> datatype = blockRegister.getDataKey(data.getKey());
            Map.Entry<String, JsonNode> finalData = data;
            datatype.ifPresent(craftoryDataKey -> {
              try {
                ((ComplexCustomBlock) customBlock.get()).getPersistentData().set(craftoryDataKey,
                    mapper.treeToValue(finalData.getValue(), craftoryDataKey.getDataClass()));
              } catch (JsonProcessingException e) {
                Log.error("Couldn't process custom block data");
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

  private ObjectNode getChunkNode(Chunk chunk) {
    ObjectNode chunkData;
    String chunkKey = getChunkKey(chunk);

    if (rootNode.with(CHUNKS_KEY).has(chunkKey)) {
      chunkData = (ObjectNode) rootNode.with(CHUNKS_KEY).get(chunkKey);
    } else {
      chunkData = mapper.createObjectNode();
      rootNode.with(CHUNKS_KEY).set(chunkKey, chunkData);
    }
    return chunkData;
  }

  private String getLocationKey(@NonNull Location location) {
    return ((int) location.getX()) + ";" + ((int) location.getY()) + ";" + ((int) location.getZ());
  }

  private ObjectNode persistentDataToNode(ComplexCustomBlock customBlock) {
    ObjectNode persistentData = mapper.createObjectNode();
    for (Map.Entry<CraftoryDataKey, Object> entry : customBlock.getPersistentData().getData().entrySet()) {
      persistentData.set(entry.getKey().toString(), mapper.valueToTree(entry.getValue()));
    }
    return persistentData;
  }

  private String getChunkKey(Chunk chunk) {
    return chunk.getX() + ";" + chunk.getZ();
  }
}
