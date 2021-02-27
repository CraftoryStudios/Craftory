package studio.craftory.core.resourcepack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
@AllArgsConstructor
public class GenerationData {
  private int amountLeft;
  private String data;
  private String blockName;
}
