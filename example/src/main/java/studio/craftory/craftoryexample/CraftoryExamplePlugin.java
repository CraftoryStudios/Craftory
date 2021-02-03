package studio.craftory.craftoryexample;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import studio.craftory.core.CraftoryAddon;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.data.keys.CraftoryKey;
import studio.craftory.core.items.CustomItemManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.craftoryexample.blocks.SimpleGenerator;
import studio.craftory.craftoryexample.commands.SpawnGeneratorCommand;
import studio.craftory.craftoryexample.items.Wrench;

public final class CraftoryExamplePlugin extends JavaPlugin implements CraftoryAddon {

  @Override
  public void onLoad() {

    /* Custom Item */
    /*CraftoryKey itemKey = new CraftoryKey(this,"wrench");
    ItemStack wrench = new ItemStack(Material.STICK);
    ItemMeta itemMeta = wrench.getItemMeta();
    itemMeta.setDisplayName("A WRENCH");
    wrench.setItemMeta(itemMeta);
    CustomItemManager.registerCustomItem(itemKey, wrench);

    ItemEventManager.registerDumbEvent("PlayerInteractEvent", Wrench::onClick);

    /* Custom Blocks */

  }

  @Override
  public void onEnable() {
    CustomBlockAPI.registerCustomBlock(this, SimpleGenerator.class);
    this.getCommand("simplegen").setExecutor(new SpawnGeneratorCommand());
  }

}