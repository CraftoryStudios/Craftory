package studio.craftory.craftoryexample.commands;

import java.util.Optional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.craftoryexample.blocks.SimpleGenerator;

public class SpawnGeneratorCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      Optional<BaseCustomBlock> generator = CustomBlockAPI.placeCustomBlock(player.getLocation().add(0,3,0), SimpleGenerator.class);
      return generator.isPresent();
    }
    return false;
  }
}
