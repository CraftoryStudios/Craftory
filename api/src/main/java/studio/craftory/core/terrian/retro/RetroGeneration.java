package studio.craftory.core.terrian.retro;

import java.util.HashSet;
import java.util.Random;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.data.Vector3;
import studio.craftory.core.terrian.TerrianUtils;
import studio.craftory.core.terrian.retro.population.ore.Ore;

public class RetroGeneration implements Listener {

  @Inject
  public CustomBlockAPI customBlockAPI;

  private HashSet<Ore> ores = new HashSet<>();
  //TODO this doesn't save or account for added generation
  private HashSet<String> visitedChunks = new HashSet<>();

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent chunkLoadEvent) {
    if (visitedChunks.contains(TerrianUtils.getChunkUUID(chunkLoadEvent.getChunk()))) return;

    populateOre(chunkLoadEvent.getChunk());
    visitedChunks.add(TerrianUtils.getChunkUUID(chunkLoadEvent.getChunk()));
  }

  public void registerOre(Ore ore) {
    ore.injectAPI(customBlockAPI);
    ores.add(ore);
  }

  public void populateOre(@NonNull Chunk chunk) {
    for (int cx = -1; cx <= 1; cx++) {
      for (int cz = -1; cz <= 1; cz++) {
        Random random = new Random(TerrianUtils.getChunkPopulationSeed(chunk.getX() + cx, chunk.getZ() + cz, chunk.getWorld().getSeed()));
        for (Ore ore : ores) {
          int amountOfOre = ore.getAmount().getRandomInRange(random);
          for(int i = 0; i < amountOfOre; i++) {
            Vector3 location = new Vector3(random.nextInt(16) + 16 * cx, ore.getHeight().getRandomInRange(random), random.nextInt(16) + 16 * cz);
            ore.generate(location, chunk, random);
          }
        }
      }
    }
  }
}
