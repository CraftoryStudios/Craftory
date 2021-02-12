package studio.craftory.craftoryexample;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import studio.craftory.core.Craftory;
import studio.craftory.core.data.keys.CraftoryKey;
import studio.craftory.core.executors.interfaces.Tickable;
import studio.craftory.core.items.CustomItemManager;
import studio.craftory.core.items.ItemEventManager;
import studio.craftory.core.persistence.PersistenceManager;
import studio.craftory.craftoryexample.executor.ComplexObject;
import studio.craftory.craftoryexample.executor.SimpleObject;
import studio.craftory.craftoryexample.items.Wrench;
import studio.craftory.craftoryexample.persitence.TestBlock;

public final class CraftoryExamplePlugin extends JavaPlugin {

  static final int AMOUNT = 10;
  protected static final ConcurrentMap<Integer, Tickable> tickableObjects = new ConcurrentHashMap<>();
  Random random = new Random();

  @Override
  public void onLoad() {

    //Executor
    //AsyncExecutionManager asyncExecutionManager = Craftory.getInstance().getAsyncExecutionManager();
    //asyncExecutionManager.registerTickableClass(SimpleObject.class);
    //asyncExecutionManager.registerTickableClass(ComplexObject.class);

    //Persistence
    PersistenceManager persistenceManager = Craftory.getInstance().getPersistenceManager();
    JsonObject root = new JsonObject();
    TestBlock testBlock = new TestBlock();

    Gson gson = new Gson();

    JsonObject objectJSON = (JsonObject) persistenceManager.saveFields(testBlock);
    root.add(testBlock.getSafeBlockLocation().getX() + "," + testBlock.getSafeBlockLocation().getY() + "," + testBlock.getSafeBlockLocation().getZ(),objectJSON);
    getServer().getLogger().info(() -> gson.toJson(root));

    CraftoryKey itemKey = new CraftoryKey("example","wrench");
    ItemStack wrench = new ItemStack(Material.STICK);
    ItemMeta itemMeta = wrench.getItemMeta();
    itemMeta.setDisplayName("A WRENCH");
    wrench.setItemMeta(itemMeta);
    CustomItemManager.registerCustomItem(itemKey, wrench);

    //ItemEventManager.registerDumbEvent(PlayerInteractEvent.class, Wrench::onClick);
    ItemEventManager.registerSmartEvent(PlayerInteractEvent.class, "example:wrench", Wrench::onClick);

    CraftoryKey toolKey = new CraftoryKey("example", "drill");
    ItemStack drill = new ItemStack(Material.GOLDEN_PICKAXE);
    CustomItemManager.setDisplayName(drill, "DRILL");
    CustomItemManager.setUnbreakable(drill, true);
    CustomItemManager.registerCustomItem(toolKey, drill);

    ItemEventManager.registerItemOnHoldEffects("example:drill", Arrays.asList(
        PotionEffectType.FAST_DIGGING.createEffect(Integer.MAX_VALUE,2),
        PotionEffectType.SLOW.createEffect(Integer.MAX_VALUE,1)
        ));
  }

  @Override
  public void onEnable() {
    //Test Executor
    //AsyncExecutionManager asyncExecutionManager = Craftory.getInstance().getAsyncExecutionManager();

    for (int i = 0; i < AMOUNT; i++) {
      tickableObjects.put(i, new SimpleObject(i));
      tickableObjects.put(i + AMOUNT, new ComplexObject());
    }

    //Assign Objects Execution
//    for (Tickable tickable : tickableObjects.values()) {
//      asyncExecutionManager.addTickableObject(tickable);
//    }


    AtomicInteger checker = new AtomicInteger(0);
    //Check a value
    getServer().getScheduler().scheduleSyncRepeatingTask(this,() -> {
      int indexs = getRandomNumber(0, AMOUNT - 1);
      int value = ((SimpleObject) tickableObjects.get(indexs)).getTestVar();
      this.getLogger().info("Value should be "+checker.get() * indexs+" but is "+value + " which is " + (value - (checker.get() * indexs)) + " different");
      checker.getAndAdd(500);

      int index = getRandomNumber(AMOUNT + 1, AMOUNT * 2);
      ComplexObject object = ((ComplexObject) tickableObjects.get(index));
      ArrayList<Integer> testArray = object.getIntegers();
      this.getLogger().info(testArray.toString());
    }, 20L, 500L);
  }

  public int getRandomNumber(int min, int max) { return random.nextInt(max-min) + min; }

}