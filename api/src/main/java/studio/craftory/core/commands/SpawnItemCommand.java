package studio.craftory.core.commands;

import java.util.Optional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.craftory.core.items.CustomItemUtils;
import studio.craftory.core.utils.ParseUtils;

public class SpawnItemCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player && args.length > 0) {
      Player player = (Player)sender;
      String itemName = args[0];
      Optional<ItemStack> itemStack;

      if (itemName.contains(":")) {
        itemStack = CustomItemUtils.getCustomItem(itemName);
      } else {
        if (CustomItemUtils.isDuplicateItemName(itemName)) {
          player.sendMessage("This item has multiple versions please specify namespace in form plugin:item");
          return true;
        } else {
          itemStack = CustomItemUtils.getUniqueItem(itemName);
        }
      }

      if(itemStack.isPresent()) {
        ItemStack item = itemStack.get();
        int amount = ParseUtils.parseOrDefault(args[1],1);
        item.setAmount(amount);
        player.getInventory().addItem(item);
        return true;
      }
    }
    return false;
  }
}
