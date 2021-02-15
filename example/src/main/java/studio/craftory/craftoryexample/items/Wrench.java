package studio.craftory.craftoryexample.items;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import studio.craftory.core.api.CustomItemAPI;

public class Wrench {

  private Wrench() {}

  public static void onClick(Event e) {
    PlayerInteractEvent event = (PlayerInteractEvent) e;
    if(event.getHand()!= EquipmentSlot.HAND) return;
    if(CustomItemAPI.isCustomItem(event.getItem()) && CustomItemAPI.matchCustomItemName(event.getItem(), "craftoryexample:wrench")){
      event.getPlayer().sendMessage("Hello : " + CustomItemAPI.getItemName(event.getItem()));
    }


  }

}
