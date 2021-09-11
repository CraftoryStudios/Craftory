package studio.craftory.core.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import lombok.NonNull;
import studio.craftory.core.Craftory;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.utils.Log;

public class BlockAssetGenerator {

  public static final String NOTE_BLOCK = "note_block";
  public static final String MUSHROOM_STEM = "mushroom_stem";
  public static final String BROWN_MUSHROOM_BLOCK = "brown_mushroom_block";
  public static final String RED_MUSHROOM_BLOCK = "red_mushroom_block";
  private ArrayList<GenerationData> blocksToUse = new ArrayList<>();
  private final ArrayList<String> resourcePacks = new ArrayList<>(Arrays.asList("low", "normal", "high"));
  Gson gson = new GsonBuilder().disableHtmlEscaping().create();
  JsonObject renderDataFile = new JsonObject();
  File file = new File(Craftory.getInstance().getDataFolder(), "renderData.json");

  public BlockAssetGenerator() {
    try {
      renderDataFile = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
    } catch (IOException e) {
      Log.debug("Couldn't read render data while generating assets");
    }
    //Should be read from config file and generated
    blocksToUse.add(new GenerationData(750, "a00T", NOTE_BLOCK));
    blocksToUse.add(new GenerationData(64, "0", MUSHROOM_STEM));
    blocksToUse.add(new GenerationData(64, "0", BROWN_MUSHROOM_BLOCK));
    blocksToUse.add(new GenerationData(64, "0", RED_MUSHROOM_BLOCK));
  }

  public void writeRenderDataFile() {
    try (FileWriter fw = new FileWriter(file)){
      gson.toJson(renderDataFile, fw);
    } catch (IOException e) {
      Log.warn("Couldn't write render data");
    }
  }

  public void addToRenderFile(String blockKey, JsonArray renderFileData) {
    renderDataFile.add(blockKey, renderFileData);
  }

  public String generateBlockState() {
    GenerationData generationData = blocksToUse.get(0);
    switch (generationData.getBlockName()) {
      case MUSHROOM_STEM:
        return generateMushroomState(generationData, "s");
      case BROWN_MUSHROOM_BLOCK:
        return generateMushroomState(generationData, "b");
      case RED_MUSHROOM_BLOCK:
        return generateMushroomState(generationData, "r");
      case NOTE_BLOCK:
        return generateNoteBlockState(generationData);
      default:
        return "";
    }
  }

  public void addBlockStateToPack(String state, String asset, CraftoryDirection direction) {
    String blockType = state.substring(0, 1);
    String stateData = state.substring(1);

    switch (blockType) {
      case "s":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles(MUSHROOM_STEM), direction);
        break;
      case "b":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles(BROWN_MUSHROOM_BLOCK), direction);
        break;
      case "n":
        addNoteblockToPack(stateData, asset, getAllBlockStateFiles(NOTE_BLOCK), direction);
        break;
      case "r":
        addMushroomToPack(stateData, asset, getAllBlockStateFiles(RED_MUSHROOM_BLOCK), direction);
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
      if (!Files.exists(blockstatePath)) {
        try {
          Files.createDirectories(blockstatePath.getParent());
          Files.createFile(blockstatePath);
        } catch (IOException e) {
          Log.error(e.toString());
        }
      }
       paths.add(blockstatePath);
    }
    return paths;
  }

  private void addNoteblockToPack(String state, String asset, ArrayList<Path> paths, CraftoryDirection direction) {
    for (Path path : paths) {
      if (Files.exists(path) && Files.isRegularFile(path)) {
        JsonElement node = null;
        try (FileReader fr = new FileReader(path.toFile())) {
          node = new JsonParser().parse(fr);
        } catch (Exception e) {
          Log.error(e.toString());
        }
          if (node == null || !node.isJsonObject()) {
            node = new JsonObject();
          }
          JsonObject nodeObject = node.getAsJsonObject();

          JsonObject noteblockState = new JsonObject();
          noteblockState.addProperty("model", asset);
          addDirectionData(noteblockState, direction);

          StringBuilder builder = new StringBuilder();
          builder.append("instrument=");
          builder.append(getInstrument(state.substring(0,1)));
          builder.append(",note=");
          builder.append(Integer.parseInt(state.substring(1,3)));
          builder.append(",powered=");
          builder.append(mapBoolean(state.charAt(3)));

          JsonObject variants = new JsonObject();
          variants.add(builder.toString(), noteblockState);
          nodeObject.add("variants", variants);
        try (FileWriter fw = new FileWriter(path.toFile())){
          gson.toJson(node, fw);
        } catch (IOException e) {
          Log.error(e.toString());
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

  private void addDirectionData(JsonObject node, CraftoryDirection direction) {
    if (direction != CraftoryDirection.WEST) {
      switch (direction) {
        case NORTH:
          node.addProperty("y", 90);
          break;
        case SOUTH:
          node.addProperty("y", 270);
          break;
        case EAST:
          node.addProperty("y", 180);
          break;
        case UP:
          node.addProperty("x", 90);
          break;
        case DOWN:
          node.addProperty("x", 270);
          break;
        default:
          break;
      }
    }
  }

  private void addMushroomToPack(String state, String asset, ArrayList<Path> paths, CraftoryDirection direction) {
    for (Path path : paths) {
      if (Files.exists(path) && Files.isRegularFile(path)) {
        JsonObject node = null;
        try (FileReader fr = new FileReader(path.toFile())) {
          node = new JsonParser().parse(fr).getAsJsonObject();
        } catch(Exception e) {
          Log.error(e.toString());
        }
        if (node == null || !node.isJsonObject()) {
          node = new JsonObject();
        }

          JsonObject mushroomState = new JsonObject();
          JsonObject when = new JsonObject();
          when.addProperty("east", mapBoolean(state.charAt(0)));
          when.addProperty("down", mapBoolean(state.charAt(1)));
          when.addProperty("north", mapBoolean(state.charAt(2)));
          when.addProperty("south", mapBoolean(state.charAt(3)));
          when.addProperty("up", mapBoolean(state.charAt(4)));
          when.addProperty("west", mapBoolean(state.charAt(5)));
          mushroomState.add("when", when);

          JsonObject apply = new JsonObject();
          apply.addProperty("model", asset);
          mushroomState.add("apply", apply);
          addDirectionData(apply, direction);

          JsonArray multipartNode = new JsonArray();
          multipartNode.add(mushroomState);
          node.add("multipart", multipartNode);

        try (FileWriter fw = new FileWriter(path.toFile())){
          gson.toJson(node, fw);
        } catch (IOException e) {
          Log.error(e.toString());
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
