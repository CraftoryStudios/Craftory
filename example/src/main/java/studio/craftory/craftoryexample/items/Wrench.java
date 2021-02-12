package studio.craftory.craftoryexample.items;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import studio.craftory.core.items.CustomItemManager;
import studio.craftory.core.utils.Log;

public class Wrench {

  public static void onClick(Event e) {
    PlayerInteractEvent event = (PlayerInteractEvent) e;
    if(event.getHand()!= EquipmentSlot.HAND) return;
    if(CustomItemManager.isCustomItem(event.getItem())){
      event.getPlayer().sendMessage("Hello : " + CustomItemManager.getItemName(event.getItem()));
    } else  {
      Log.warn("Bad Wrench");
    }


  }

}
