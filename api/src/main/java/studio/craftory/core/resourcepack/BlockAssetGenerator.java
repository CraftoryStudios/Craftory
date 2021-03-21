package studio.craftory.core.resourcepack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.NonNull;
import studio.craftory.core.Craftory;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.utils.Log;

public class BlockAssetGenerator {

  private ArrayList<GenerationData> blocksToUse = new ArrayList<>();
  private final ArrayList<String> resourcePacks = new ArrayList<>(Arrays.asList("low", "normal", "high"));
  ObjectMapper mapper = new ObjectMapper();
  ObjectNode renderDataFile = mapper.createObjectNode();
  File file = new File(Craftory.getInstance().getDataFolder(), "renderData.json");

  public BlockAssetGenerator() {
    try {
      renderDataFile = (ObjectNode) mapper.readTree(file);
    } catch (IOException e) {
    }
    //Should be read from config file and generated
    blocksToUse.add(new GenerationData(750, "a00T","note_block"));
    blocksToUse.add(new GenerationData(64, "0", "mushroom_stem"));
    blocksToUse.add(new GenerationData(64, "0", "brown_mushroom_block"));
    blocksToUse.add(new GenerationData(64, "0", "red_mushroom_block"));
  }

  public void writeRenderDataFile() {
    try {
      mapper.writeValue(file, renderDataFile);
    } catch (IOException e) {
      Log.warn("Couldn't write render data");
    }
  }

  public void addToRenderFile(String blockKey, ArrayNode renderFileData) {
    renderDataFile.set(blockKey, renderFileData);
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

  public void addBlockStateToPack(String state, String asset, CraftoryDirection direction) {
    String blockType = state.substring(0, 1);
    String stateData = state.substring(1);

    switch (blockType) {
      case "s":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles("mushroom_stem"), direction);
        break;
      case "b":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles("brown_mushroom_block"), direction);
        break;
      case "n":
        addNoteblockToPack(stateData, asset, getAllBlockStateFiles("note_block"), direction);
        break;
      case "r":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles("red_mushroom_block"), direction);
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

  private void addNoteblockToPack(String state, String asset, ArrayList<Path> paths, CraftoryDirection direction) {
    for (Path path : paths) {
      if (Files.exists(path) && Files.isRegularFile(path)) {
        try {
          JsonNode node = mapper.readTree(path.toFile());
          if (!node.isObject()) {
            node = mapper.createObjectNode();
          }

          ObjectNode noteblockState = mapper.createObjectNode();
          noteblockState.put("model", asset);
          addDirectionData(noteblockState, direction);

          StringBuilder builder = new StringBuilder();
          builder.append("instrument=");
          builder.append(getInstrument(state.substring(0,1)));
          builder.append(",note=");
          builder.append(Integer.parseInt(state.substring(1,3)));
          builder.append(",powered=");
          builder.append(mapBoolean(state.charAt(3)));

          ((ObjectNode) node.with("variants")).set(builder.toString(), noteblockState);

          mapper.writeValue(path.toFile(), node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private String getInstrument(@NonNull String instrument) {
    switch (instrument) {
      case "a":
        return "banjo";
      case "b":
        return "basedrum";
      case "c":
        return "bass";
      case "d" :
        return "bell";
      case "e":
        return "bit";
      case "f":
        return "chime";
      case "g":
        return "cow_bell";
      case "h":
        return "didgeridoo";
      case "i":
        return "flute";
      case "j":
        return "guitar";
      case "k":
        return "iron_xylophone";
      case "l":
        return "pling";
      case "m":
        return "snare";
      case "n":
        return "hat";
      case "o":
        return "xylophone";
      default:
        return "error";
    }
  }

  private void addDirectionData(ObjectNode node, CraftoryDirection direction) {
    if (direction != CraftoryDirection.WEST) {
      switch (direction) {
        case NORTH:
          node.put("y", 90);
          break;
        case SOUTH:
          node.put("y", 270);
          break;
        case EAST:
          node.put("y", 180);
          break;
        case UP:
          node.put("x", 90);
          break;
        case DOWN:
          node.put("x", 270);
          break;
      }
    }
  }

  private void addMushroomToPack(String state, String asset, ArrayList<Path> paths, CraftoryDirection direction) {
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
          addDirectionData(mushroomState.with("apply"), direction);

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
    String result = "n"+generationData.getData();
    char instrument = generationData.getData().charAt(0);
    instrument += 1;
    int note = Integer.parseInt(generationData.getData().substring(1,3));
    String powered = generationData.getData().substring(3,4);

    if (instrument == 'o' && note < 24) {
      instrument = 'a';
      note += 1;
    } else if (instrument == 'o' && note == 24) {
      instrument = 'a';
      note = 0;
      powered = "F";
    }

    String blockData = "" + instrument + ("00"+note).substring(String.valueOf(note).length()) + powered;
    decreaseGenerationData(generationData, blockData);

    return result;
  }

  private String generateMushroomState(GenerationData generationData, String block) {
    StringBuilder result = new StringBuilder();
    result.append(block);
    for (int i = 0; i < 6; i++) {
      result.append((Integer.parseInt(generationData.getData()) & 0x1 << i) != 0 ? "T" : "F");
    }

    String blockData = String.valueOf(Integer.parseInt(generationData.getData()) - 1);
    decreaseGenerationData(generationData, blockData);

    return result.toString();
  }

  private void decreaseGenerationData(GenerationData generationData, String blockData) {
    if (generationData.getAmountLeft() != 0) {
      generationData.setAmountLeft(generationData.getAmountLeft() - 1);
      generationData.setData(blockData);
    } else {
      blocksToUse.remove(0);
    }
  }

}
