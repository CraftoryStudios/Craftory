package studio.craftory.core.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.Craftory;
import studio.craftory.core.items.CustomItemUtils;
import studio.craftory.core.utils.Log;

@Builder
public class ShapelessCraftingRecipe implements ICraftingRecipe{

  @Getter
  private NamespacedKey namespacedKey;


  private ShapelessRecipe bukkitRecipe;
  private final String name;
  private final ItemStack result;
  private final String permission;

  @Singular private final Map<Material, Integer> vanillaIngredients;
  // Full names of allowed custom items
  @Singular private final Map<String, Integer> uniqueItemIngredients;
  // Common names of allowed custom items from any plugin
  @Singular private Map<String, Integer> commonItemIngredients;

  @Override
  public boolean hasPermission(Player player) {
    if (permission==null) return true;
    return player.hasPermission(permission);
  }

  @Override
  public void handlePrepareItemCraft(PrepareItemCraftEvent e) {
    ItemStack[] matrix = e.getInventory().getMatrix();
    ItemStack item;
    Map<Material, Integer> vanillaCounts = new HashMap<>(vanillaIngredients);
    Map<String, Integer> uniqueCounts = new HashMap<>(uniqueItemIngredients);
    Map<String, Integer> commonCounts = new HashMap<>(commonItemIngredients);

    Log.info(vanillaIngredients.toString());
    BiFunction<Object,Integer, Integer> subtractOne = (k, x) -> x-=1;
    for (int i = 0; i < 9; i++) {

      item = matrix[i];

      if (item != null) {
        if (CustomItemUtils.isCustomItem(item)) {
          String itemName = CustomItemUtils.getItemName(item);
          String commonName = itemName.split(":")[1];

          uniqueCounts.computeIfPresent(itemName, subtractOne);
          commonCounts.computeIfPresent(commonName, subtractOne);
        } else {
          vanillaCounts.computeIfPresent(item.getType(), (k, x) -> x-=1);
        }
      }
    }

    Predicate<Integer> greaterThanZero = x -> x > 0;
    if (vanillaCounts.values().stream().anyMatch(greaterThanZero) || uniqueCounts.values().stream().anyMatch(greaterThanZero) || commonCounts.values().stream().anyMatch(greaterThanZero)) {
      e.getInventory().setResult(new ItemStack(Material.AIR));
      Log.warn("DIDNT MEET ALL REQUIREMENTS");
      Log.warn(namespacedKey.toString() + "    "  + vanillaIngredients.toString());
      Log.warn(namespacedKey.toString() + "    "  + vanillaCounts.toString());
    }
  }

  @Override
  public void register(Plugin plugin) {
    // Create the recipe
    namespacedKey = new NamespacedKey(plugin, name);
    bukkitRecipe = new ShapelessRecipe(namespacedKey, result);

    // Set the ingredients
    vanillaIngredients.forEach((mat, amount) -> bukkitRecipe.addIngredient(amount, mat));
    uniqueItemIngredients.forEach((item, amount) -> bukkitRecipe.addIngredient(amount, CustomItemUtils.getCustomItem(item).get().getData()));
    commonItemIngredients.forEach((item, amount) -> bukkitRecipe.addIngredient(amount, CustomItemUtils.getCustomItem(item).get().getData()));

    // Register the recipe
    Bukkit.addRecipe(bukkitRecipe);
    Craftory.getRecipeManager().registerRecipe(this);

    // Make common ingredients only contain the common part of the item name
    Map<String, Integer> formattedCommonIngredients = new HashMap<>();
    commonItemIngredients.forEach((item, amount) -> formattedCommonIngredients.put(item.split(":")[1],amount));
    commonItemIngredients = formattedCommonIngredients;
  }
}
