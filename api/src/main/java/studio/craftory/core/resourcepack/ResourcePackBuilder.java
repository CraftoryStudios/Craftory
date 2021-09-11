package studio.craftory.core.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.CraftoryAddon;
import studio.craftory.core.utils.Constants.ResourcePack;
import studio.craftory.core.utils.FileUtils;
import studio.craftory.core.utils.Log;

@UtilityClass
public class ResourcePackBuilder {

  private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public static void run() {
    File tempDirectory = new File(ResourcePack.TEMP_PATH);
    tempDirectory.mkdirs();

    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      if (CraftoryAddon.class.isAssignableFrom(plugin.getClass())) {

        File assetDirectory = new File(ResourcePack.ASSETS_PATH, plugin.getName());
        assetDirectory.mkdirs();
        File zipFile = new File(tempDirectory, plugin.getName() + ".zip");

        try {
          if (!zipFile.createNewFile()) {
            Log.debug("Couldn't create resource pack zip file, as already existed");
          }
        } catch (IOException e) {
          Log.error(e.toString());
        }

        FileUtils.downloadResource(((CraftoryAddon) plugin).getAddonResources(), zipFile);
        FileUtils.unZip(zipFile, assetDirectory);
        FileUtils.copyResources(assetDirectory.getAbsolutePath(), new File(ResourcePack.RESOURCE_PACK_PATH).getAbsolutePath(),
            (source, dest) -> mergeResources(source, dest));

        FileUtils.recursiveDirectoryDelete(ResourcePack.ASSETS_PATH);
      }
    }
    FileUtils.recursiveDirectoryDelete(ResourcePack.TEMP_PATH);
  }

  private static void mergeResources(File source, File dest) {
    Optional<String> fileExtensionOptional = getExtension(dest.getName());
    if (!fileExtensionOptional.isPresent()) {
      Log.warn("Unknown file in resource pack", dest.getName());
      return;
    }

    switch (fileExtensionOptional.get()) {
      case "png":
      case "ogg":
      case "mcmeta":
        Log.warn("Can't merge file " + dest.getName());
        break;
      case "json":
        mergeJSON(source, dest);
        break;
      default:
        break;
    }
  }

  private static void mergeJSON(File source, File dest) {
    try (FileWriter fw = new FileWriter(dest)){
      JsonObject sourceNode = new JsonParser().parse(new FileReader(source)).getAsJsonObject();
      JsonObject destNode = new JsonParser().parse(new FileReader(dest)).getAsJsonObject();
      gson.toJson( merge(destNode, sourceNode), fw);
    } catch (IOException e) {
      Log.error(e.toString());
    }
  }

  private static JsonObject merge(JsonObject mainNode, JsonObject updateNode) {
    for (Entry<String, JsonElement> element :  updateNode.entrySet()) {
      JsonObject jsonNode = (JsonObject) mainNode.get(element.getKey());
      // if field exists and is an embedded object
      if (jsonNode != null && jsonNode.isJsonObject()) {
        merge(jsonNode, (JsonObject) updateNode.get(element.getKey()));
      }
      else {
        if (mainNode.isJsonObject()) {
          // Overwrite field
          JsonObject value = (JsonObject) updateNode.get(element.getKey());
          mainNode.add(element.getKey(), value);
        }
      }
    }

    return mainNode;
  }

  private static Optional<String> getExtension(String filename) {
    return Optional.ofNullable(filename)
                   .filter(f -> f.contains("."))
                   .map(f -> f.substring(filename.lastIndexOf(".") + 1));
  }
}
