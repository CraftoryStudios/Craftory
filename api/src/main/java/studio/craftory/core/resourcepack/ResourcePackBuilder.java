package studio.craftory.core.resourcepack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.CraftoryAddon;
import studio.craftory.core.utils.Constants.ResourcePack;
import studio.craftory.core.utils.FileUtils;
import studio.craftory.core.utils.Log;

@UtilityClass
public class ResourcePackBuilder {

  private static final ObjectMapper objectMapper = new ObjectMapper();

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
    try {
      JsonNode sourceNode = objectMapper.readTree(source);
      JsonNode destNode = objectMapper.readTree(dest);
      objectMapper.writeValue(dest, merge(destNode, sourceNode));
    } catch (IOException e) {
      Log.error(e.toString());
    }
  }

  private static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
    Iterator<String> fieldNames = updateNode.fieldNames();
    while (fieldNames.hasNext()) {

      String fieldName = fieldNames.next();
      JsonNode jsonNode = mainNode.get(fieldName);
      // if field exists and is an embedded object
      if (jsonNode != null && jsonNode.isObject()) {
        merge(jsonNode, updateNode.get(fieldName));
      }
      else {
        if (mainNode instanceof ObjectNode) {
          // Overwrite field
          JsonNode value = updateNode.get(fieldName);
          ((ObjectNode) mainNode).set(fieldName, value);
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