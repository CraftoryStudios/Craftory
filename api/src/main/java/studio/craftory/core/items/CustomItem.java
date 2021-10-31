package studio.craftory.core.items;

import static studio.craftory.core.utils.Constants.Keys.ITEM_NAME_NAMESPACED_KEY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import studio.craftory.core.Craftory;
import studio.craftory.core.containers.keys.ItemDataKey;

public class CustomItem {

  private final String name;
  private final String displayName;
  private final Material material;
  private final Integer attackSpeed;
  private final Integer attackDamage;
  private boolean unbreakable = true;
  private final Set<PotionEffect> holdEffects;
  private ChatColor displayNameColour = ChatColor.WHITE;
  private final Map<Class<?>, Consumer<Event>> handlers;
  private final Map<ItemDataKey, Object> attributes;
  private final String modelPath;
  private String plugin;
  private ItemStack itemStack;
  private int renderID;

  CustomItem(String name, String displayName, Material material, Integer attackSpeed, Integer attackDamage, boolean unbreakable,
      Set<PotionEffect> holdEffects, ChatColor displayNameColour, Map<Class<?>, Consumer<Event>> handlers, Map<ItemDataKey, Object> attributes,
      String modelPath, String plugin, ItemStack itemStack, int renderID) {
    this.name = name;
    this.displayName = displayName;
    this.material = material;
    this.attackSpeed = attackSpeed;
    this.attackDamage = attackDamage;
    this.unbreakable = unbreakable;
    this.holdEffects = holdEffects;
    this.displayNameColour = displayNameColour;
    this.handlers = handlers;
    this.attributes = attributes;
    this.modelPath = modelPath;
    this.plugin = plugin;
    this.itemStack = itemStack;
    this.renderID = renderID;
  }

  private static boolean $default$unbreakable() {return true;}

  private static ChatColor $default$displayNameColour() {return ChatColor.WHITE;}

