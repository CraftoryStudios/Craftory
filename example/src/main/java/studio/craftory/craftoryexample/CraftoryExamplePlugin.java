package studio.craftory.craftoryexample;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import kr.entree.spigradle.annotations.PluginMain;
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
import studio.craftory.core.containers.keys.ItemDataKey;
import studio.craftory.core.items.CustomItem;
import studio.craftory.core.items.recipes.ShapedCraftingRecipe;
import studio.craftory.core.items.recipes.ShapelessCraftingRecipe;
import studio.craftory.core.utils.Log;
import studio.craftory.craftoryexample.blocks.CopperOre;
import studio.craftory.craftoryexample.blocks.SimpleGenerator;
import studio.craftory.craftoryexample.items.Wrench;


@PluginMain
public final class CraftoryExamplePlugin extends JavaPlugin implements CraftoryAddon {

  @Override
  public void onLoad() {
    /* Custom Blocks */

    /* Custom Item */

  }

  @Override
  public void onEnable() {
    String[] test = {"custom/block/machine/generator_n"};
    Craftory.customBlockAPI().registerCustomBlock(this, SimpleGenerator.class, test, DefaultRotationalRenderer.class);

    String[] test1 = {"custom/block/mineral/copper/copper_ore"};
    Craftory.customBlockAPI().registerCustomBlock(this, CopperOre.class, test1);

    ItemDataKey magicalPower = new ItemDataKey(new NamespacedKey(this, "magical-power"), PersistentDataType.INTEGER);
    CustomItem wrench = CustomItem.builder()
                                  .name("wrench").unbreakable(true)
                                  .modelPath("craftoryexample:items/wrench")
                                  .attackDamage(1).handler(PlayerInteractEvent.class, Wrench::onClick)
                                  .displayName("Wrench").material(Material.STICK)
                                  .displayNameColour(ChatColor.AQUA).holdEffect(
            PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 1))
                                  .attribute(magicalPower, 100).build();
    wrench.register(this);

    CustomItem.builder().name("superstar").displayName("Super Star").material(Material.NETHER_STAR)
              .modelPath("craftoryexample:items/superstar").build().register(this);

  }


  @Override
  public void craftoryOnEnable() {
    Optional<ItemStack> itemStackOptional = Craftory.getCustomItemManager().getCustomItem("craftoryexample:wrench");
    ItemStack res;
    if (itemStackOptional.isPresent()) {
      res = itemStackOptional.get();
    } else {
      throw new NullPointerException("ItemStack can't be null");
    }
    res.setAmount(6);
    ShapedCraftingRecipe.builder().name("wrenchdoubler").recipe(new String[]{"XWX", "XWX", "XWX"}).commonItemIngredient('W', "craftoryexample:wrench")
                        .result(res).build().register(this);
    res.setAmount(1);
    ShapedCraftingRecipe.builder().name("wrench").recipe(new String[]{"SXS", "XSX", "XSX"}).vanillaIngredient('S', Material.STICK).result(res).build()
                        .register(this);

    ItemStack diamonds = new ItemStack(Material.DIAMOND_BLOCK);
    diamonds.setAmount(64);
    ShapelessCraftingRecipe
        .builder().name("testytesttest").vanillaIngredient(Material.NETHERITE_BLOCK, 3).result(diamonds).build().register(this);
    ItemStack emeralds = new ItemStack(Material.EMERALD_BLOCK);
    emeralds.setAmount(64);
    ShapelessCraftingRecipe.builder().name("superVersion").commonItemIngredient("craftoryexample:superstar", 3).result(emeralds).build()
                           .register(this);
  }


  @Override
  public URL getAddonResources() {
    try {
      return new URL("https://www.dropbox.com/s/1n0tgldil2ov098/CraftoryCore.zip?raw=1");
    } catch (MalformedURLException e) {
      Log.error(e.toString());
    }
    return null;
  }
}