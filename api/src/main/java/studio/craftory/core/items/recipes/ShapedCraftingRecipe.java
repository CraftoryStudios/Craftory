package studio.craftory.core.items.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

public class ShapedCraftingRecipe implements ICraftingRecipe {

  private final String name;
  private final ItemStack result;
  private final String[] recipe;
  private final String permission;
  // Full names of allowed custom items
  private final Map<Character, String> uniqueItemIngredients;
  // Common names of allowed custom items from any plugin
  private final Map<Character, String> commonItemIngredients;
  // Vanilla materials
  private final Map<Character, Material> vanillaIngredients;
  // Sets of allowed vanilla materials
  private final Map<Character, List<Material>> vanillaIngredientGroups;
  private NamespacedKey namespacedKey;
  private ShapedRecipe bukkitRecipe;

  ShapedCraftingRecipe(String name, ItemStack result, String[] recipe, String permission, Map<Character, String> uniqueItemIngredients,
      Map<Character, String> commonItemIngredients, Map<Character, Material> vanillaIngredients,
      Map<Character, List<Material>> vanillaIngredientGroups, NamespacedKey namespacedKey, ShapedRecipe bukkitRecipe) {
    this.name = name;
    this.result = result;
    this.recipe = recipe;
    this.permission = permission;
    this.uniqueItemIngredients = uniqueItemIngredients;
    this.commonItemIngredients = commonItemIngredients;
    this.vanillaIngredients = vanillaIngredients;
    this.vanillaIngredientGroups = vanillaIngredientGroups;
    this.namespacedKey = namespacedKey;
    this.bukkitRecipe = bukkitRecipe;
  }

  public static ShapedCraftingRecipeBuilder builder() {return new ShapedCraftingRecipeBuilder();}

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

  public NamespacedKey getNamespacedKey() {return this.namespacedKey;}

  public static class ShapedCraftingRecipeBuilder {

    private String name;
    private ItemStack result;
    private String[] recipe;
    private String permission;
    private ArrayList<Character> uniqueItemIngredients$key;
    private ArrayList<String> uniqueItemIngredients$value;
    private ArrayList<Character> commonItemIngredients$key;
    private ArrayList<String> commonItemIngredients$value;
    private ArrayList<Character> vanillaIngredients$key;
    private ArrayList<Material> vanillaIngredients$value;
    private ArrayList<Character> vanillaIngredientGroups$key;
    private ArrayList<List<Material>> vanillaIngredientGroups$value;
    private NamespacedKey namespacedKey;
    private ShapedRecipe bukkitRecipe;

    ShapedCraftingRecipeBuilder() {}

    public ShapedCraftingRecipeBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ShapedCraftingRecipeBuilder result(ItemStack result) {
      this.result = result;
      return this;
    }

    public ShapedCraftingRecipeBuilder recipe(String[] recipe) {
      this.recipe = recipe;
      return this;
    }

    public ShapedCraftingRecipeBuilder permission(String permission) {
      this.permission = permission;
      return this;
    }

    public ShapedCraftingRecipeBuilder uniqueItemIngredient(Character uniqueItemIngredientKey, String uniqueItemIngredientValue) {
      if (this.uniqueItemIngredients$key == null) {
        this.uniqueItemIngredients$key = new ArrayList<Character>();
        this.uniqueItemIngredients$value = new ArrayList<String>();
      }
      this.uniqueItemIngredients$key.add(uniqueItemIngredientKey);
      this.uniqueItemIngredients$value.add(uniqueItemIngredientValue);
      return this;
    }

