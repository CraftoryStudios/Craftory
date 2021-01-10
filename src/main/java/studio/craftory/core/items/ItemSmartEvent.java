package studio.craftory.core.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public enum ItemSmartEvent {

  PLAYERINTERACTEVENT(PlayerInteractEvent.class),
  PLAYERINTERACTENTITYEVENT(PlayerInteractEntityEvent.class),
  BLOCKBREAKEVENT(BlockBreakEvent.class);

  private final Class<?> eventClass;

  private static final Map<Class<?>, ItemSmartEvent> valueMap = new HashMap<>();

  static {
    for (ItemSmartEvent s:  values()) {
      valueMap.put(s.eventClass, s);
    }
  }

  ItemSmartEvent(Class<?> clazz) {
    eventClass = clazz;
  }

  Class<?> getEventClass() { return eventClass; }

  String getValidationString(Event event) {
    if (!eventClass.equals(event.getClass())) {
      throw new IllegalArgumentException("Event class must match the eventClass of SmartEvent");
    }
    switch (this) {
      case PLAYERINTERACTEVENT:
        PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
        if(playerInteractEvent.getItem()!=null) return playerInteractEvent.getItem().getType().toString(); // Replace with custom item name
        break;
      case PLAYERINTERACTENTITYEVENT:
        PlayerInteractEntityEvent playerInteractEntityEvent = (PlayerInteractEntityEvent) event;
        return playerInteractEntityEvent.getPlayer().getInventory().getItem(playerInteractEntityEvent.getHand()).getType().toString(); // Replace with custom item name
      case BLOCKBREAKEVENT:
        BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
        return blockBreakEvent.getPlayer().getInventory().getItemInMainHand().getType().toString(); // Replace with custom item name
    }
    return "";
  }

  public static boolean isValid(Class<?> clazz) {
    return Arrays.stream(values()).anyMatch(e -> e.eventClass.equals(clazz));
  }

  public static ItemSmartEvent fromClass(Class<?> clazz) {
    return valueMap.get(clazz);
  }
}
