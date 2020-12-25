package studio.craftory.core;

import org.bukkit.plugin.java.JavaPlugin;

public final class Craftory extends JavaPlugin {

  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getLogger().info("Plugin now running!");
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
