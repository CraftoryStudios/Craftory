package studio.craftory.core.items.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
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

public class ShapelessCraftingRecipe implements ICraftingRecipe {

  private final String name;
  private final ItemStack result;
  private final String permission;
  private final Map<Material, Integer> vanillaIngredients;
  // Full names of allowed custom items
  private final Map<String, Integer> uniqueItemIngredients;
  private NamespacedKey namespacedKey;
  private ShapelessRecipe bukkitRecipe;
  // Common names of allowed custom items from any plugin
  private Map<String, Integer> commonItemIngredients;

  ShapelessCraftingRecipe(String name, ItemStack result, String permission, Map<Material, Integer> vanillaIngredients,
      Map<String, Integer> uniqueItemIngredients, NamespacedKey namespacedKey, ShapelessRecipe bukkitRecipe,
      Map<String, Integer> commonItemIngredients) {
    this.name = name;
    this.result = result;
    this.permission = permission;
    this.vanillaIngredients = vanillaIngredients;
    this.uniqueItemIngredients = uniqueItemIngredients;
    this.namespacedKey = namespacedKey;
    this.bukkitRecipe = bukkitRecipe;
    this.commonItemIngredients = commonItemIngredients;
  }

  public static ShapelessCraftingRecipeBuilder builder() {return new ShapelessCraftingRecipeBuilder();}

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
    ItemStack item;
    Map<Material, Integer> vanillaCounts = new HashMap<>(vanillaIngredients);
    Map<String, Integer> uniqueCounts = new HashMap<>(uniqueItemIngredients);
    Map<String, Integer> commonCounts = new HashMap<>(commonItemIngredients);

    BiFunction<Object, Integer, Integer> subtractOne = (k, x) -> x -= 1;
    for (int i = 0; i < 9; i++) {

      item = matrix[i];

      if (item != null) {
        if (CustomItemUtils.isCustomItem(item)) {
          String itemName = CustomItemUtils.getItemName(item);
          String commonName = itemName.split(":")[1];

          uniqueCounts.computeIfPresent(itemName, subtractOne);
          commonCounts.computeIfPresent(commonName, subtractOne);
        } else {
          vanillaCounts.computeIfPresent(item.getType(), (k, x) -> x -= 1);
        }
      }
    }

    Predicate<Integer> greaterThanZero = x -> x > 0;
    if (vanillaCounts.values().stream().anyMatch(greaterThanZero) || uniqueCounts.values().stream().anyMatch(greaterThanZero) || commonCounts.values()
                                                                                                                                             .stream()
                                                                                                                                             .anyMatch(
                                                                                                                                                 greaterThanZero)) {
      e.getInventory().setResult(new ItemStack(Material.AIR));
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
    Craftory.recipeManager().registerRecipe(this);

    // Make common ingredients only contain the common part of the item name
    Map<String, Integer> formattedCommonIngredients = new HashMap<>();
    commonItemIngredients.forEach((item, amount) -> formattedCommonIngredients.put(item.split(":")[1], amount));
    commonItemIngredients = formattedCommonIngredients;
  }

  public NamespacedKey getNamespacedKey() {return this.namespacedKey;}

  public static class ShapelessCraftingRecipeBuilder {

    private String name;
    private ItemStack result;
    private String permission;
    private ArrayList<Material> vanillaIngredients$key;
    private ArrayList<Integer> vanillaIngredients$value;
    private ArrayList<String> uniqueItemIngredients$key;
    private ArrayList<Integer> uniqueItemIngredients$value;
    private NamespacedKey namespacedKey;
    private ShapelessRecipe bukkitRecipe;
    private ArrayList<String> commonItemIngredients$key;
    private ArrayList<Integer> commonItemIngredients$value;

    ShapelessCraftingRecipeBuilder() {}

    public ShapelessCraftingRecipeBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ShapelessCraftingRecipeBuilder result(ItemStack result) {
      this.result = result;
      return this;
    }

    public ShapelessCraftingRecipeBuilder permission(String permission) {
      this.permission = permission;
      return this;
    }

