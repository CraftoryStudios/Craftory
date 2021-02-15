package studio.craftory.craftoryexample;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import studio.craftory.core.Craftory;
import studio.craftory.core.CraftoryAddon;
import studio.craftory.core.api.CustomItemAPI;
import studio.craftory.core.items.CustomItem;
import studio.craftory.craftoryexample.blocks.SimpleGenerator;
import studio.craftory.craftoryexample.commands.SpawnGeneratorCommand;
import studio.craftory.craftoryexample.items.Wrench;

public final class CraftoryExamplePlugin extends JavaPlugin implements CraftoryAddon {

  @Override
  public void onLoad() {
    /* Custom Blocks */

    /* Custom Item */
    CustomItem wrench = CustomItem.builder().plugin("example").name("wrench").unbreakable(true).attackDamage(1).handler(PlayerInteractEvent.class,
        Wrench::onClick).displayName("Wrench").material(Material.STICK).displayNameColour(ChatColor.AQUA).holdEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE,1)).build();
    CustomItemAPI.registerCustomItem(wrench);
  }

  @Override
  public void onEnable() {
    Craftory.getInstance().getCustomBlockAPI().registerCustomBlock(this, SimpleGenerator.class);
    this.getCommand("simplegen").setExecutor(new SpawnGeneratorCommand());
  }

}