package studio.craftory.core.blocks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.ReferenceType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import org.bukkit.block.Block;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.renders.BlockStateRenderer;
import studio.craftory.core.blocks.renders.CraftoryRenderer;
import studio.craftory.core.blocks.renders.RenderData;
import studio.craftory.core.blocks.renders.Renderers;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Log;

public class BlockRenderManager {
  @Inject
  private CustomBlockRegistry customBlockRegistry;
  private final ObjectMapper mapper;

  private Map<String, CraftoryRenderer> renderers = new HashMap<>();
  private Map<CraftoryBlockKey, RenderData> renderData = new HashMap<>();


  public BlockRenderManager() {
    registerRenderer(Renderers.BLOCK_STATE_RENDER, new BlockStateRenderer());
    registerRenderer(Renderers.TRANSPARENT_BLOCK_STATE_RENDER, new BlockStateRenderer());
    mapper = new ObjectMapper();
    loadRenderData();
  }

  public void registerRenderer(String key, CraftoryRenderer renderer) {
    renderers.putIfAbsent(key, renderer);
  }
  public void registerRenderer(Renderers key, CraftoryRenderer renderer) {
    registerRenderer(key.value, renderer);
  }

  public void renderCustomBlock(CraftoryBlockKey blockKey, Block block, CraftoryDirection direction) {
    RenderData data = renderData.get(blockKey);
    data.getRenderer().render(block, direction, data);
  }

  private void loadRenderData() {
    File renderDataFile = new File(Craftory.getInstance().getDataFolder(), Craftory.getInstance().getName() + "renderData.json");
    if (renderDataFile.exists()) {
      try {
        JsonNode node = mapper.readTree(renderDataFile);
        parseRenderData(node);
      } catch (IOException e) {
        Log.error("Couldn't read render data");
      }
    } else {
      Log.warn("No render data found");
    }
  }

  private void parseRenderData(JsonNode node) {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;

      Iterator<Entry<String, JsonNode>> fields = objectNode.fields();
      Entry<String, JsonNode> field;
      while (fields.hasNext()) {
        field = fields.next();

        Optional<CraftoryBlockKey> blockKeyOptional = customBlockRegistry.getBlockKey(field.getKey());
        if (blockKeyOptional.isPresent()) {
          if (field.getValue().isArray() && field.getValue().size() > 1) {
            ArrayList<String> renderDetails = mapper.convertValue(field.getValue(), new TypeReference<ArrayList<String>>() {});
            CraftoryRenderer renderer = renderers.get(renderDetails.get(0));
            renderDetails.remove(0);
            RenderData data = new RenderData(renderer, renderDetails);


            renderData.put(blockKeyOptional.get(), data);
          }
        } else {
          Log.warn("Render data has values for non existing block type");
        }
      }
    } else {
      Log.warn("Error in render data structure: is not type object");
    }
  }
}
