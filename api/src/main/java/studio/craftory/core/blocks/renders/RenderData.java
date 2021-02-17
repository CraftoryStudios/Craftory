package studio.craftory.core.blocks.renders;

import java.util.List;
import lombok.Getter;

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
