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

  private static final Map<Class<?>, ItemSmartEvent> valueMap = new HashMap<>();

  static {
    for (ItemSmartEvent s : values()) {
      valueMap.put(s.eventClass, s);
    }
  }

  private final Class<?> eventClass;

  ItemSmartEvent(Class<?> clazz) {
    eventClass = clazz;
  }

  public static boolean isValid(Class<?> clazz) {
    return Arrays.stream(values()).anyMatch(e -> e.eventClass.equals(clazz));
  }

  public static ItemSmartEvent fromClass(Class<?> clazz) {
    return valueMap.get(clazz);
  }

  Class<?> getEventClass() {return eventClass;}

  String getValidationString(Event event) {
    if (!eventClass.equals(event.getClass())) {
      throw new IllegalArgumentException("Event class must match the eventClass of SmartEvent");
    }
    switch (this) {
      case PLAYERINTERACTEVENT:
        PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
        if (playerInteractEvent.getItem() != null) {
          return CustomItemUtils.getItemName(playerInteractEvent.getItem());
        }
        break;
      case PLAYERINTERACTENTITYEVENT:
        PlayerInteractEntityEvent playerInteractEntityEvent = (PlayerInteractEntityEvent) event;
        return CustomItemUtils.getItemName(playerInteractEntityEvent.getPlayer().getInventory().getItem(playerInteractEntityEvent.getHand()));
      case BLOCKBREAKEVENT:
        BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
        return CustomItemUtils.getItemName(blockBreakEvent.getPlayer().getInventory().getItemInMainHand());
    }
    return "";
  }
}
