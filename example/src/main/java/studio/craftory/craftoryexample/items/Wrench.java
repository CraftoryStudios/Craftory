package studio.craftory.craftoryexample.items;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import studio.craftory.core.items.CustomItemUtils;

public class Wrench {

  private Wrench() {}

  public static void onClick(Event e) {
    PlayerInteractEvent event = (PlayerInteractEvent) e;
    if(event.getHand()!= EquipmentSlot.HAND || event.getItem()==null) return;
    if(CustomItemUtils.isCustomItem(event.getItem()) && CustomItemUtils.matchCustomItemName(event.getItem(), "craftoryexample:wrench")){
      event.getPlayer().sendMessage("Hello : " + CustomItemUtils.getItemName(event.getItem()));
    }


  }

}
