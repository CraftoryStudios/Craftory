package studio.craftory.craftoryexample.items;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import studio.craftory.core.items.CustomItemManager;

public class Wrench {

  public static void onClick(Event e) {
    System.out.println("In Wrench Method");
    PlayerInteractEvent event = (PlayerInteractEvent) e;
    if(event.getHand()!= EquipmentSlot.HAND) return;
    System.out.println("Item: " + event.getItem());
    System.out.println(event.toString());
    System.out.println(event.getPlayer().toString());
    if(CustomItemManager.isCustomItem(event.getItem())){
      System.out.println(CustomItemManager.getItemName(event.getItem()) + " :))))");
      event.getPlayer().sendMessage("Hello : " + CustomItemManager.getItemName(event.getItem()));
    } else  {
      System.out.println(CustomItemManager.getItemName(event.getItem()) + " :(((((");
    }


  }

}
