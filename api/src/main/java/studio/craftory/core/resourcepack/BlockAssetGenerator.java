package studio.craftory.core.resourcepack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import studio.craftory.core.Craftory;
import studio.craftory.core.utils.Log;

public class BlockAssetGenerator {

  private ArrayList<GenerationData> blocksToUse = new ArrayList<>();
  private final ArrayList<String> resourcePacks = new ArrayList<>(Arrays.asList("low", "normal", "high"));
  ObjectMapper mapper = new ObjectMapper();

  public BlockAssetGenerator() {
    //Should be read from config file and generated
    blocksToUse.add(new GenerationData(750, "a,0,T","note_block"));
    blocksToUse.add(new GenerationData(64, "0", "mushroom_stem"));
    blocksToUse.add(new GenerationData(64, "0", "brown_mushroom_block"));
    blocksToUse.add(new GenerationData(64, "0", "red_mushroom_block"));
  }

  public String generateBlockState() {
    GenerationData generationData = blocksToUse.get(0);
    switch (generationData.getBlockName()) {
      case "mushroom_stem":
        return generateMushroomState(generationData, "s");
      case "brown_mushroom_block":
        return generateMushroomState(generationData, "b");
      case "red_mushroom_block":
        return generateMushroomState(generationData, "r");
      case "note_block":
        return generateNoteBlockState(generationData);
    }
    return "";
  }

  public void addBlockStateToPack(String state, String asset) {
    String blockType = state.substring(0, 1);
    String stateData = state.substring(1);

    switch (blockType) {
      case "s":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles("mushroom_stem"));
        break;
      case "b":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles("brown_mushroom_block"));
        break;
      case "n":
        addNoteblockToPack(stateData, asset, getAllBlockStateFiles("note_block"));
        break;
      case "r":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles("red_mushroom_block"));
        break;
      case "c":

        break;
      default:
        Log.warn("No render type found");
        break;
    }
  }

  private ArrayList<Path> getAllBlockStateFiles(String blockstateName) {
    ArrayList<Path> paths = new ArrayList<>();
    for (String pack : resourcePacks) {
      Path blockstatePath = Paths.get(Craftory.getInstance().getDataFolder() +
          "/resourcepacks/" + pack + "/assets/minecraft/blockstates/" + blockstateName + ".json");
      if (Files.exists(blockstatePath)) {

      } else {
        try {
          Files.createDirectories(blockstatePath.getParent());
          Files.createFile(blockstatePath);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
       paths.add(blockstatePath);
    }
    return paths;
  }

  private void addNoteblockToPack(String state, String asset, ArrayList<Path> paths) {
    for (Path path : paths) {
      if (Files.exists(path) && Files.isRegularFile(path)) {
        try {
          JsonNode node = mapper.readTree(path.toFile());

          ObjectNode noteblockState = mapper.createObjectNode();
          String[] data = state.split(",");
          noteblockState.with("when").put("instrument", data[0]);
          noteblockState.with("when").put("note", data[1]);
          noteblockState.with("when").put("powered", mapBoolean(data[3].charAt(0)));
          noteblockState.with("apply").put("model", asset);

          ArrayNode multipartNode = node.withArray("multipart");
          multipartNode.add(noteblockState);

          mapper.writeValue(path.toFile(), node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void addMushroomToPack(String state, String asset, ArrayList<Path> paths) {
    for (Path path : paths) {
      if (Files.exists(path) && Files.isRegularFile(path)) {
        try {
          JsonNode node = mapper.readTree(path.toFile());

          ObjectNode mushroomState = mapper.createObjectNode();
          mushroomState.with("when").put("east", mapBoolean(state.charAt(0)));
          mushroomState.with("when").put("down", mapBoolean(state.charAt(1)));
          mushroomState.with("when").put("north", mapBoolean(state.charAt(2)));
          mushroomState.with("when").put("south", mapBoolean(state.charAt(3)));
          mushroomState.with("when").put("up", mapBoolean(state.charAt(4)));
          mushroomState.with("when").put("west", mapBoolean(state.charAt(5)));
          mushroomState.with("apply").put("model", asset);

          ArrayNode multipartNode = node.withArray("multipart");
          multipartNode.add(mushroomState);

          mapper.writeValue(path.toFile(), node);
        } catch (IOException e) {
          e.printStackTrace();
        }
        

      }
    }
  }

  private String mapBoolean(char booleanChar) {
    if (booleanChar == 'T') {
      return "true";
    }
    return "false";
  }

  private String generateNoteBlockState(GenerationData generationData) {
    String[] data = generationData.getData().split(",");
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
    if (generationData.getAmountLeft() != 0) {
      generationData.setAmountLeft(generationData.getAmountLeft() - 1);
      generationData.setData(blockData);
      blocksToUse.add(0, generationData);
    }

    return blockData;
  }

  private String generateMushroomState(GenerationData generationData, String block) {
    StringBuilder result = new StringBuilder();
    result.append(block);
    for (int i = 0; i < 6; i++) {
      result.append((Integer.parseInt(generationData.getData()) & 0x1 << i) != 0 ? "T" : "F");
    }

    blocksToUse.remove(0);
    if (generationData.getAmountLeft() != 0) {
      generationData.setAmountLeft(generationData.getAmountLeft() - 1);
      generationData.setData(String.valueOf(Integer.parseInt(generationData.getData()) - 1));
      blocksToUse.add(0, generationData);
    }

    return result.toString();
  }

  public void addRenderData(String blockKey, ArrayList<String> renderData) {
  }
}
