package studio.craftory.core.items.recipes;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import studio.craftory.core.items.CustomItemUtils;
import studio.craftory.core.utils.Log;

public class RecipeManager implements Listener {

  private final Map<NamespacedKey, ICraftingRecipe> shapedRecipes = new HashMap<>();
  private final Map<NamespacedKey, ICraftingRecipe> shapelessRecipes = new HashMap<>();

  public void registerRecipe(ShapedCraftingRecipe recipe) {
    shapedRecipes.put(recipe.getNamespacedKey(), recipe);
    Log.debug("Registered shaped recipe " + recipe.getNamespacedKey().toString());
  }

  public void registerRecipe(ShapelessCraftingRecipe recipe) {
    shapelessRecipes.put(recipe.getNamespacedKey(), recipe);
    Log.debug("Registered shapeless recipe " + recipe.getNamespacedKey().toString());
  }

  @EventHandler
  public void onPrepareItemCraftEvent(PrepareItemCraftEvent e) {
    if (e.getInventory().getResult() == null || e.getInventory().getResult().getType() == Material.AIR) {
      return;
    }

    Recipe recipe = e.getRecipe();
    if (recipe instanceof ShapedRecipe) {
      handleCraftEvent(shapedRecipes, ((ShapedRecipe) recipe).getKey(), e);
    } else if (recipe instanceof ShapelessRecipe) {
      handleCraftEvent(shapelessRecipes, ((ShapelessRecipe) recipe).getKey(), e);
    }
  }

  private void handleCraftEvent(Map<NamespacedKey, ICraftingRecipe> recipes, NamespacedKey key, PrepareItemCraftEvent e) {
    if (recipes.containsKey(key)) {
      // If it's one of our recipes handle it
      ICraftingRecipe customRecipe = recipes.get(key);
      Player player = (Player) e.getViewers().get(0);
      if (customRecipe.hasPermission(player)) {
        customRecipe.handlePrepareItemCraft(e);
        player.discoverRecipe(key);
      }

    } else {
      // If it's not one of our recipes don't let it use any custom items
      for (ItemStack item : e.getInventory().getMatrix()) {
        if (item != null && CustomItemUtils.isCustomItem(item)) {
          e.getInventory().setResult(new ItemStack(Material.AIR));
          return;
        }
      }
    }
  }

}
