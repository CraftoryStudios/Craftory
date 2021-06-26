package studio.craftory.core.containers;

import java.util.List;
import lombok.Getter;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;

public class RenderData {
  @Getter
  private CraftoryRenderer renderer;
  @Getter
  private List<String> data;

  public RenderData(CraftoryRenderer renderer, List<String> data) {
    this.renderer = renderer;
    this.data = data;
  }
}
