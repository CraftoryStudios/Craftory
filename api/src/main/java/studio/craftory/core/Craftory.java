package studio.craftory.core;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegister;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.core.persistence.PersistenceManager;

public final class Craftory extends JavaPlugin {

  @Getter
  private static Craftory instance;

  //Internal
  private Injector injector;
  private static ArrayList<JavaPlugin> addons = new ArrayList<>();

  private AsyncExecutionManager asyncExecutionManager;
  private SyncExecutionManager syncExecutionManager;

  //External API
  @Getter
  PersistenceManager persistenceManager;
  @Getter
  Gson gson = new Gson();



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

    //Persistence
    persistenceManager = injector.getSingleton(PersistenceManager.class);

    //Custom Block
    injector.getSingleton(CustomBlockRegister.class);
    injector.getSingleton(CustomBlockManager.class);
  }

  @Override
  public void onEnable() {
    //Executor
    asyncExecutionManager.runTaskTimer(this, 20L, 1L);
    getServer().getPluginManager().registerEvents(new ItemEventManager(), this);

  }

  public static void registerCustomBlock(Class<? extends BaseCustomBlock> customBlock) {
    //Test
  }

  public Craftory() {
    super();
  }

  public Craftory(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }
}
