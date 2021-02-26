package studio.craftory.core.resourcepack;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockAssetGenerator {

  private ArrayList<GenerationData> blocksToUse = new ArrayList<>();

  public String generateBlockState() {
    GenerationData generationData = blocksToUse.get(0);
    switch (generationData.blockName) {
      case "stem":
        return generateMushroomState(generationData, "s");
      case "brown":
        return generateMushroomState(generationData, "b");
      case "red":
        return generateMushroomState(generationData, "r");
      case "noteblock":
        return generateNoteBlockState(generationData);
    }
    return "";
  }

  private String generateNoteBlockState(GenerationData generationData) {
    String[] data = generationData.data.split(",");
    char instrument = data[0].charAt(0);
    int note = Integer.parseInt(data[1]);
    String powered = data[2];

    if (instrument == 'o' && note < 24) {
      instrument = 'a';
      note += 1;
    } else if (instrument == 'o' && note == 24) {
      instrument = 'a';
      note = 0;
      powered = "F";
    }

    String blockData = "" + instrument + note + powered;

    blocksToUse.remove(0);
    if (generationData.amountLeft != 0) {
      generationData.amountLeft -= 1;
      generationData.data = blockData;
      blocksToUse.add(0, generationData);
    }

    return blockData;
  }

  private String generateMushroomState(GenerationData generationData, String block) {
    StringBuilder result = new StringBuilder();
    result.append(block);
    for (int i = 0; i < 6; i++) {
      result.append((Integer.parseInt(generationData.data) & 0x1 << i) != 0 ? "T" : "F");
    }

    blocksToUse.remove(0);
    if (generationData.amountLeft != 0) {
      generationData.amountLeft -= 1;
      generationData.data = String.valueOf(Integer.parseInt(generationData.data) - 1);
      blocksToUse.add(0, generationData);
    }

    return result.toString();
  }

  public void addRenderData(String blockKey, ArrayList<String> renderData) {
  }
}
