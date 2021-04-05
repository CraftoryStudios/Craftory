package studio.craftory.core.blocks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.block.Block;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.blocks.rendering.renderers.DefaultRenderer;
import studio.craftory.core.blocks.rendering.renderers.DefaultRotationalRenderer;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.RenderData;
import studio.craftory.core.data.events.ResourcePackBuilt;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Log;

public class BlockRenderManager {
  private final ObjectMapper mapper;

  @Getter
  private Map<String, CraftoryRenderer> renderers = new HashMap<>();
  private Map<String, RenderData> blockToRenderDataMap = new HashMap<>();

  public BlockRenderManager() {
    mapper = new ObjectMapper();
    registerDefaultRenders();
  }

  public void onResourcePackBuilt(ResourcePackBuilt event) {
    loadRenderData();
  }

  private void registerDefaultRenders() {
    registerRenderer(DefaultRenderer.class);
    registerRenderer(DefaultRotationalRenderer.class);
  }

  public void registerRenderer(@NonNull Class<? extends CraftoryRenderer> renderer) {
    try {
      renderers.putIfAbsent(renderer.getSimpleName(), renderer.getDeclaredConstructor().newInstance());
    } catch (Exception e ) {
      Log.error("Couldn't register renderer ", renderer.getName());
    }
  }

  public void renderCustomBlock(CraftoryBlockKey blockKey, Block block, CraftoryDirection direction) {
    RenderData data = blockToRenderDataMap.get(blockKey.toString());
    data.getRenderer().render(block, direction, data);
  }

  public void loadRenderData() {
    File renderDataFile = new File(Craftory.getInstance().getDataFolder(), "renderData.json");
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

  private void parseRenderData(@NonNull JsonNode node) {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;

      Iterator<Entry<String, JsonNode>> fields = objectNode.fields();
      Entry<String, JsonNode> field;
      while (fields.hasNext()) {
        field = fields.next();

        if (field.getValue().isArray() && field.getValue().size() > 1) {
          blockToRenderDataMap.put(field.getKey(), extractRenderData(field.getValue()));
        } else {
          Log.warn("Block type "+ field.getKey() + " doesn't have correct render data");
        }
      }
    } else {
      Log.warn("Error in render data structure: is not type object");
    }
  }

  private RenderData extractRenderData(@NonNull JsonNode node) {
    ArrayList<String> renderDetails = mapper.convertValue(node, new TypeReference<ArrayList<String>>() {});
    CraftoryRenderer renderer = renderers.get(renderDetails.get(0));
    renderDetails.remove(0);
    return new RenderData(renderer, renderDetails);
  }
}
