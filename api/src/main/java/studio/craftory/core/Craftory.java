package studio.craftory.core;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import com.google.gson.Gson;
import java.io.File;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegister;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.core.listeners.ChunkListener;
import studio.craftory.core.listeners.CustomBlockListener;
import studio.craftory.core.listeners.WorldListener;

public final class Craftory extends JavaPlugin {

  @Getter
  private static Craftory instance;

  //Internal
  private Injector injector;
  private AsyncExecutionManager asyncExecutionManager;
  private SyncExecutionManager syncExecutionManager;
  private CustomBlockManager customBlockManager;

  //External API
  @Getter
  Gson gson = new Gson();
  @Getter
  CustomBlockAPI customBlockAPI;



  @Override
  public void onLoad() {
    instance = this;

    //Injector Setup
    injector = new InjectorBuilder().addDefaultHandlers("studio.craftory.core").create();
    injector.register(Craftory.class, instance);
    injector.register(Server.class, getServer());
    injector.register(PluginManager.class, getServer().getPluginManager());

    //Executors
    asyncExecutionManager = injector.getSingleton(AsyncExecutionManager.class);
    syncExecutionManager = injector.getSingleton(SyncExecutionManager.class);

    //Custom Block
    injector.getSingleton(CustomBlockRegister.class);
    customBlockManager = injector.getSingleton(CustomBlockManager.class);

    //API
    customBlockAPI = injector.getSingleton(CustomBlockAPI.class);
  }

  @Override
  public void onEnable() {
    //Load Data
    getServer().getWorlds().forEach(world -> {
      customBlockManager.registerWorld(world);
      for (Chunk chunk : world.getLoadedChunks()) {
        customBlockManager.loadSavedBlocks(chunk);
      }
    });

    //Register Events
    getServer().getPluginManager().registerEvents(new CustomBlockListener(), this);
    getServer().getPluginManager().registerEvents(new WorldListener(), this);
    getServer().getPluginManager().registerEvents(new ChunkListener(), this);

    //Executor
    asyncExecutionManager.runTaskTimer(this, 20L, 1L);
    syncExecutionManager.runTaskTimer(this, 20L,1L);
    getServer().getPluginManager().registerEvents(new ItemEventManager(), this);

  }

  public Craftory() {
    super();
  }

  public Craftory(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }
}
