package studio.craftory.core.resourcepack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.FileUtil;
import studio.craftory.core.Craftory;
import studio.craftory.core.CraftoryAddon;
import studio.craftory.core.utils.Log;

public class CraftorySetup {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static void run() {
    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      if (CraftoryAddon.class.isAssignableFrom(plugin.getClass())) {

        File zipFile = new File(Craftory.getInstance().getDataFolder(), "tempassets");
        File destDir = new File(Craftory.getInstance().getDataFolder(), "assets"+ File.separator + plugin.getName());
        zipFile.mkdirs();
        destDir.mkdirs();

        zipFile = new File(zipFile, plugin.getName() + ".zip");

        try {
          zipFile.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
        downloadResource(((CraftoryAddon) plugin).getAddonResources(), zipFile);
        unZip(zipFile, destDir);

        File endDir = new File(Craftory.getInstance().getDataFolder(), "resourcepacks");
        copyResources(destDir.getAbsolutePath(), endDir.getAbsolutePath());

        try {
          Files.walk(Paths.get(Craftory.getInstance().getDataFolder() + "/tempassets"))
               .sorted(Comparator.reverseOrder())
               .map(Path::toFile)
               .forEach(File::delete);
          Files.walk(Paths.get(Craftory.getInstance().getDataFolder() + "/assets"))
               .sorted(Comparator.reverseOrder())
               .map(Path::toFile)
               .forEach(File::delete);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void downloadResource(URL url, File localFilename) {
    try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(localFilename); FileChannel fileChannel = fileOutputStream.getChannel()) {

      fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    } catch (IOException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    }
  }

  private static void copyResources(String sourceDirectoryLocation, String destinationDirectoryLocation)  {
    try {
      Files.walk(Paths.get(sourceDirectoryLocation))
           .forEach(source -> {
             Path destination = Paths.get(destinationDirectoryLocation, source.toString().substring(sourceDirectoryLocation.length()));

             if (Files.exists(destination) && !Files.isDirectory(destination)) {
               mergeResources(source.toFile(), destination.toFile());
             } else {
               try {
                 Files.copy(source, destination);
               } catch (IOException e) {
                 e.printStackTrace();
               }
             }
           });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void mergeResources(File source, File dest) {
    Optional<String> fileExtensionOptional = getExtension(dest.getName());
    if (!fileExtensionOptional.isPresent()) {
      Log.warn("What is this");
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
    }
  }

  private static void mergeJSON(File source, File dest) {
    try {
      JsonNode sourceNode = objectMapper.readTree(source);
      JsonNode destNode = objectMapper.readTree(dest);
      objectMapper.writeValue(dest, merge(destNode, sourceNode));
    } catch (IOException e) {
      e.printStackTrace();
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

  private static void unZip(File input, File destDir)  {
    try {
      byte[] buffer = new byte[1024];
      ZipInputStream zis = new ZipInputStream(new FileInputStream(input));
      ZipEntry zipEntry = zis.getNextEntry();

      while (zipEntry != null) {
        File newFile = newFile(destDir, zipEntry);
        if (zipEntry.isDirectory()) {
          if (!newFile.isDirectory() && !newFile.mkdirs()) {
            throw new IOException("Failed to create directory " + newFile);
          }
        } else {
          // fix for Windows-created archives
          File parent = newFile.getParentFile();
          if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent);
          }

          // write file content
          FileOutputStream fos = new FileOutputStream(newFile);
          int len;
          while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
          }
          fos.close();
        }
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
      zis.close();
    } catch (IOException e) {
      Log.error(e.toString());
    }
  }

  private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }
}