    public ShapelessCraftingRecipeBuilder vanillaIngredient(Material vanillaIngredientKey, Integer vanillaIngredientValue) {
      if (this.vanillaIngredients$key == null) {
        this.vanillaIngredients$key = new ArrayList<Material>();
        this.vanillaIngredients$value = new ArrayList<Integer>();
      }
      this.vanillaIngredients$key.add(vanillaIngredientKey);
      this.vanillaIngredients$value.add(vanillaIngredientValue);
      return this;
    }

    public ShapelessCraftingRecipeBuilder vanillaIngredients(Map<? extends Material, ? extends Integer> vanillaIngredients) {
      if (this.vanillaIngredients$key == null) {
        this.vanillaIngredients$key = new ArrayList<Material>();
        this.vanillaIngredients$value = new ArrayList<Integer>();
      }
      for (final Map.Entry<? extends Material, ? extends Integer> $lombokEntry : vanillaIngredients.entrySet()) {
        this.vanillaIngredients$key.add($lombokEntry.getKey());
        this.vanillaIngredients$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public ShapelessCraftingRecipeBuilder clearVanillaIngredients() {
      if (this.vanillaIngredients$key != null) {
        this.vanillaIngredients$key.clear();
        this.vanillaIngredients$value.clear();
      }
      return this;
    }

    public ShapelessCraftingRecipeBuilder uniqueItemIngredient(String uniqueItemIngredientKey,
        Integer uniqueItemIngredientValue) {
      if (this.uniqueItemIngredients$key == null) {
        this.uniqueItemIngredients$key = new ArrayList<String>();
        this.uniqueItemIngredients$value = new ArrayList<Integer>();
      }
      this.uniqueItemIngredients$key.add(uniqueItemIngredientKey);
      this.uniqueItemIngredients$value.add(uniqueItemIngredientValue);
      return this;
    }

    public ShapelessCraftingRecipeBuilder uniqueItemIngredients(
        Map<? extends String, ? extends Integer> uniqueItemIngredients) {
      if (this.uniqueItemIngredients$key == null) {
        this.uniqueItemIngredients$key = new ArrayList<String>();
        this.uniqueItemIngredients$value = new ArrayList<Integer>();
      }
      for (final Map.Entry<? extends String, ? extends Integer> $lombokEntry : uniqueItemIngredients.entrySet()) {
        this.uniqueItemIngredients$key.add($lombokEntry.getKey());
        this.uniqueItemIngredients$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public ShapelessCraftingRecipeBuilder clearUniqueItemIngredients() {
      if (this.uniqueItemIngredients$key != null) {
        this.uniqueItemIngredients$key.clear();
        this.uniqueItemIngredients$value.clear();
      }
      return this;
    }

    public ShapelessCraftingRecipeBuilder namespacedKey(NamespacedKey namespacedKey) {
      this.namespacedKey = namespacedKey;
      return this;
    }

    public ShapelessCraftingRecipeBuilder bukkitRecipe(ShapelessRecipe bukkitRecipe) {
      this.bukkitRecipe = bukkitRecipe;
      return this;
    }

    public ShapelessCraftingRecipeBuilder commonItemIngredient(String commonItemIngredientKey,
        Integer commonItemIngredientValue) {
      if (this.commonItemIngredients$key == null) {
        this.commonItemIngredients$key = new ArrayList<String>();
        this.commonItemIngredients$value = new ArrayList<Integer>();
      }
      this.commonItemIngredients$key.add(commonItemIngredientKey);
      this.commonItemIngredients$value.add(commonItemIngredientValue);
      return this;
    }

    public ShapelessCraftingRecipeBuilder commonItemIngredients(
        Map<? extends String, ? extends Integer> commonItemIngredients) {
      if (this.commonItemIngredients$key == null) {
        this.commonItemIngredients$key = new ArrayList<String>();
        this.commonItemIngredients$value = new ArrayList<Integer>();
      }
      for (final Map.Entry<? extends String, ? extends Integer> $lombokEntry : commonItemIngredients.entrySet()) {
        this.commonItemIngredients$key.add($lombokEntry.getKey());
        this.commonItemIngredients$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public ShapelessCraftingRecipeBuilder clearCommonItemIngredients() {
      if (this.commonItemIngredients$key != null) {
        this.commonItemIngredients$key.clear();
        this.commonItemIngredients$value.clear();
      }
      return this;
    }

    public ShapelessCraftingRecipe build() {
      Map<Material, Integer> vanillaIngredients;
      switch (this.vanillaIngredients$key == null ? 0 : this.vanillaIngredients$key.size()) {
        case 0:
          vanillaIngredients = java.util.Collections.emptyMap();
          break;
        case 1:
          vanillaIngredients = java.util.Collections.singletonMap(this.vanillaIngredients$key.get(0), this.vanillaIngredients$value.get(0));
          break;
        default:
          vanillaIngredients = new java.util.LinkedHashMap<Material, Integer>(
              this.vanillaIngredients$key.size() < 1073741824 ? 1 + this.vanillaIngredients$key.size() + (this.vanillaIngredients$key.size() - 3) / 3
                  : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.vanillaIngredients$key.size(); $i++) {
            vanillaIngredients.put(this.vanillaIngredients$key.get($i), (Integer) this.vanillaIngredients$value.get($i));
          }
          vanillaIngredients = java.util.Collections.unmodifiableMap(vanillaIngredients);
      }
      Map<String, Integer> uniqueItemIngredients;
      switch (this.uniqueItemIngredients$key == null ? 0 : this.uniqueItemIngredients$key.size()) {
        case 0:
          uniqueItemIngredients = java.util.Collections.emptyMap();
          break;
        case 1:
          uniqueItemIngredients = java.util.Collections.singletonMap(this.uniqueItemIngredients$key.get(0), this.uniqueItemIngredients$value.get(0));
          break;
        default:
          uniqueItemIngredients = new java.util.LinkedHashMap<String, Integer>(
              this.uniqueItemIngredients$key.size() < 1073741824 ? 1 + this.uniqueItemIngredients$key.size()
                  + (this.uniqueItemIngredients$key.size() - 3) / 3 : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.uniqueItemIngredients$key.size(); $i++) {
            uniqueItemIngredients.put(this.uniqueItemIngredients$key.get($i), (Integer) this.uniqueItemIngredients$value.get($i));
          }
          uniqueItemIngredients = java.util.Collections.unmodifiableMap(uniqueItemIngredients);
      }
      Map<String, Integer> commonItemIngredients;
      switch (this.commonItemIngredients$key == null ? 0 : this.commonItemIngredients$key.size()) {
        case 0:
          commonItemIngredients = java.util.Collections.emptyMap();
          break;
        case 1:
          commonItemIngredients = java.util.Collections.singletonMap(this.commonItemIngredients$key.get(0), this.commonItemIngredients$value.get(0));
          break;
        default:
          commonItemIngredients = new java.util.LinkedHashMap<String, Integer>(
              this.commonItemIngredients$key.size() < 1073741824 ? 1 + this.commonItemIngredients$key.size()
                  + (this.commonItemIngredients$key.size() - 3) / 3 : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.commonItemIngredients$key.size(); $i++) {
            commonItemIngredients.put(this.commonItemIngredients$key.get($i), (Integer) this.commonItemIngredients$value.get($i));
          }
          commonItemIngredients = java.util.Collections.unmodifiableMap(commonItemIngredients);
      }

      return new ShapelessCraftingRecipe(name, result, permission, vanillaIngredients, uniqueItemIngredients, namespacedKey, bukkitRecipe,
          commonItemIngredients);
    }

    public String toString() {
      return "ShapelessCraftingRecipe.ShapelessCraftingRecipeBuilder(name=" + this.name + ", result=" + this.result + ", permission="
          + this.permission
          + ", vanillaIngredients$key=" + this.vanillaIngredients$key + ", vanillaIngredients$value=" + this.vanillaIngredients$value
          + ", uniqueItemIngredients$key=" + this.uniqueItemIngredients$key + ", uniqueItemIngredients$value=" + this.uniqueItemIngredients$value
          + ", namespacedKey=" + this.namespacedKey + ", bukkitRecipe=" + this.bukkitRecipe + ", commonItemIngredients$key="
          + this.commonItemIngredients$key + ", commonItemIngredients$value=" + this.commonItemIngredients$value + ")";
    }
  }
}
