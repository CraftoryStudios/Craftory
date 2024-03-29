package studio.craftory.core.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<X, Y> {

  private X key;
  private Y value;
}
