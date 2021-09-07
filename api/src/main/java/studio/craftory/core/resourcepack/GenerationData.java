package studio.craftory.core.resourcepack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerationData {

  private int amountLeft;
  private String data;
  private String blockName;
}
