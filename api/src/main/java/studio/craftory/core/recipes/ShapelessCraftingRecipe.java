package studio.craftory.core.recipes;

import java.util.HashMap;
import java.util.Map;
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

    for (int i = 0; i < 9; i++) {
      item = matrix[i];
      if (item != null) {
        if (CustomItemUtils.isCustomItem(item)) {
          String name = CustomItemUtils.getItemName(item);
          String commonName = name.split(":")[1];
          if (uniqueCounts.containsKey(name)) {
            uniqueCounts.put(name, uniqueCounts.get(name) - 1);
          } else if (commonCounts.containsKey(commonName)) {
            commonCounts.put(commonName, commonCounts.get(commonName) - 1);
          }
        } else {
          if (vanillaCounts.containsKey(item.getType())) {
            vanillaCounts.put(item.getType(), vanillaCounts.get(item.getType()) - 1);
          }
        }
      }
    }

    Predicate<Integer> greaterThanZero = (x) -> x > 0;
    if (vanillaCounts.values().stream().anyMatch(greaterThanZero) || uniqueCounts.values().stream().anyMatch(greaterThanZero) || commonCounts.values().stream().anyMatch(greaterThanZero)) {
      e.getInventory().setResult(new ItemStack(Material.AIR));
    }
  }

  @Override
  public void register(Plugin plugin) {
    namespacedKey = new NamespacedKey(plugin, name);

    bukkitRecipe = new ShapelessRecipe(namespacedKey, result);

    vanillaIngredients.forEach((mat, amount) -> bukkitRecipe.addIngredient(amount, mat));
    uniqueItemIngredients.forEach((name, amount) -> bukkitRecipe.addIngredient(amount,
        CustomItemUtils.getCustomItem(name).get().getData()));
    commonItemIngredients.forEach((name, amount) -> bukkitRecipe.addIngredient(amount,
        CustomItemUtils.getCustomItem(name).get().getData()));
    Bukkit.addRecipe(bukkitRecipe);
    Craftory.getRecipeManager().registerRecipe(this);

    // Make common ingredients only contain the common part of the item name
    Map<String, Integer> formattedCommonIngredients = new HashMap<>();
    commonItemIngredients.forEach((name, amount) -> formattedCommonIngredients.put(name.split(":")[1],amount));
    commonItemIngredients = formattedCommonIngredients;
  }
}
