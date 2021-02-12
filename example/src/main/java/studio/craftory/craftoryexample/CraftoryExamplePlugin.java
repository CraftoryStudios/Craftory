package studio.craftory.craftoryexample;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import studio.craftory.core.Craftory;
import studio.craftory.core.CraftoryAddon;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.data.keys.CraftoryKey;
import studio.craftory.core.items.CustomItemManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.craftoryexample.blocks.SimpleGenerator;
import studio.craftory.craftoryexample.commands.SpawnGeneratorCommand;
import studio.craftory.craftoryexample.items.Wrench;
import studio.craftory.core.data.keys.CraftoryKey;
import studio.craftory.core.executors.interfaces.Tickable;
import studio.craftory.core.items.CustomItemManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.core.persistence.PersistenceManager;
import studio.craftory.craftoryexample.executor.ComplexObject;
import studio.craftory.craftoryexample.executor.SimpleObject;
import studio.craftory.craftoryexample.items.Wrench;
import studio.craftory.craftoryexample.persitence.TestBlock;

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

    CraftoryKey itemKey = new CraftoryKey("example","wrench");
    ItemStack wrench = new ItemStack(Material.STICK);
    ItemMeta itemMeta = wrench.getItemMeta();
    //ItemEventManager.registerDumbEvent(PlayerInteractEvent.class, Wrench::onClick);
    ItemEventManager.registerSmartEvent(PlayerInteractEvent.class, "example:wrench", Wrench::onClick);

    CraftoryKey toolKey = new CraftoryKey("example", "drill");
    ItemStack drill = new ItemStack(Material.GOLDEN_PICKAXE);
    CustomItemManager.setDisplayName(drill, "DRILL");
    CustomItemManager.setUnbreakable(drill, true);
    CustomItemManager.registerCustomItem(toolKey, drill);

    ItemEventManager.registerItemOnHoldEffects("example:drill", Arrays.asList(
        PotionEffectType.FAST_DIGGING.createEffect(Integer.MAX_VALUE,2),
        PotionEffectType.SLOW.createEffect(Integer.MAX_VALUE,1)
        ));
    wrench.setItemMeta(itemMeta);
    CustomItemManager.registerCustomItem(itemKey, wrench);

    itemMeta.setDisplayName("A WRENCH");
  }

  @Override
  public void onEnable() {
    Craftory.getInstance().getCustomBlockAPI().registerCustomBlock(this, SimpleGenerator.class);
    this.getCommand("simplegen").setExecutor(new SpawnGeneratorCommand());
  }

}