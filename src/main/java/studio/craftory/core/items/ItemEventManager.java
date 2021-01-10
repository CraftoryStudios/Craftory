package studio.craftory.core.items;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Map;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class ItemEventManager implements Listener {

  private static final Map<String, Set<Method>> dumbEvents = new HashMap<>();
  private static final Map<ItemSmartEvent, Map<String, Method>> smartEvents = new EnumMap<>(ItemSmartEvent.class);
  private static final Map<String, Collection<PotionEffect>> itemOnHoldEffects = new HashMap<>();

  public static void registerDumbEvent(Event event, Method method) {
    String name = event.getEventName();
    Set<Method> temp = dumbEvents.getOrDefault(name,new HashSet<>());
    temp.add(method);
    dumbEvents.put(name, temp);
  }

  public static boolean registerSmartEvent(Event event, String triggerItemName, Method method) {
    Class<?> eventClass = event.getClass();
    if (ItemSmartEvent.isValid(eventClass)) {
      ItemSmartEvent itemSmartEvent = ItemSmartEvent.fromClass(eventClass);
      Map<String, Method> temp = smartEvents.getOrDefault(itemSmartEvent, new HashMap<>());
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
  public void onEvent(Event event) {
    handleDumbEvents(event);
    handleSmartEvent(event);
  }

  /**
   * Dumb handler leaves validation to the method
   * @param event The event
   */
  private void handleDumbEvents(Event event) {
    String name = event.getEventName();
    if (dumbEvents.containsKey(name)) {
      for (Method method: dumbEvents.get(name)) {
        try {
          method.invoke(event);
        } catch (IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
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
              smartEvents.get(itemSmartEvent).get(triggerItemName).invoke(eventClass.cast(event));
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
    addPotionEffects(event.getPlayer().getInventory().getItemInMainHand().getType().toString(),
        (Player) event.getPlayer());
  }

  @EventHandler
  public void onPlayerItemHeld(PlayerItemHeldEvent event) {
    // Replace with custom item name
    removePotionEffects(event.getPlayer().getInventory().getItem(event.getPreviousSlot()).getType().toString(), event.getPlayer());
    addPotionEffects(event.getPlayer().getInventory().getItem(event.getNewSlot()).getType().toString(), event.getPlayer());
  }

  @EventHandler
  public void onInventoryInteract(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    PlayerInventory inventory = player.getInventory();
    if(event.isShiftClick()) {
      if(player.getInventory().getHeldItemSlot()==event.getSlot()){
        // Replace with custom item name
        removePotionEffects(inventory.getItemInMainHand().getType().toString() ,player);
      }
      return;
    }
    if(event.getHotbarButton()!=-1){
      if(player.getInventory().getHeldItemSlot()==event.getHotbarButton()) { //Moving item in
        // Replace with custom item name
        removePotionEffects(player.getInventory().getItemInMainHand().getType().toString(), player);
      }
    } else {
      if(player.getInventory().getHeldItemSlot()==event.getSlot()) { //Moving item in
        // Replace with custom item name
        removePotionEffects(player.getInventory().getItem(event.getSlot()).getType().toString(), player);
      }
    }
  }

  @EventHandler
  public void onPlayerItemDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    // Replace with custom item name
    removePotionEffects(event.getItemDrop().getItemStack().getType().toString(), player);
    addPotionEffects(player.getInventory().getItemInMainHand().getType().toString(), player);
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
      addPotionEffects(event.getItem().getItemStack().getType().toString() , player);
    }
  }

  private void addPotionEffects(String itemName, Player player) {
    if(itemOnHoldEffects.containsKey(itemName)) {
      player.addPotionEffects(itemOnHoldEffects.get(itemName));
    }
  }

  private void removePotionEffects(String itemName, Player player) {
    if(itemOnHoldEffects.containsKey(itemName)) {
      for(PotionEffect effect: itemOnHoldEffects.get(itemName)) {
        player.removePotionEffect(effect.getType());
      }
    }
  }

}
