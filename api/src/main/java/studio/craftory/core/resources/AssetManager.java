package studio.craftory.core.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.blocks.renders.Renderers;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Log;

public class AssetManager extends BukkitRunnable {

  private Map<Character, Map<String, String[]>> preRenderData = new HashMap<>();
  private Map<String, String> usedBlockData = new HashMap<>();
  private Map<String, Integer> blocksToUse = new HashMap<>();
  private ObjectMapper objectMapper = new ObjectMapper();
  private String currentMethod;
  private ObjectNode blockstate;
  private int count = 0;

  public AssetManager() {
    blocksToUse.put("mushroomStem", 64);
    blocksToUse.put("noteBlock", 750);
  }

  public void registerCustomBlock(@NonNull CraftoryBlockKey blockKey, char renderer, @NonNull String[] textures) {
    preRenderData.computeIfAbsent(renderer, k -> new HashMap<>()).put(blockKey.toString(), textures);
  }

  @Override
  public void run() {
    ObjectNode node = objectMapper.createObjectNode();
    pickMethod();

    Map<String, String[]> blocks = preRenderData.get(Renderers.BLOCK_STATE_RENDER.value.charAt(0));
    for (Entry<String, String[]> block : blocks.entrySet()) {
      node.set(block.getKey(), generateRenderData(block.getValue()));
    }
  }

  private ArrayNode generateRenderData(String[] value) {
    ArrayNode arrayNode = objectMapper.createArrayNode();
    arrayNode.add(Renderers.BLOCK_STATE_RENDER.value);
    if (value.length == 1) {
      getAllBlockState(arrayNode, value);
    } else if (value.length == 2) {
      getAllBlockState(arrayNode, value);
      arrayNode.add(value[1]);
    } else if (value.length == 5) {
      getBlockStates(arrayNode, value);
      arrayNode.add(value[1]);
    } else {
      Log.warn("Error");
    }
    return arrayNode;
  }

  private void getBlockStates(ArrayNode arrayNode, String[] value) {
  }

  private void getAllBlockState(ArrayNode arrayNode, String[] value) {

  }

  private void saveCurrentMethod() {
    if (currentMethod.isEmpty()) return;

    //save blockstate

    blocksToUse.replace(currentMethod, count);
  }

  private void pickMethod() {
    saveCurrentMethod();
    currentMethod = "";
    blockstate = objectMapper.createObjectNode();
    for (Entry<String, Integer> method : blocksToUse.entrySet()) {
      if (method.getValue() > 0) {
        currentMethod = method.getKey();
        count = method.getValue();
        return;
      }
    }
    if (currentMethod.isEmpty()) {
      Log.warn("No method found");
    }
  }

}
