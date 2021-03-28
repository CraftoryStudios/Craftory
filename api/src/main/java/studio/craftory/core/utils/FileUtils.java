package studio.craftory.core.utils;

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
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {
  public void copyResources(String sourceDirectory, String destinationDirectory, MergeAction mergeAction) {
    try (Stream<Path> stream = Files.walk(Paths.get(sourceDirectory))){
      stream.forEach(source -> {
             Path destination = Paths.get(destinationDirectory, source.toString().substring(sourceDirectory.length()));

             if (Files.exists(destination) && !Files.isDirectory(destination)) {
               mergeAction.merge(source.toFile(), destination.toFile());
             } else {
               try {
                 Files.copy(source, destination);
               } catch (IOException e) {
                 Log.warn("Unable to copy file ", source.toString(), " to location ", destination.toString());
               }
             }
           });
    } catch (IOException e) {
      Log.error(e.toString());
    }
  }

  public void downloadResource(URL url, File localFilename) {
    try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(localFilename); FileChannel fileChannel = fileOutputStream.getChannel()) {

      fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    } catch (IOException fileNotFoundException) {
      Log.error(fileNotFoundException.toString());
    }
  }

  public void unZip(File input, File outputDirectory) {
    byte[] buffer = new byte[1024];
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(input))){
      ZipEntry zipEntry = zis.getNextEntry();

      while (zipEntry != null) {
        File newFile = newFile(outputDirectory, zipEntry);
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
          try (FileOutputStream fos = new FileOutputStream(newFile)) {
            int len;
            while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
          }
        }
        zipEntry = zis.getNextEntry();
      }

    } catch (IOException e) {
      Log.warn(e.toString());
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

  public void recursiveDirectoryDelete(String directory) {
    try (Stream<Path> stream = Files.walk(Paths.get(directory))){
      stream.sorted(Comparator.reverseOrder())
           .map(Path::toFile)
           .forEach(File::delete);
    } catch (IOException e) {
      Log.warn("Couldn't delete directory: " + directory);
    }
  }

  public interface MergeAction {
    void merge(File source, File dest);
  }
}
