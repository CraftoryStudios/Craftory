package studio.craftory.core.blocks.renders;

import java.util.ArrayList;
import lombok.Data;
import lombok.Getter;

public class RenderData {
  @Getter
  private CraftoryRenderer renderer;
  @Getter
  private ArrayList<String> data;

  public RenderData(CraftoryRenderer renderer, ArrayList<String> data) {
    this.renderer = renderer;
    this.data = data;
  }
}
