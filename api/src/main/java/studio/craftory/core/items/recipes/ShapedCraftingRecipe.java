package studio.craftory.core.items.recipes;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.Craftory;
import studio.craftory.core.items.CustomItemUtils;

@Builder
public class ShapedCraftingRecipe implements ICraftingRecipe {

  private final String name;
  private final ItemStack result;
  private final String[] recipe;
  private final String permission;
  // Full names of allowed custom items
  @Singular
  private final Map<Character, String> uniqueItemIngredients;
  // Common names of allowed custom items from any plugin
  @Singular
  private final Map<Character, String> commonItemIngredients;
  // Vanilla materials
  @Singular
  private final Map<Character, Material> vanillaIngredients;
  // Sets of allowed vanilla materials
  @Singular
  private final Map<Character, List<Material>> vanillaIngredientGroups;
  @Getter
  private NamespacedKey namespacedKey;
  private ShapedRecipe bukkitRecipe;

  @Override
  public void register(Plugin plugin) {
    // Create Recipe
    namespacedKey = new NamespacedKey(plugin, name);
    bukkitRecipe = new ShapedRecipe(namespacedKey, result);
    bukkitRecipe.shape(recipe);

    // Set the ingredients
    uniqueItemIngredients.forEach((c, item) -> bukkitRecipe.setIngredient(c, CustomItemUtils.getCustomItem(item).get().getData()));
    commonItemIngredients.forEach((c, item) -> bukkitRecipe.setIngredient(c, CustomItemUtils.getCustomItem(item).get().getData()));
    vanillaIngredients.forEach(bukkitRecipe::setIngredient);
    vanillaIngredientGroups.forEach((c, mats) -> bukkitRecipe.setIngredient(c, new MaterialChoice(mats)));

    // Register the recipe
    Bukkit.addRecipe(bukkitRecipe);
    Craftory.recipeManager().registerRecipe(this);
  }

  @Override
  public boolean hasPermission(Player player) {
    if (permission == null) {
      return true;
    }
    return player.hasPermission(permission);
  }

  @Override
  public void handlePrepareItemCraft(PrepareItemCraftEvent e) {
    ItemStack[] matrix = e.getInventory().getMatrix();
    char[] pattern = String.join("", this.recipe).toCharArray();
    char ingredient;
    ItemStack item;
    boolean valid = true;

    for (int i = 0; i < 9; i++) {
      ingredient = pattern[i];
      item = matrix[i];

      if (vanillaIngredients.containsKey(ingredient)) {
        if (!validateVanillaItem(item, ingredient)) {
          valid = false;
        }
      } else if (vanillaIngredientGroups.containsKey(ingredient)) {
        if (!validateVanillaItemGroup(item, ingredient)) {
          valid = false;
        }
      } else if (commonItemIngredients.containsKey(ingredient)) {
        if (!validateCommonItem(item, ingredient)) {
          valid = false;
        }
      } else if (uniqueItemIngredients.containsKey(ingredient)) {
        if (!validateUniqueItem(item, ingredient)) {
          valid = false;
        }
      }

      if (!valid) {
        e.getInventory().setResult(new ItemStack(Material.AIR));
        break;
      }
    }

  }

  private boolean validateVanillaItem(ItemStack item, char ingredient) {
    return item != null && item.getType() == vanillaIngredients.get(ingredient) && !CustomItemUtils.isCustomItem(item);
  }

  private boolean validateVanillaItemGroup(ItemStack item, char ingredient) {
    return item != null && vanillaIngredientGroups.get(ingredient).contains(item.getType()) && !CustomItemUtils.isCustomItem(item);
  }

  private boolean validateCommonItem(ItemStack item, char ingredient) {
    return item != null && CustomItemUtils.isCustomItem(item) && CustomItemUtils.matchCustomItemCommonName(item,
        commonItemIngredients.get(ingredient));
  }

  private boolean validateUniqueItem(ItemStack item, char ingredient) {
    return item != null && CustomItemUtils.isCustomItem(item) && CustomItemUtils.matchCustomItemName(item, uniqueItemIngredients.get(ingredient));
  }
}
