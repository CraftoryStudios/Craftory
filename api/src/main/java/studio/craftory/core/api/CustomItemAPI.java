package studio.craftory.core.api;

import java.util.Optional;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import studio.craftory.core.Craftory;
import studio.craftory.core.items.CustomItemUtils;

@UtilityClass
public class CustomItemAPI {

  public static final NamespacedKey CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CHARGE");
  public static final NamespacedKey MAX_CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "MAX_CHARGE");

  /* Utility Methods */
  public static boolean isCustomItem(@NonNull ItemStack itemStack) {
    return CustomItemUtils.isCustomItem(itemStack);
  }

  public static boolean matchCustomItemName(@NonNull ItemStack itemStack, @NonNull String name) {
    return CustomItemUtils.matchCustomItemName(itemStack, name);
  }

  public static boolean isCustomItemName(@NonNull String name) {
    return CustomItemUtils.isCustomItemName(name);
  }

  public static String buildItemName(@NonNull JavaPlugin plugin, @NonNull String itemName) {
    return CustomItemUtils.buildItemName(plugin, itemName);
  }

  public static String buildItemName(@NonNull String plugin, @NonNull String itemName) {
    return CustomItemUtils.buildItemName(plugin, itemName);
  }

  public static String getItemName(@NonNull ItemStack itemStack) {
    return CustomItemUtils.getItemName(itemStack);
  }

  public static Optional<ItemStack> getCustomItem(@NonNull String name) {
    return CustomItemUtils.getCustomItem(name);
  }

  public static Optional<ItemStack> getCustomItemOrDefault(@NonNull String name) {
    return CustomItemUtils.getCustomItemOrDefault(name);
  }

  /* Item Properties */
  public static void setAttackSpeed(@NonNull ItemStack itemStack, int attackSpeed) {
    CustomItemUtils.setAttackSpeed(itemStack, attackSpeed);
  }

  public static void setAttackDamage(@NonNull ItemStack itemStack, int attackDamage) {
    CustomItemUtils.setAttackDamage(itemStack, attackDamage);
  }

  public static void setUnbreakable(@NonNull ItemStack itemStack, boolean unbreakable) {
    CustomItemUtils.setUnbreakable(itemStack, unbreakable);
  }

  public static void setMaxCharge(@NonNull ItemStack itemStack, int maxCharge) {
    CustomItemUtils.setMaxCharge(itemStack, maxCharge);
  }

  public static void setCharge(@NonNull ItemStack itemStack, int charge) {
    CustomItemUtils.setCharge(itemStack, charge);
  }

  public static void setCharge(@NonNull ItemStack itemStack, int charge, int maxCharge) {
    CustomItemUtils.setCharge(itemStack, charge, maxCharge);
  }

  public static void setItemPropertyString(@NonNull ItemStack itemStack, @NonNull NamespacedKey key, @NonNull String value) {
    CustomItemUtils.setItemPropertyString(itemStack, key, value);
  }

  public static void setItemPropertyInt(@NonNull ItemStack itemStack, @NonNull NamespacedKey key, @NonNull Integer value) {
    CustomItemUtils.setItemPropertyInt(itemStack, key, value);
  }

  public static void setItemPropertyDouble(@NonNull ItemStack itemStack, @NonNull NamespacedKey key,@NonNull Double value) {
    CustomItemUtils.setItemPropertyDouble(itemStack, key, value);
  }

  public static void setItemPropertyByte(@NonNull ItemStack itemStack, @NonNull NamespacedKey key, @NonNull Byte value) {
    CustomItemUtils.setItemPropertyByte(itemStack, key, value);
  }

  /* Display Name and tool tips */
  public static void setDisplayName(@NonNull ItemStack itemStack, @NonNull String displayName) {
    CustomItemUtils.setDisplayName(itemStack, displayName);
  }

  public static void setDisplayName(@NonNull ItemStack itemStack, @NonNull String displayName, @NonNull ChatColor color) {
    CustomItemUtils.setDisplayName(itemStack, displayName, color);
  }

  public static void setLore(@NonNull ItemStack itemStack, @NonNull String lore) {
    CustomItemUtils.setLore(itemStack, lore);
  }

  public static void addToLore(@NonNull ItemStack itemStack, @NonNull String line) {
    CustomItemUtils.addToLore(itemStack, line);
  }

  public static void replaceLoreLine(@NonNull ItemStack itemStack, @NonNull String line, @NonNull String replacementKey) {
    CustomItemUtils.replaceLoreLine(itemStack, line, replacementKey);
  }

  /* Durability */
  public static ItemStack updateDurabilityLore(@NonNull ItemStack item, int current, int max) {
    return CustomItemUtils.updateDurabilityLore(item, current, max);
  }

  /* Progress bar */
  public static ItemStack updateLoreProgressBar(@NonNull ItemStack item,@NonNull String title, int max, int progress) {
    return CustomItemUtils.updateLoreProgressBar(item, title, max, progress);
  }

}
