package studio.craftory.core.data;

import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntRange {
  private int min;
  private int max;

  public int getRandomInRange(Random random) {
    return random.nextInt((max - min) + 1) + min;
  }
}
