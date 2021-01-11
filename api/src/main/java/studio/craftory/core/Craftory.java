package studio.craftory.core;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import java.util.ArrayList;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import studio.craftory.core.blocks.BaseCustomBlock;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.persistence.PersistenceManager;

public final class Craftory extends JavaPlugin {

  @Getter
  private static Craftory instance;

  //Internal
  private Injector injector;
  private static ArrayList<JavaPlugin> addons = new ArrayList<>();

  //External
  @Getter
  AsyncExecutionManager asyncExecutionManager;
  @Getter
  PersistenceManager persistenceManager;



  @Override
  public void onLoad() {
    instance = this;

    //Injector Setup
    injector = new InjectorBuilder().addDefaultHandlers("studio.craftory.core").create();
    injector.register(Craftory.class, instance);
    injector.register(Server.class, getServer());
    injector.register(PluginManager.class, getServer().getPluginManager());

    //Executor
    asyncExecutionManager = new AsyncExecutionManager(4);

    //Persistence
    persistenceManager = new PersistenceManager();
  }

  @Override
  public void onEnable() {
    //Executor
    asyncExecutionManager.runTaskTimer(this, 20L, 1L);
  }

  public static void registerCustomBlock(BaseCustomBlock customBlock) {
    //Test
  }
}
