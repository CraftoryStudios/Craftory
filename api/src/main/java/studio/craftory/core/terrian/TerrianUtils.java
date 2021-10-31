package studio.craftory.core.terrian;

import java.util.Random;
import org.bukkit.Chunk;

public final class TerrianUtils {

  private TerrianUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

  public static long getChunkPopulationSeed(int chunkX, int chunkZ, long worldSeed) {
    Random rnd = new Random(worldSeed);
    return chunkX * rnd.nextLong() ^ chunkZ * rnd.nextLong() ^ worldSeed;
  }

  public static String getChunkUUID(Chunk chunk) {
    return chunk.getX() + ":" + chunk.getZ() + ":" + chunk.getWorld().getName();
  }
}
