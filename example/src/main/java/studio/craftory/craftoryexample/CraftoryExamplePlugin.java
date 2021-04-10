package studio.craftory.craftoryexample;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import studio.craftory.core.Craftory;
import studio.craftory.core.CraftoryAddon;
import studio.craftory.core.blocks.rendering.renderers.DefaultRotationalRenderer;
import studio.craftory.core.data.IntRange;
import studio.craftory.core.data.keys.ItemDataKey;
import studio.craftory.core.items.CustomItem;
import studio.craftory.core.recipes.ShapedCraftingRecipe;
import studio.craftory.core.recipes.ShapelessCraftingRecipe;
import studio.craftory.core.terrian.retro.population.ore.VanillaOre;
import studio.craftory.core.utils.Log;
import studio.craftory.craftoryexample.blocks.CopperOre;
import studio.craftory.craftoryexample.blocks.SimpleGenerator;
import studio.craftory.craftoryexample.items.Wrench;

public final class CraftoryExamplePlugin extends JavaPlugin implements CraftoryAddon {

  @Override
  public void onLoad() {
    /* Custom Blocks */

    /* Custom Item */
    ItemDataKey magicalPower = new ItemDataKey(new NamespacedKey(this, "magical-power"), PersistentDataType.INTEGER);
    CustomItem wrench = CustomItem.builder()
                                  .name("wrench").unbreakable(true)
                                  .attackDamage(1).handler(PlayerInteractEvent.class, Wrench::onClick)
                                  .displayName("Wrench").material(Material.STICK)
                                  .displayNameColour(ChatColor.AQUA).holdEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE,1))
                                  .attribute(magicalPower, 100).build();
    wrench.register(this);
    ItemStack res = wrench.getItem();
    res.setAmount(6);
    ShapedCraftingRecipe.builder().name("wrenchdoubler").recipe(new String[]{"XWX", "XWX", "XWX"}).commonItemIngredient('W',"craftoryexample:wrench").result(res).build().register(this);
    res.setAmount(1);
    ShapedCraftingRecipe.builder().name("wrench").recipe(new String[]{"SXS","XSX","XSX"}).vanillaIngredient('S',Material.STICK).result(res).build().register(this);

    ItemStack diamonds = new ItemStack(Material.DIAMOND_BLOCK);
    diamonds.setAmount(64);
    ShapelessCraftingRecipe.builder().name("testytesttest").vanillaIngredient(Material.NETHERITE_BLOCK,3).result(diamonds).build().register(this);

    CustomItem superStar = CustomItem.builder().name("superstar").displayName("Super Star").material(Material.NETHER_STAR).build();
    superStar.register(this);
    ItemStack emeralds = new ItemStack(Material.EMERALD_BLOCK);
    emeralds.setAmount(64);
    ShapelessCraftingRecipe.builder().name("superVersion").commonItemIngredient("craftoryexample:superstar", 3).result(emeralds).build().register(this);
  }

  @Override
  public void onEnable() {
    String[] test = {"custom/block/machine/generator_n"};
    Craftory.getCustomBlockAPI().registerCustomBlock(this, SimpleGenerator.class, test, DefaultRotationalRenderer.class);

    String[] test1 = {"custom/block/mineral/copper/copper_ore"};
    Craftory.getCustomBlockAPI().registerCustomBlock(this, CopperOre.class, test1);
  }

  @Override
  public void craftoryOnEnable() {
    HashSet<Material> set = new HashSet<>(Arrays.asList(Material.STONE));
    Craftory.getRetoGeneration().registerOre(new VanillaOre(CopperOre.class,
        set, new IntRange(20,33), new IntRange(5, 60),
        new IntRange(3,10)));
  }

  @Override
  public URL getAddonResources() {
    try {
      return new URL("https://www.dropbox.com/s/l6q9uevu2cjpcju/CraftoryCore.zip?raw=1");
    } catch (MalformedURLException e) {
      Log.error(e.toString());
    }
    return null;
  }
}