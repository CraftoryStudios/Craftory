package studio.craftory.core;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.plugin.java.JavaPlugin;
import studio.craftory.core.blocks.ComplexObject;
import studio.craftory.core.blocks.SimpleObject;
import studio.craftory.core.blocks.TestBlock;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.interfaces.Tickable;
import studio.craftory.core.persistence.PersistenceManager;

public final class Craftory extends JavaPlugin {

  protected static final ConcurrentMap<Integer, Tickable> tickableObjects = new ConcurrentHashMap<>();
  static final int AMOUNT = 10;
  Random random = new Random();

  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getLogger().info("Plugin now running! NOW");

    PersistenceManager persistenceManager = new PersistenceManager();

    JsonObject root = new JsonObject();
    TestBlock testBlock = new TestBlock();

    JsonObject objectJSON = (JsonObject) persistenceManager.saveFields(testBlock);
    getServer().getLogger().info(() -> persistenceManager.getGson().toJson(root));
    root.add("test",objectJSON);

    //Setup Executor
    AsyncExecutionManager syncExecutionManager = new AsyncExecutionManager(4);
    syncExecutionManager.registerTickableClass(SimpleObject.class);
    syncExecutionManager.registerTickableClass(ComplexObject.class);

    //Create Objects
    for (int i = 0; i < AMOUNT; i++) {
      tickableObjects.put(i, new SimpleObject(i));
      tickableObjects.put(i + AMOUNT, new ComplexObject());
    }

    //Assign Objects Execution
    for (Tickable tickable : tickableObjects.values()) {
      syncExecutionManager.addTickableObject(tickable);
    }

    //Begin Executor Running
    syncExecutionManager.runTaskTimer(this, 20L, 1L);


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

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
