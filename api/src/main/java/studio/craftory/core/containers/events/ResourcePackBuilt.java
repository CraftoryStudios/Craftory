package studio.craftory.core.containers.events;

import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResourcePackBuilt extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  public ResourcePackBuilt() {

  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  @NonNull
  public HandlerList getHandlers() {
    return HANDLERS;
  }
}
