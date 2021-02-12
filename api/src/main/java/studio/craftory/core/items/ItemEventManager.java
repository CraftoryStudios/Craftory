package studio.craftory.core.items;

<<<<<<<
import java.lang.reflect.Method;
=======

>>>>>>>
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class ItemEventManager implements Listener {

  private static final Map<String, Set<Consumer<Event>>> dumbEvents = new HashMap<>();
  private static final Map<ItemSmartEvent, Map<String, Consumer<Event>>> smartEvents = new EnumMap<>(ItemSmartEvent.class);
  private static final Map<String, Collection<PotionEffect>> itemOnHoldEffects = new HashMap<>();

  public static void registerDumbEvent(Class event, Consumer<Event> method) {
    Set<Consumer<Event>> temp = dumbEvents.getOrDefault(event.getSimpleName(),new HashSet<>());
    temp.add(method);
    dumbEvents.put(event.getSimpleName(), temp);
  }

  public static boolean registerSmartEvent(Class event, String triggerItemName, Consumer<Event> method) {
    if (ItemSmartEvent.isValid(event)) {
      ItemSmartEvent itemSmartEvent = ItemSmartEvent.fromClass(event);
      Map<String, Consumer<Event>> temp = smartEvents.getOrDefault(itemSmartEvent, new HashMap<>());
      temp.put(triggerItemName, method);
      smartEvents.put(itemSmartEvent, temp);
      return true;
    }
    return false;
  }

  public static void registerItemOnHoldEffects(String itemName, Collection<PotionEffect> effects) {
    itemOnHoldEffects.put(itemName, effects);
  }


  @EventHandler
  public void onRightClickTest(PlayerInteractEvent event) {
    if(event.getHand().equals(EquipmentSlot.HAND) && event.getAction()== Action.RIGHT_CLICK_BLOCK && event.getItem()==null) {
      event.getPlayer().getInventory().setItemInMainHand(CustomItemManager.getCustomItem("example:drill"));
    }
    handleDumbEvents(event);
  }

  /**
   * Dumb handler leaves validation to the method
   * @param event The event
   */
  private void handleDumbEvents(Event event) {
    String name = event.getEventName();
    if (dumbEvents.containsKey(name)) {
      for (Consumer<Event> method: dumbEvents.get(name)) {
        method.accept(event);
      }
    }
  }

  // Does event.getClass return the highest level class? I hope it does
  private void handleSmartEvent(Event event) {
    Class<?> eventClass = event.getClass();
    if (ItemSmartEvent.isValid(eventClass)) {
      ItemSmartEvent itemSmartEvent = ItemSmartEvent.fromClass(eventClass);
      if(smartEvents.containsKey(itemSmartEvent)) {
        // Some how validate here and get a string to map to the right methods
        // e.g. Get the name of the custom item in player hand
        String triggerItemName = itemSmartEvent.getValidationString(event);
        if(smartEvents.get(itemSmartEvent).containsKey(triggerItemName)){
            try {
              smartEvents.get(itemSmartEvent).get(triggerItemName).accept(event);
            } catch (Exception e) {
              e.printStackTrace();
            }
        }
      }
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    // Replace with custom item name
    addPotionEffects(event.getPlayer().getInventory().getItemInMainHand(),
        (Player) event.getPlayer());
  }

  @EventHandler
  public void onPlayerItemHeld(PlayerItemHeldEvent event) {
    ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
    ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
    if(oldItem!=null) {
      removePotionEffects(oldItem, event.getPlayer());
    }
    if(newItem!=null) {
      addPotionEffects(newItem, event.getPlayer());
    }
  }

  @EventHandler
  public void onInventoryInteract(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    PlayerInventory inventory = player.getInventory();
    if(event.isShiftClick()) {
      if(player.getInventory().getHeldItemSlot()==event.getSlot()){
        removePotionEffects(inventory.getItemInMainHand() ,player);
      }
      return;
    }
    if(event.getHotbarButton()!=-1){
      if(player.getInventory().getHeldItemSlot()==event.getHotbarButton()) { //Moving item in
        removePotionEffects(player.getInventory().getItemInMainHand(), player);
      }
    } else {
      if(player.getInventory().getHeldItemSlot()==event.getSlot()) { //Moving item in
        removePotionEffects(player.getInventory().getItem(event.getSlot()), player);
      }
    }
  }

  @EventHandler
  public void onPlayerItemDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    // Replace with custom item name
    removePotionEffects(event.getItemDrop().getItemStack(), player);
    addPotionEffects(player.getInventory().getItemInMainHand(), player);
  }

  @EventHandler
  public void onPlayerPickupItem(EntityPickupItemEvent event) {
    if(!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    PlayerInventory inventory = player.getInventory();
    if(!inventory.getItemInMainHand().getType().equals(Material.AIR)) return;
    boolean onlyHeldFree = true;
    for (int i = 0; i < player.getInventory().getHeldItemSlot(); i++) {
      if(inventory.getItem(i)==null || inventory.getItem(i).getType()==Material.AIR){
        onlyHeldFree = false;
        break;
      }
    }
    if(onlyHeldFree) {
      // Replace with custom item name
      addPotionEffects(event.getItem().getItemStack(), player);
    }
  }

  private void addPotionEffects(ItemStack item, Player player) {
    if(item==null) return;
    String itemName = CustomItemManager.getItemName(item);
    if(itemOnHoldEffects.containsKey(itemName)) {
      player.addPotionEffects(itemOnHoldEffects.get(itemName));
    }
  }

  private void removePotionEffects(ItemStack item, Player player) {
    if(item==null) return;
    String itemName = CustomItemManager.getItemName(item);
    if(itemOnHoldEffects.containsKey(itemName)) {
      for(PotionEffect effect: itemOnHoldEffects.get(itemName)) {
        player.removePotionEffect(effect.getType());
      }
    }
  }

}
