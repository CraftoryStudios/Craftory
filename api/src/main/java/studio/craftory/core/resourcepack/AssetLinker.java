package studio.craftory.core.resourcepack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.blocks.BlockRenderManager;
import studio.craftory.core.blocks.CustomBlockRegistry;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.blocks.rendering.DefaultRenderers;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Log;

public class AssetLinker extends BukkitRunnable {

  private Map<String, Map<String, String[]>> assetsToGenerate = new HashMap<>();
  private Map<String, String> usedBlockData = new HashMap<>();
  private Map<String, Integer> blocksToUse = new HashMap<>();
  private ObjectMapper objectMapper = new ObjectMapper();
  private String currentMethod;
  private ObjectNode blockstate;
  private int count = 0;

  @Inject
  private BlockRenderManager blockRenderManager;

  @Override
  public void run() {
    linkCustomBlockAssets();
  }

  public void registerBlockAssets(@NonNull CraftoryBlockKey blockKey, String renderer, @NonNull String[] textures) {
    assetsToGenerate.computeIfAbsent(renderer, k -> new HashMap<>()).put(blockKey.toString(), textures);
  }



  private void linkCustomBlockAssets() {
    for (Entry<String, Map<String, String[]>> rendererAssets : assetsToGenerate.entrySet()) {
      CraftoryRenderer renderer = blockRenderManager.getRenderers().get(rendererAssets.getKey());

      for (Entry<String, String[]> blockAssets : rendererAssets.getValue().entrySet()) {
        renderer.generateAssets(blockAssets.getKey(), blockAssets.getValue());
      }
    }
  }

  private void generate() {
    ObjectNode node = objectMapper.createObjectNode();
    pickMethod();

    Map<String, String[]> blocks = preRenderData.get(DefaultRenderers.BLOCK_STATE_RENDER.value.charAt(0));
    for (Entry<String, String[]> block : blocks.entrySet()) {
      node.set(block.getKey(), generateRenderData(block.getValue()));
    }
  }

  private ArrayNode generateRenderData(String[] value) {
    ArrayNode arrayNode = objectMapper.createArrayNode();
    arrayNode.add(DefaultRenderers.BLOCK_STATE_RENDER.value);
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
