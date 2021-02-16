package studio.craftory.core;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import java.io.File;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegistry;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.core.listeners.ChunkListener;
import studio.craftory.core.listeners.CustomBlockListener;
import studio.craftory.core.listeners.WorldListener;
import studio.craftory.core.utils.Log;

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
  CustomBlockAPI customBlockAPI;



  @Override
  public void onLoad() {
    instance = this;
    Log.setLogger(this.getLogger());

    //Injector Setup
    injector = new InjectorBuilder().addDefaultHandlers("studio.craftory.core").create();
    injector.register(Craftory.class, instance);
    injector.register(Server.class, getServer());
    injector.register(PluginManager.class, getServer().getPluginManager());

    //Executors
    asyncExecutionManager = injector.getSingleton(AsyncExecutionManager.class);
    syncExecutionManager = injector.getSingleton(SyncExecutionManager.class);

    //Custom Block
    injector.getSingleton(CustomBlockRegistry.class);
    customBlockManager = injector.getSingleton(CustomBlockManager.class);

    //API
    customBlockAPI = injector.getSingleton(CustomBlockAPI.class);
  }

  @Override
  public void onEnable() {
    //Load Data
    getServer().getWorlds().forEach(world -> customBlockManager.getDataStorageManager().registerWorld(world));

    //Register Events
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(injector.getSingleton(CustomBlockListener.class), instance);
    pluginManager.registerEvents(injector.getSingleton(WorldListener.class), instance);
    pluginManager.registerEvents(injector.getSingleton(ChunkListener.class), instance);
    pluginManager.registerEvents(injector.getSingleton(ItemEventManager.class), instance);

    //Executor
    asyncExecutionManager.runTaskTimer(this, 20L, 1L);
    syncExecutionManager.runTaskTimer(this, 20L,1L);
    getServer().getPluginManager().registerEvents(new ItemEventManager(), this);
  }

  @Override
  public void onDisable() {
    customBlockManager.getDataStorageManager().writeAll();
    customBlockManager.getDataStorageManager().saveAll();
  }

  public Craftory() {
    super();
  }

  public Craftory(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }
}
