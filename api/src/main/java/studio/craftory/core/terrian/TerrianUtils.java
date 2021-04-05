package studio.craftory.core.terrian;

import java.util.Random;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;

@UtilityClass
public class TerrianUtils {

  public long getChunkPopulationSeed(int chunkX, int chunkZ, long worldSeed) {
    Random rnd = new Random(worldSeed);
    return chunkX * rnd.nextLong() ^ chunkZ * rnd.nextLong() ^ worldSeed;
  }

  public String getChunkUUID(Chunk chunk) {
    return chunk.getX() + ":" + chunk.getZ() + ":" + chunk.getWorld().getName();
  }
}
