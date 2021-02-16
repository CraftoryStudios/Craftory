package studio.craftory.core.blocks.renders;

import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.NoteBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.utils.Log;

public class BlockStateRenderer implements CraftoryRenderer{

  @Override
  public void render(@NonNull Block block, @NonNull CraftoryDirection direction, @NonNull RenderData renderData) {
    int directionKey = direction.label;

    //Must have render data for every CraftoryDirection
    if (renderData.getData().size() != 6) {
      Log.warn("Incomplete render data");
      return;
    }

    String data = renderData.getData().get(directionKey);
    if (data.isEmpty()) {
      //Block doesn't use directions so default to NORTH
      data = renderData.getData().get(0);
    }

    String blockType = data.substring(0,1);
    String stateData = data.substring(1);

    switch (blockType) {
      case "s":
        renderStem(stateData, block);
        break;
      case "b":
        renderBrownMushroom(stateData, block);
        break;
      case "n":
        renderNoteBlock(stateData, block);
        break;
      case "r":
        renderRedMushroom(stateData, block);
        break;
      case "c":
        renderChorusPlant(stateData, block);
        break;
      default:
        Log.warn("No render type found");
        break;
    }
  }

  private void renderChorusPlant(@NonNull String stateData, Block block) {
    block.setType(Material.CHORUS_PLANT, false);
    block.setBlockData(getMutlifacingData(stateData, block), false);
  }

  private void renderRedMushroom(@NonNull String stateData, Block block) {
    block.setType(Material.RED_MUSHROOM_BLOCK, false);
    block.setBlockData(getMutlifacingData(stateData, block), false);
  }

  private void renderNoteBlock(@NonNull String stateData, Block block) {
    String instrument = stateData.substring(0,1);
    int note = Integer.parseInt(stateData.substring(1,3));
    String powered = stateData.substring(3,4);

    block.setType(Material.NOTE_BLOCK, false);
    NoteBlock blockData = (NoteBlock) block.getBlockData();

    blockData.setInstrument(getInstrument(instrument));
    blockData.setNote(new Note(note));
    blockData.setPowered(powered.equals("T"));
    block.setBlockData(blockData, false);
  }

  private void renderBrownMushroom(@NonNull String stateData, Block block) {
    block.setType(Material.BROWN_MUSHROOM_BLOCK, false);
    block.setBlockData(getMutlifacingData(stateData, block), false);
  }

  private void renderStem(@NonNull String stateData, Block block) {
    block.setType(Material.MUSHROOM_STEM, false);
    block.setBlockData(getMutlifacingData(stateData, block), false);
  }

  private Instrument getInstrument(@NonNull String instrument) {
    switch (instrument) {
      case "a":
        return Instrument.BANJO;
      case "b":
        return Instrument.BASS_DRUM;
      case "c":
        return Instrument.BASS_GUITAR;
      case "d" :
        return Instrument.BELL;
      case "e":
        return Instrument.BIT;
      case "f":
        return Instrument.CHIME;
      case "g":
        return Instrument.COW_BELL;
      case "h":
        return Instrument.DIDGERIDOO;
      case "i":
        return Instrument.FLUTE;
      case "j":
        return Instrument.GUITAR;
      case "k":
        return Instrument.IRON_XYLOPHONE;
      case "l":
        return Instrument.PIANO;
      case "m":
        return Instrument.SNARE_DRUM;
      case "n":
        return Instrument.STICKS;
      case "o":
        return Instrument.XYLOPHONE;
      default:
        return Instrument.BANJO;
    }
  }

  private static MultipleFacing getMutlifacingData(@NonNull String stateData, Block block) {
    MultipleFacing blockData = (MultipleFacing) block.getBlockData();

    String[] states = stateData.split("");

    blockData.setFace(BlockFace.DOWN, states[0].equals("T"));
    blockData.setFace(BlockFace.EAST, states[1].equals("T"));
    blockData.setFace(BlockFace.NORTH, states[2].equals("T"));
    blockData.setFace(BlockFace.SOUTH, states[3].equals("T"));
    blockData.setFace(BlockFace.UP, states[4].equals("T"));
    blockData.setFace(BlockFace.WEST, states[5].equals("T"));

    return blockData;
  }

}
