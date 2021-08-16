package studio.craftory.core;

import kr.entree.spigradle.annotations.PluginMain;
import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import java.io.File;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.BlockRenderer;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.BlockRegistry;
import studio.craftory.core.blocks.storage.CraftoryBlockKeyDataType;
import studio.craftory.core.blocks.storage.CraftoryDirectionDataType;
import studio.craftory.core.commands.SpawnItemCommand;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.items.CustomItemManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.core.blocks.listeners.ChunkListener;
import studio.craftory.core.blocks.listeners.BlockInteractionsListener;
import studio.craftory.core.items.recipes.RecipeManager;
import studio.craftory.core.resourcepack.AssetLinker;
import studio.craftory.core.terrian.retro.RetroGeneration;
import studio.craftory.core.utils.Log;

@PluginMain
public final class Craftory extends JavaPlugin {

  @Getter
  private static Craftory instance;

  //Internal
  private Injector injector;
  private AsyncExecutionManager asyncExecutionManager;
  private SyncExecutionManager syncExecutionManager;
  private CustomBlockManager customBlockManager;
  private BlockRenderer blockRenderer;
  private FileConfiguration pluginConfiguration = getConfig();
  private BlockRegistry registry;

  //External
  private CustomItemManager customItemManager;
  private RecipeManager recipeManager;
  private CustomBlockAPI customBlockAPI;
  private RetroGeneration retroGeneration;

  public static CustomItemManager getCustomItemManager() {
    return instance.customItemManager;
  }
  public static RecipeManager getRecipeManager() { return instance.recipeManager; }
  public static CustomBlockAPI getCustomBlockAPI() {return instance.customBlockAPI; }
  public static RetroGeneration getRetoGeneration() {return instance.retroGeneration; }

  public static BlockRegistry blockRegistry() { return instance.registry; }

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
    registry = injector.getSingleton(BlockRegistry.class);
    blockRenderer = injector.getSingleton(BlockRenderer.class);
    customBlockManager = injector.getSingleton(CustomBlockManager.class);

    //API
    customBlockAPI = injector.getSingleton(CustomBlockAPI.class);
    customItemManager = injector.getSingleton(CustomItemManager.class);
    recipeManager = injector.getSingleton(RecipeManager.class);
  }

  @Override
  public void onEnable() {
    //Setup Config
    pluginConfiguration.addDefault("developerDebug", false);
    pluginConfiguration.options().copyDefaults(true);
    saveConfig();

    Log.setDebug(pluginConfiguration.getBoolean("developerDebug"));

    //Register Events
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(blockRenderer, instance);
    pluginManager.registerEvents(injector.getSingleton(BlockInteractionsListener.class), instance);
    pluginManager.registerEvents(injector.getSingleton(ChunkListener.class), instance);
    pluginManager.registerEvents(injector.getSingleton(ItemEventManager.class), instance);
    pluginManager.registerEvents(recipeManager, instance);
    pluginManager.registerEvents(customItemManager, instance);

    //Executor
    asyncExecutionManager.runTaskTimer(this, 20L, 1L);
    syncExecutionManager.runTaskTimer(this, 20L,1L);
    getServer().getPluginManager().registerEvents(new ItemEventManager(), this);

    instance.getServer().getScheduler().runTaskLater(this, this::afterEnable, 1);

    //Commands
    PluginCommand spawnCommand = this.getCommand("spawnItem");
    if(spawnCommand!=null) {
      spawnCommand.setExecutor(new SpawnItemCommand());
    }

  }

  public void afterEnable() {
    AssetLinker linker = injector.getSingleton(AssetLinker.class);
    linker.run();

    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      if (CraftoryAddon.class.isAssignableFrom(plugin.getClass())) {
        ((CraftoryAddon) plugin).craftoryOnEnable();
      }
    }
  }

  public void onResourcesSetup() {
    //Register Events
    PluginManager pluginManager = getServer().getPluginManager();
    retroGeneration = injector.getSingleton(RetroGeneration.class);
    pluginManager.registerEvents(retroGeneration, instance);
  }

  @Override
  public void onDisable() {
    retroGeneration.saveGeneratedChunks();
  }

  public Craftory() {
    super();
  }

  public Craftory(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }
}