  public static CustomItemBuilder builder() {return new CustomItemBuilder();}

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
    if (plugin == null || name == null || material == null || displayName == null || modelPath == null) {
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

  public void register(Plugin plugin) {
    this.plugin = plugin.getName().toLowerCase(Locale.ROOT);
    Craftory.getCustomItemManager().registerCustomItem(this);
  }

  public String getName() {return this.name;}

  public Material getMaterial() {return this.material;}

  public Set<PotionEffect> getHoldEffects() {return this.holdEffects;}

  public Map<Class<?>, Consumer<Event>> getHandlers() {return this.handlers;}

  public String getModelPath() {return this.modelPath;}

  public CustomItemBuilder toBuilder() {
    return new CustomItemBuilder().name(this.name).displayName(this.displayName).material(this.material).attackSpeed(this.attackSpeed)
                                  .attackDamage(this.attackDamage).unbreakable(this.unbreakable).holdEffects(
            this.holdEffects == null ? java.util.Collections.emptySet() : this.holdEffects).displayNameColour(this.displayNameColour).handlers(
            this.handlers == null ? java.util.Collections.emptyMap() : this.handlers).attributes(
            this.attributes == null ? java.util.Collections.emptyMap() : this.attributes).modelPath(this.modelPath).plugin(this.plugin)
                                  .itemStack(this.itemStack).renderID(this.renderID);
  }

  public static class CustomItemBuilder {

    private String name;
    private String displayName;
    private Material material;
    private Integer attackSpeed;
    private Integer attackDamage;
    private boolean unbreakable$value;
    private boolean unbreakable$set;
    private ArrayList<PotionEffect> holdEffects;
    private ChatColor displayNameColour$value;
    private boolean displayNameColour$set;
    private ArrayList<Class<?>> handlers$key;
    private ArrayList<Consumer<Event>> handlers$value;
    private ArrayList<ItemDataKey> attributes$key;
    private ArrayList<Object> attributes$value;
    private String modelPath;
    private String plugin;
    private ItemStack itemStack;
    private int renderID;

    CustomItemBuilder() {}

    public CustomItemBuilder name(String name) {
      this.name = name;
      return this;
    }

    public CustomItemBuilder displayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public CustomItemBuilder material(Material material) {
      this.material = material;
      return this;
    }

    public CustomItemBuilder attackSpeed(Integer attackSpeed) {
      this.attackSpeed = attackSpeed;
      return this;
    }

    public CustomItemBuilder attackDamage(Integer attackDamage) {
      this.attackDamage = attackDamage;
      return this;
    }

    public CustomItemBuilder unbreakable(boolean unbreakable) {
      this.unbreakable$value = unbreakable;
      this.unbreakable$set = true;
      return this;
    }

    public CustomItemBuilder holdEffect(PotionEffect holdEffect) {
      if (this.holdEffects == null) {
        this.holdEffects = new ArrayList<PotionEffect>();
      }
      this.holdEffects.add(holdEffect);
      return this;
    }

    public CustomItemBuilder holdEffects(Collection<? extends PotionEffect> holdEffects) {
      if (this.holdEffects == null) {
        this.holdEffects = new ArrayList<PotionEffect>();
      }
      this.holdEffects.addAll(holdEffects);
      return this;
    }

    public CustomItemBuilder clearHoldEffects() {
      if (this.holdEffects != null) {
        this.holdEffects.clear();
      }
      return this;
    }

    public CustomItemBuilder displayNameColour(ChatColor displayNameColour) {
      this.displayNameColour$value = displayNameColour;
      this.displayNameColour$set = true;
      return this;
    }

    public CustomItemBuilder handler(Class<?> handlerKey, Consumer<Event> handlerValue) {
      if (this.handlers$key == null) {
        this.handlers$key = new ArrayList<Class<?>>();
        this.handlers$value = new ArrayList<Consumer<Event>>();
      }
      this.handlers$key.add(handlerKey);
      this.handlers$value.add(handlerValue);
      return this;
    }

    public CustomItemBuilder handlers(Map<? extends Class<?>, ? extends Consumer<Event>> handlers) {
      if (this.handlers$key == null) {
        this.handlers$key = new ArrayList<Class<?>>();
        this.handlers$value = new ArrayList<Consumer<Event>>();
      }
      for (final Map.Entry<? extends Class<?>, ? extends Consumer<Event>> $lombokEntry : handlers.entrySet()) {
        this.handlers$key.add($lombokEntry.getKey());
        this.handlers$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public CustomItemBuilder clearHandlers() {
      if (this.handlers$key != null) {
        this.handlers$key.clear();
        this.handlers$value.clear();
      }
      return this;
    }

    public CustomItemBuilder attribute(ItemDataKey attributeKey, Object attributeValue) {
      if (this.attributes$key == null) {
        this.attributes$key = new ArrayList<ItemDataKey>();
        this.attributes$value = new ArrayList<Object>();
      }
      this.attributes$key.add(attributeKey);
      this.attributes$value.add(attributeValue);
      return this;
    }

    public CustomItemBuilder attributes(Map<? extends ItemDataKey, ?> attributes) {
      if (this.attributes$key == null) {
        this.attributes$key = new ArrayList<ItemDataKey>();
        this.attributes$value = new ArrayList<Object>();
      }
      for (final Map.Entry<? extends ItemDataKey, ?> $lombokEntry : attributes.entrySet()) {
        this.attributes$key.add($lombokEntry.getKey());
        this.attributes$value.add($lombokEntry.getValue());
      }
      return this;
    }

    public CustomItemBuilder clearAttributes() {
      if (this.attributes$key != null) {
        this.attributes$key.clear();
        this.attributes$value.clear();
      }
      return this;
    }

    public CustomItemBuilder modelPath(String modelPath) {
      this.modelPath = modelPath;
      return this;
    }

    public CustomItemBuilder plugin(String plugin) {
      this.plugin = plugin;
      return this;
    }

    public CustomItemBuilder itemStack(ItemStack itemStack) {
      this.itemStack = itemStack;
      return this;
    }

    public CustomItemBuilder renderID(int renderID) {
      this.renderID = renderID;
      return this;
    }

    public CustomItem build() {
      boolean unbreakable$value = this.unbreakable$value;
      if (!this.unbreakable$set) {
        unbreakable$value = CustomItem.$default$unbreakable();
      }
      Set<PotionEffect> holdEffects;
      switch (this.holdEffects == null ? 0 : this.holdEffects.size()) {
        case 0:
          holdEffects = java.util.Collections.emptySet();
          break;
        case 1:
          holdEffects = java.util.Collections.singleton(this.holdEffects.get(0));
          break;
        default:
          holdEffects = new java.util.LinkedHashSet<PotionEffect>(
              this.holdEffects.size() < 1073741824 ? 1 + this.holdEffects.size() + (this.holdEffects.size() - 3) / 3 : Integer.MAX_VALUE);
          holdEffects.addAll(this.holdEffects);
          holdEffects = java.util.Collections.unmodifiableSet(holdEffects);
      }
      ChatColor displayNameColour$value = this.displayNameColour$value;
      if (!this.displayNameColour$set) {
        displayNameColour$value = CustomItem.$default$displayNameColour();
      }
      Map<Class<?>, Consumer<Event>> handlers;
      switch (this.handlers$key == null ? 0 : this.handlers$key.size()) {
        case 0:
          handlers = java.util.Collections.emptyMap();
          break;
        case 1:
          handlers = java.util.Collections.singletonMap(this.handlers$key.get(0), this.handlers$value.get(0));
          break;
        default:
          handlers = new java.util.LinkedHashMap<Class<?>, Consumer<Event>>(
              this.handlers$key.size() < 1073741824 ? 1 + this.handlers$key.size() + (this.handlers$key.size() - 3) / 3 : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.handlers$key.size(); $i++) {
            handlers.put(this.handlers$key.get($i), (Consumer<Event>) this.handlers$value.get($i));
          }
          handlers = java.util.Collections.unmodifiableMap(handlers);
      }
      Map<ItemDataKey, Object> attributes;
      switch (this.attributes$key == null ? 0 : this.attributes$key.size()) {
        case 0:
          attributes = java.util.Collections.emptyMap();
          break;
        case 1:
          attributes = java.util.Collections.singletonMap(this.attributes$key.get(0), this.attributes$value.get(0));
          break;
        default:
          attributes = new java.util.LinkedHashMap<ItemDataKey, Object>(
              this.attributes$key.size() < 1073741824 ? 1 + this.attributes$key.size() + (this.attributes$key.size() - 3) / 3 : Integer.MAX_VALUE);
          for (int $i = 0; $i < this.attributes$key.size(); $i++) {
            attributes.put(this.attributes$key.get($i), (Object) this.attributes$value.get($i));
          }
          attributes = java.util.Collections.unmodifiableMap(attributes);
      }

      return new CustomItem(name, displayName, material, attackSpeed, attackDamage, unbreakable$value, holdEffects, displayNameColour$value, handlers,
          attributes, modelPath, plugin, itemStack, renderID);
    }

    public String toString() {
      return "CustomItem.CustomItemBuilder(name=" + this.name + ", displayName=" + this.displayName + ", material=" + this.material + ", attackSpeed="
          + this.attackSpeed + ", attackDamage=" + this.attackDamage + ", unbreakable$value=" + this.unbreakable$value + ", holdEffects="
          + this.holdEffects + ", displayNameColour$value=" + this.displayNameColour$value + ", handlers$key=" + this.handlers$key
          + ", handlers$value="
          + this.handlers$value + ", attributes$key=" + this.attributes$key + ", attributes$value=" + this.attributes$value + ", modelPath="
          + this.modelPath + ", plugin=" + this.plugin + ", itemStack=" + this.itemStack + ", renderID=" + this.renderID + ")";
    }
  }
}
