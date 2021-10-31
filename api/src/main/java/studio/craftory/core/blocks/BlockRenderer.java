package studio.craftory.core.blocks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.blocks.rendering.renderers.DefaultRenderer;
import studio.craftory.core.blocks.rendering.renderers.DefaultRotationalRenderer;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.RenderData;
import studio.craftory.core.containers.events.ResourcePackBuilt;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Log;

public class BlockRenderer implements Listener {
  private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  private final Map<String, CraftoryRenderer> renderers = new HashMap<>();
  private final Map<String, RenderData> blockToRenderDataMap = new HashMap<>();

  public BlockRenderer() {
    registerDefaultRenders();
  }

  @EventHandler
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
        JsonElement node = new JsonParser().parse(new FileReader(renderDataFile));
        if (node == null || !node.isJsonObject()) {
          node = new JsonObject();
        }
        parseRenderData(node.getAsJsonObject());
      } catch (IOException e) {
        Log.error("Couldn't read render data");
      }
    } else {
      Log.warn("No render data found");
    }
  }

  private void parseRenderData(@NonNull JsonObject node) {
    if (node.isJsonObject()) {

      for (Entry<String, JsonElement> field : node.entrySet()) {
        if (field.getValue().isJsonArray() && ((JsonArray)field.getValue()).size() > 1) {
          blockToRenderDataMap.put(field.getKey(), extractRenderData((JsonArray)field.getValue()));
        } else {
          Log.warn("Block type "+ field.getKey() + " doesn't have correct render data");
        }
      }
    } else {
      Log.warn("Error in render data structure: is not type object");
    }
  }

  private RenderData extractRenderData(@NonNull JsonArray node) {
    ArrayList<String> renderDetails = gson.fromJson(node, new TypeToken<ArrayList<String>>(){}.getType());
    CraftoryRenderer renderer = renderers.get(renderDetails.get(0));
    renderDetails.remove(0);
    return new RenderData(renderer, renderDetails);
  }

  public Map<String, CraftoryRenderer> getRenderers() {return this.renderers;}
}
