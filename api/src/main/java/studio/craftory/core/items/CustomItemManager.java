package studio.craftory.core.items;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.data.keys.CraftoryKey;
import studio.craftory.core.utils.StringUtils;

public class CustomItemManager {

  protected static final Map<String, ItemStack> customItemCache = new HashMap<>();

  public static final NamespacedKey ITEM_NAME_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CUSTOM_ITEM_NAME");
  public static final NamespacedKey CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CHARGE");
  public static final NamespacedKey MAX_CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "MAX_CHARGE");

  private CustomItemManager() {}

  /* Registering */
  public static void registerCustomItem(CraftoryKey itemKey, ItemStack baseStateItem) {
    String itemName = itemKey.toString();
    ItemMeta itemMeta = baseStateItem.getItemMeta();
    itemMeta.getPersistentDataContainer().set(ITEM_NAME_NAMESPACED_KEY, PersistentDataType.STRING, itemName);
    baseStateItem.setItemMeta(itemMeta);
    customItemCache.put(itemName, baseStateItem);
  }

  /* Utility Methods */
  public static boolean isCustomItem(ItemStack itemStack) {
    if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta()) {
      return false;
    }
    return itemStack.getItemMeta().getPersistentDataContainer().has(ITEM_NAME_NAMESPACED_KEY, PersistentDataType.STRING);
  }

  public static boolean isCustomItemName(String name) {
    return customItemCache.containsKey(name);
  }

  public static String getItemName(@NonNull ItemStack itemStack) {
    if (itemStack.hasItemMeta()) {
      PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
      return container.getOrDefault(ITEM_NAME_NAMESPACED_KEY, PersistentDataType.STRING, itemStack.getType().toString());
    }
    return itemStack.getType().toString();
  }

  public static ItemStack getCustomItem(String name) {
    if (customItemCache.containsKey(name)) {
      return customItemCache.get(name).clone();
    }
    return new ItemStack(Material.AIR);
  }

  public static ItemStack getCustomItemOrDefault(String name) {
    if (customItemCache.containsKey(name)) {
      return customItemCache.get(name).clone();
    }
    Optional<Material> material = Optional.ofNullable(Material.getMaterial(name));
    return material.map(ItemStack::new).orElseGet(() -> new ItemStack(Material.AIR));
  }

  /* Item Properties */
  public static void setAttackSpeed(ItemStack itemStack, int attackSpeed) {
    ItemMeta meta = itemStack.getItemMeta();
    AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed",
        attackSpeed, Operation.ADD_NUMBER, EquipmentSlot.HAND);
    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
    itemStack.setItemMeta(meta);
  }

  public static void setAttackDamage(ItemStack itemStack, int attackDamage) {
    ItemMeta meta = itemStack.getItemMeta();
    AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attack_damage",
        attackDamage, Operation.ADD_NUMBER, EquipmentSlot.HAND);
    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
    itemStack.setItemMeta(meta);
  }

  public static void setUnbreakable(ItemStack itemStack, boolean unbreakable) {
    ItemMeta meta = itemStack.getItemMeta();
    meta.setUnbreakable(unbreakable);
    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    itemStack.setItemMeta(meta);
  }

  public static void setMaxCharge(ItemStack itemStack, int maxCharge) {
    if(!itemStack.getItemMeta().getPersistentDataContainer().has(CHARGE_NAMESPACED_KEY, PersistentDataType.INTEGER)) {
      setCharge(itemStack, maxCharge);
    }
    setItemPropertyInt(itemStack, MAX_CHARGE_NAMESPACED_KEY, maxCharge);
  }

  public static void setCharge(ItemStack itemStack, int charge) {
    int maxCharge = itemStack.getItemMeta().getPersistentDataContainer().get(MAX_CHARGE_NAMESPACED_KEY, PersistentDataType.INTEGER);
    setCharge(itemStack, charge, maxCharge);
  }

  public static void setCharge(ItemStack itemStack, int charge, int maxCharge) {
    setItemPropertyInt(itemStack, CHARGE_NAMESPACED_KEY, charge);
    replaceLoreLine(itemStack, "Charge: " + StringUtils.rawEnergyToPrefixed(0) + "/" + StringUtils.rawEnergyToPrefixed(maxCharge), "Charge: ");
  }

  public static void setItemPropertyString(ItemStack itemStack, NamespacedKey key, String value) {
    itemStack.getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
  }

  public static void setItemPropertyInt(ItemStack itemStack, NamespacedKey key, int value) {
    itemStack.getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
  }

  public static void setItemPropertyDouble(ItemStack itemStack, NamespacedKey key, double value) {
    itemStack.getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
  }

  public static void setItemPropertyByte(ItemStack itemStack, NamespacedKey key, byte value) {
    itemStack.getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.BYTE, value);
  }

  /* Display Name and tool tips */
  private static ChatColor getDisplayNameColour(String displayName) {
    String displayNameChecker = displayName.toLowerCase(Locale.ROOT);
    if (displayNameChecker.contains("iron")) {
      return ChatColor.GRAY;
    } else if (displayNameChecker.contains("gold")) {
      return ChatColor.GOLD;
    } else if (displayNameChecker.contains("diamond")) {
      return ChatColor.BLUE;
    } else if (displayNameChecker.contains("emerald")) {
      return ChatColor.GREEN;
    } else {
      return ChatColor.RESET;
    }
  }

  public static void setDisplayName(@NonNull ItemStack itemStack, @NonNull String displayName) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(getDisplayNameColour(displayName) + displayName);
    itemStack.setItemMeta(itemMeta);
  }

  public static void setDisplayName(@NonNull ItemStack itemStack, @NonNull String displayName, @NonNull ChatColor color) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(color + displayName);
    itemStack.setItemMeta(itemMeta);
  }

  public static void setLore(@NonNull ItemStack itemStack, @NonNull String lore) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setLore(new ArrayList<>(Collections.singletonList(lore)));
    itemStack.setItemMeta(itemMeta);
  }

  public static void addToLore(@NonNull ItemStack itemStack, @NonNull String line) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    List<String> lore = itemMeta.getLore();
    if(lore==null) lore = new ArrayList<>();
    lore.add(line);
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
  }

  public static void replaceLoreLine(@NonNull ItemStack itemStack, @NonNull String line, @NonNull String replacementKey) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    List<String> lore = itemMeta.getLore();
    if(lore==null) lore = new ArrayList<>();
    lore.removeIf(s -> s.contains(replacementKey));
    lore.add(line);
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
  }

  /* Durability */
  public static ItemStack updateDurabilityLore(ItemStack item, int current, int max) {
    ItemMeta itemMeta = item.getItemMeta();
    List<String> lore;
    if (itemMeta.hasLore()) {
      lore = itemMeta.getLore();
    } else {
      lore = new ArrayList<>();
    }
    int line = -1;
    for (int i = 0; i < lore.size(); i++) {
      if (lore.get(i).contains("Durability")) {
        line = i;
        break;
      }
    }
    if (line == -1) {
      lore.add("");
      lore.add(ChatColor.WHITE+"Durability "+current + " / "+max);
    } else {
      lore.set(line,ChatColor.WHITE+"Durability "+current + " / "+max);
    }

    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    return item;
  }


  /* Progress bar */
  public static ItemStack updateLoreProgressBar(ItemStack item, String title, int max, int progress) {
    ItemMeta itemMeta = item.getItemMeta();
    List<String> lore;
    if (itemMeta.hasLore()) {
      lore = itemMeta.getLore();
    } else {
      lore = new ArrayList<>();
    }
    int line = -1;
    for (int i = 0; i < lore.size(); i++) {
      if (lore.get(i).toLowerCase(Locale.ROOT).startsWith(title.toLowerCase(Locale.ROOT))) {
        line = i;
        break;
      }
    }
    if (line == -1) {
      lore.add(ChatColor.YELLOW+title+": "+ChatColor.WHITE+progress/max+"%  "+getProgressBar(progress,
          max, 10, '█', ChatColor.GREEN, ChatColor.RED));
    } else {
      lore.set(line,
          ChatColor.YELLOW+title+": "+ChatColor.WHITE+progress/max+"%  "+getProgressBar(progress,
              max, 10, '█', ChatColor.GREEN, ChatColor.RED));
    }

    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    return item;
  }

  private static String getProgressBar(int current, int max, int totalBars, char symbol,
      ChatColor completedColor,
      ChatColor notCompletedColor) {
    float percent = (float) current / max;
    int progressBars = (int) (totalBars * percent);

    return Strings.repeat("" + completedColor + symbol, progressBars)
        + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
  }
}
