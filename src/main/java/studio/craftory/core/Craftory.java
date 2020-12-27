package studio.craftory.core;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.blocks.SimpleIncrease;
import studio.craftory.core.executors.ASSyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;

public final class Craftory extends JavaPlugin {

  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getLogger().info("Plugin now running! NOW");
    ASSyncExecutionManager syncExecutionManager = new ASSyncExecutionManager();
    syncExecutionManager.registerTickableClass(SimpleIncrease.class);
    for (int i = 0; i < 100000; i++) {
      syncExecutionManager.addTickableObject(new SimpleIncrease());

    }
    syncExecutionManager.runTaskTimer(this, 20L, 1L);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
