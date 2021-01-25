package studio.craftory.core.blocks.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.safecontainers.SafeBlockLocation;

public abstract class BaseCustomBlock {

  @Getter
  private SafeBlockLocation safeBlockLocation;

  @Getter
  @Setter
  private CraftoryDirection facingDirection;


  protected BaseCustomBlock(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    this.safeBlockLocation = new SafeBlockLocation(location);
    this.facingDirection = facingDirection;
  }

  public void renderCustomBlock() {

  }

}