    public ShapedCraftingRecipeBuilder uniqueItemIngredients(Map<? extends Character, ? extends String> uniqueItemIngredients) {
      if (this.uniqueItemIngredients$key == null) {
        this.uniqueItemIngredients$key = new ArrayList<Character>();
        this.uniqueItemIngredients$value = new ArrayList<String>();
      }
      for (final Map.Entry<? extends Character, ? extends String> $lombokEntry : uniqueItemIngredients.entrySet()) {
        this.uniqueItemIngredients$key.add($lombokEntry.getKey());
        this.uniqueItemIngredients$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder clearUniqueItemIngredients() {
      if (this.uniqueItemIngredients$key != null) {
        this.uniqueItemIngredients$key.clear();
        this.uniqueItemIngredients$value.clear();
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder commonItemIngredient(Character commonItemIngredientKey, String commonItemIngredientValue) {
      if (this.commonItemIngredients$key == null) {
        this.commonItemIngredients$key = new ArrayList<Character>();
        this.commonItemIngredients$value = new ArrayList<String>();
      }
      this.commonItemIngredients$key.add(commonItemIngredientKey);
      this.commonItemIngredients$value.add(commonItemIngredientValue);
      return this;
    }

    public ShapedCraftingRecipeBuilder commonItemIngredients(Map<? extends Character, ? extends String> commonItemIngredients) {
      if (this.commonItemIngredients$key == null) {
        this.commonItemIngredients$key = new ArrayList<Character>();
        this.commonItemIngredients$value = new ArrayList<String>();
      }
      for (final Map.Entry<? extends Character, ? extends String> $lombokEntry : commonItemIngredients.entrySet()) {
        this.commonItemIngredients$key.add($lombokEntry.getKey());
        this.commonItemIngredients$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder clearCommonItemIngredients() {
      if (this.commonItemIngredients$key != null) {
        this.commonItemIngredients$key.clear();
        this.commonItemIngredients$value.clear();
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder vanillaIngredient(Character vanillaIngredientKey, Material vanillaIngredientValue) {
      if (this.vanillaIngredients$key == null) {
        this.vanillaIngredients$key = new ArrayList<Character>();
        this.vanillaIngredients$value = new ArrayList<Material>();
      }
      this.vanillaIngredients$key.add(vanillaIngredientKey);
      this.vanillaIngredients$value.add(vanillaIngredientValue);
      return this;
    }

    public ShapedCraftingRecipeBuilder vanillaIngredients(Map<? extends Character, ? extends Material> vanillaIngredients) {
      if (this.vanillaIngredients$key == null) {
        this.vanillaIngredients$key = new ArrayList<Character>();
        this.vanillaIngredients$value = new ArrayList<Material>();
      }
      for (final Map.Entry<? extends Character, ? extends Material> $lombokEntry : vanillaIngredients.entrySet()) {
        this.vanillaIngredients$key.add($lombokEntry.getKey());
        this.vanillaIngredients$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder clearVanillaIngredients() {
      if (this.vanillaIngredients$key != null) {
        this.vanillaIngredients$key.clear();
        this.vanillaIngredients$value.clear();
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder vanillaIngredientGroup(Character vanillaIngredientGroupKey,
        List<Material> vanillaIngredientGroupValue) {
      if (this.vanillaIngredientGroups$key == null) {
        this.vanillaIngredientGroups$key = new ArrayList<Character>();
        this.vanillaIngredientGroups$value = new ArrayList<List<Material>>();
      }
      this.vanillaIngredientGroups$key.add(vanillaIngredientGroupKey);
      this.vanillaIngredientGroups$value.add(vanillaIngredientGroupValue);
      return this;
    }

    public ShapedCraftingRecipeBuilder vanillaIngredientGroups(
        Map<? extends Character, ? extends List<Material>> vanillaIngredientGroups) {
      if (this.vanillaIngredientGroups$key == null) {
        this.vanillaIngredientGroups$key = new ArrayList<Character>();
        this.vanillaIngredientGroups$value = new ArrayList<List<Material>>();
      }
      for (final Map.Entry<? extends Character, ? extends List<Material>> $lombokEntry : vanillaIngredientGroups.entrySet()) {
        this.vanillaIngredientGroups$key.add($lombokEntry.getKey());
        this.vanillaIngredientGroups$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder clearVanillaIngredientGroups() {
      if (this.vanillaIngredientGroups$key != null) {
        this.vanillaIngredientGroups$key.clear();
        this.vanillaIngredientGroups$value.clear();
      }
      return this;
    }

    public ShapedCraftingRecipeBuilder namespacedKey(NamespacedKey namespacedKey) {
      this.namespacedKey = namespacedKey;
      return this;
    }

    public ShapedCraftingRecipeBuilder bukkitRecipe(ShapedRecipe bukkitRecipe) {
      this.bukkitRecipe = bukkitRecipe;
      return this;
    }

    public ShapedCraftingRecipe build() {
      Map<Character, String> uniqueItemIngredients;
      switch (this.uniqueItemIngredients$key == null ? 0 : this.uniqueItemIngredients$key.size()) {
        case 0:
          uniqueItemIngredients = java.util.Collections.emptyMap();
          break;
        case 1:
          uniqueItemIngredients = java.util.Collections.singletonMap(this.uniqueItemIngredients$key.get(0), this.uniqueItemIngredients$value.get(0));
          break;
        default:
          uniqueItemIngredients = new java.util.LinkedHashMap<Character, String>(
              this.uniqueItemIngredients$key.size() < 1073741824 ? 1 + this.uniqueItemIngredients$key.size()
                  + (this.uniqueItemIngredients$key.size() - 3) / 3 : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.uniqueItemIngredients$key.size(); $i++) {
            uniqueItemIngredients.put(this.uniqueItemIngredients$key.get($i), (String) this.uniqueItemIngredients$value.get($i));
          }
          uniqueItemIngredients = java.util.Collections.unmodifiableMap(uniqueItemIngredients);
      }
      Map<Character, String> commonItemIngredients;
      switch (this.commonItemIngredients$key == null ? 0 : this.commonItemIngredients$key.size()) {
        case 0:
          commonItemIngredients = java.util.Collections.emptyMap();
          break;
        case 1:
          commonItemIngredients = java.util.Collections.singletonMap(this.commonItemIngredients$key.get(0), this.commonItemIngredients$value.get(0));
          break;
        default:
          commonItemIngredients = new java.util.LinkedHashMap<Character, String>(
              this.commonItemIngredients$key.size() < 1073741824 ? 1 + this.commonItemIngredients$key.size()
                  + (this.commonItemIngredients$key.size() - 3) / 3 : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.commonItemIngredients$key.size(); $i++) {
            commonItemIngredients.put(this.commonItemIngredients$key.get($i), (String) this.commonItemIngredients$value.get($i));
          }
          commonItemIngredients = java.util.Collections.unmodifiableMap(commonItemIngredients);
      }
      Map<Character, Material> vanillaIngredients;
      switch (this.vanillaIngredients$key == null ? 0 : this.vanillaIngredients$key.size()) {
        case 0:
          vanillaIngredients = java.util.Collections.emptyMap();
          break;
        case 1:
          vanillaIngredients = java.util.Collections.singletonMap(this.vanillaIngredients$key.get(0), this.vanillaIngredients$value.get(0));
          break;
        default:
          vanillaIngredients = new java.util.LinkedHashMap<Character, Material>(
              this.vanillaIngredients$key.size() < 1073741824 ? 1 + this.vanillaIngredients$key.size() + (this.vanillaIngredients$key.size() - 3) / 3
                  : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.vanillaIngredients$key.size(); $i++) {
            vanillaIngredients.put(this.vanillaIngredients$key.get($i), (Material) this.vanillaIngredients$value.get($i));
          }
          vanillaIngredients = java.util.Collections.unmodifiableMap(vanillaIngredients);
      }
      Map<Character, List<Material>> vanillaIngredientGroups;
      switch (this.vanillaIngredientGroups$key == null ? 0 : this.vanillaIngredientGroups$key.size()) {
        case 0:
          vanillaIngredientGroups = java.util.Collections.emptyMap();
          break;
        case 1:
          vanillaIngredientGroups = java.util.Collections.singletonMap(this.vanillaIngredientGroups$key.get(0),
              this.vanillaIngredientGroups$value.get(0));
          break;
        default:
          vanillaIngredientGroups = new java.util.LinkedHashMap<Character, List<Material>>(
              this.vanillaIngredientGroups$key.size() < 1073741824 ? 1 + this.vanillaIngredientGroups$key.size()
                  + (this.vanillaIngredientGroups$key.size() - 3) / 3 : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.vanillaIngredientGroups$key.size(); $i++) {
            vanillaIngredientGroups.put(this.vanillaIngredientGroups$key.get($i),
                (List<Material>) this.vanillaIngredientGroups$value.get($i));
          }
          vanillaIngredientGroups = java.util.Collections.unmodifiableMap(vanillaIngredientGroups);
      }

      return new ShapedCraftingRecipe(name, result, recipe, permission, uniqueItemIngredients, commonItemIngredients, vanillaIngredients,
          vanillaIngredientGroups, namespacedKey, bukkitRecipe);
    }

    public String toString() {
      return "ShapedCraftingRecipe.ShapedCraftingRecipeBuilder(name=" + this.name + ", result=" + this.result + ", recipe="
          + java.util.Arrays.deepToString(this.recipe) + ", permission=" + this.permission + ", uniqueItemIngredients$key="
          + this.uniqueItemIngredients$key + ", uniqueItemIngredients$value=" + this.uniqueItemIngredients$value + ", commonItemIngredients$key="
          + this.commonItemIngredients$key + ", commonItemIngredients$value=" + this.commonItemIngredients$value + ", vanillaIngredients$key="
          + this.vanillaIngredients$key + ", vanillaIngredients$value=" + this.vanillaIngredients$value + ", vanillaIngredientGroups$key="
          + this.vanillaIngredientGroups$key + ", vanillaIngredientGroups$value=" + this.vanillaIngredientGroups$value + ", namespacedKey="
          + this.namespacedKey + ", bukkitRecipe=" + this.bukkitRecipe + ")";
    }
  }
}
