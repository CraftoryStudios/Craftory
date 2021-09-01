package studio.craftory.core.containers.events;

import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResourcePackBuilt extends Event {

  public ResourcePackBuilt() {

  }

  private static final HandlerList HANDLERS = new HandlerList();

  @Override
  @NonNull
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
