package studio.craftory.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.utils.Log;

public class CraftorySetup extends BukkitRunnable {

  @Override
  public void run() {
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
      }
    }
  }

  private void downloadResource(URL url, File localFilename) {
    try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(localFilename); FileChannel fileChannel = fileOutputStream.getChannel()) {

      fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    } catch (IOException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    }
  }

  private void unZip(File input, File destDir)  {
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
