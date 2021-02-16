package studio.craftory.core.items;

import static studio.craftory.core.items.CustomItemManager.ITEM_NAME_NAMESPACED_KEY;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import studio.craftory.core.Craftory;
import studio.craftory.core.data.keys.ItemDataKey;

@Builder(toBuilder = true)
public class CustomItem {

  private String plugin;
  private final String name;
  private final String displayName;
  private final Material material;
  private ItemStack itemStack;
  private final Integer attackSpeed;
  private final Integer attackDamage;
  private int renderID;
  @Builder.Default
  private final boolean unbreakable = true;

  @Getter
  @Singular private final Set<PotionEffect> holdEffects;

  @Builder.Default
  private final ChatColor displayNameColour = ChatColor.WHITE;

  @Getter
  @Singular private final Map<Class<?>, Consumer<Event>> handlers;

  @Singular private final Map<ItemDataKey, Object> attributes;

  public boolean hasHoldEffects() {
    return !holdEffects.isEmpty();
  }

  public boolean hasHandlers() {
    return !handlers.isEmpty();
  }

  public String getUniqueName() {
    return plugin + ":" + name;
  }

  public ItemStack getItem() {
    return itemStack.clone();
  }

  public void createItem(int renderID) {
    if (plugin==null || name==null || material==null || displayName==null) {
      throw new IllegalArgumentException("Attempted to register Custom item that was missing either: plugin, name, material or display-name");
    }
    this.renderID = renderID;

    // Create ItemStack and containers
    ItemStack item = new ItemStack(material);
    CustomItemUtils.validateItemStackMeta(item);
    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();

    // Set required fields
    persistentDataContainer.set(ITEM_NAME_NAMESPACED_KEY, PersistentDataType.STRING, getUniqueName());

    meta.setDisplayName(displayNameColour + displayName);
    meta.setUnbreakable(unbreakable);
    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    meta.setCustomModelData(renderID);

    // Set optional fields
    if (attackSpeed != null) {
      AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed",
          attackSpeed, Operation.ADD_NUMBER, EquipmentSlot.HAND);
      meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
    }

    if (attackDamage != null) {
      AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attack_damage",
          attackDamage, Operation.ADD_NUMBER, EquipmentSlot.HAND);
      meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
    }

    attributes.forEach((key, value) -> persistentDataContainer.set(key.getNamespacedKey(), key.getDataType(), value));

    // Finalise item
    item.setItemMeta(meta);
    itemStack = item;
  }

  public void register(JavaPlugin plugin) {
    this.plugin = plugin.getName().toLowerCase(Locale.ROOT);
    Craftory.getCustomItemManager().registerCustomItem(this);
  }
}
