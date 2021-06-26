package studio.craftory.core.containers.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResourcePackBuilt extends Event {

  public ResourcePackBuilt() {

  }

  private static final HandlerList HANDLERS = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
