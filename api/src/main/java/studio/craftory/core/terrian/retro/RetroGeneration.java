package studio.craftory.core.terrian.retro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import studio.craftory.core.Craftory;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.containers.Vector3;
import studio.craftory.core.terrian.TerrianUtils;
import studio.craftory.core.terrian.retro.population.ore.Ore;
import studio.craftory.core.utils.Log;

public class RetroGeneration implements Listener {

  @Inject
  public CustomBlockAPI customBlockAPI;

  private HashSet<Ore> ores = new HashSet<>();
  private Map<World, Set<String>> visitedChunks = new HashMap<>();
  private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public RetroGeneration() {
    loadGeneratedChunks();
  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent chunkLoadEvent) {
    if (visitedChunks.containsKey(chunkLoadEvent.getWorld()) &&
        visitedChunks.get(chunkLoadEvent.getWorld()).contains(TerrianUtils.getChunkUUID(chunkLoadEvent.getChunk()))) return;

    populateOre(chunkLoadEvent.getChunk());
    visitedChunks.computeIfAbsent(chunkLoadEvent.getWorld(), a -> new HashSet<>()).add(TerrianUtils.getChunkUUID(chunkLoadEvent.getChunk()));
  }

  public void saveGeneratedChunks() {
    for (Entry<World, Set<String>> visitedChunksInWorld : visitedChunks.entrySet()) {
      File file =
          new File(Craftory.getInstance().getServer().getWorldContainer().getAbsolutePath() + File.separator + visitedChunksInWorld.getKey().getName() +
              "/Craftory");
      file.mkdirs();
      file = new File(file, "chunkGenerations.json");
      try (FileWriter fw = new FileWriter(file)){
        if (!file.exists() && !file.createNewFile()) {
          Log.error("Couldn't save retro chunk data, save file couldn't be created");
        }
        gson.toJson(visitedChunksInWorld.getValue(), fw);
      } catch (Exception e) {
        Log.error("Couldn't save retro chunk data");
      }
    }
  }

  public void loadGeneratedChunks() {
    for (World world : Craftory.getInstance().getServer().getWorlds()) {
      File file =
          new File(Craftory.getInstance().getServer().getWorldContainer().getAbsolutePath() + File.separator +world.getName() +
              "/Craftory", "chunkGenerations.json");
      if (file.exists()) {
        try {
          gson.fromJson(new FileReader(file), new TypeToken<HashSet<String>>(){}.getType());
        } catch (Exception e) {
          Log.error("Couldn't load retro chunk data");
        }
      }
    }
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
