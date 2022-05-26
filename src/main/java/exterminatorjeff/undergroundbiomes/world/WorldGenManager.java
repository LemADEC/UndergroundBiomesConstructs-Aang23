package exterminatorjeff.undergroundbiomes.world;

import java.util.concurrent.CopyOnWriteArrayList;

import exterminatorjeff.undergroundbiomes.api.UBStrataColumn;
import exterminatorjeff.undergroundbiomes.api.UBStrataColumnProvider;
import exterminatorjeff.undergroundbiomes.api.UndergroundBiomeSet;
import exterminatorjeff.undergroundbiomes.api.common.UBLogger;
import exterminatorjeff.undergroundbiomes.api.event.UBForceReProcessEvent;
import exterminatorjeff.undergroundbiomes.config.UBConfig;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Level;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

/**
 * @author CurtisA, LouisDB
 */
public final class WorldGenManager implements UBStrataColumnProvider {

  private final UBLogger LOGGER;

  private final int dimensionID;
  private final UndergroundBiomeSet biomesSet;
  private UBStoneReplacer stoneReplacer;

  private boolean worldLoaded = false;
  private final CopyOnWriteArrayList<ChunkUpdate> chunkUpdates = new CopyOnWriteArrayList<>();

  public WorldGenManager(int dimensionID) {
    LOGGER = new UBLogger(WorldGenManager.class + " " + dimensionID, Level.INFO);
    LOGGER.debug("Dimension " + dimensionID + " will be UBified");

    this.dimensionID = dimensionID;
    biomesSet = new UBBiomesSet(UBConfig.SPECIFIC);
  }

  @SubscribeEvent
  public void onWorldLoad(WorldEvent.Load event) {
    final World world = event.getWorld();

    if (world.provider.getDimension() == dimensionID && !worldLoaded) {
      LOGGER.debug("Dimension " + dimensionID + " loaded");

      worldLoaded = true;

      // TODO World specific config
      world.getSaveHandler().getWorldDirectory();

      int seed = (int) world.getSeed();
      if (UBConfig.SPECIFIC.dimensionSpecificSeeds()) {
        seed += dimensionID;
      }
      this.stoneReplacer = new TraditionalStoneReplacer(seed, UBConfig.SPECIFIC.biomeSize(), biomesSet);
    }
  }

  @SubscribeEvent
  public void onWorldUnload(WorldEvent.Unload event) {
    final World world = event.getWorld();
    if (world.provider.getDimension() == dimensionID && worldLoaded) {
      worldLoaded = false;
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
    onProcess(event.getWorld(), event.getChunkX(), event.getChunkZ());
  }

  // May be called by other mods
  @SubscribeEvent
  public void onForceReprocessAll(UBForceReProcessEvent event) {
    onProcess(event.getWorld(), event.getChunkX(), event.getChunkZ());
  }

  public void onProcess(final World world, final int chunkX, final int chunkZ) {
    if (world.provider.getDimension() == dimensionID && worldLoaded) {
      // process immediately what we can
      doProcess(world, chunkX, chunkZ, false);
      // schedule a delayed update to compensate the event being lost or out of synchronized
      chunkUpdates.add(new ChunkUpdate(chunkX, chunkZ));
    }
  }

  public void doProcess(final World world, final int chunkX, final int chunkZ, final boolean sendUpdates) {
    if (world.provider.getDimension() == dimensionID && worldLoaded) {
      Chunk chunk = world.getChunk(chunkX, chunkZ);
      this.stoneReplacer.replaceStoneInChunk(world, chunk, sendUpdates);
      stoneReplacer.redoOres(world);
    }
  }

  public void onTick() {
    if (chunkUpdates.isEmpty()) {
      return;
    }
    final World world = DimensionManager.getWorld(dimensionID);
    if (world == null) {
      return;
    }
    for (final ChunkUpdate chunkUpdate : chunkUpdates) {
      chunkUpdate.tick--;
      if (chunkUpdate.tick <= 0) {
        doProcess(world, chunkUpdate.chunkX, chunkUpdate.chunkZ, true);
        chunkUpdates.remove(chunkUpdate);
      }
    }
  }

  @SubscribeEvent
  public void onGenerateMinable(GenerateMinable event) {
    if (event.getWorld().provider.getDimension() == dimensionID && worldLoaded) {
      switch (event.getType()) {
        case GRANITE:
        case DIORITE:
        case ANDESITE:
          event.setResult(Result.DENY);
          break;
        default:
          break;
      }
    }
  }

  @Override
  public UBStrataColumn strataColumn(int x, int z) {
    return this.stoneReplacer.strataColumn(x, z);
  }

  private static class ChunkUpdate {
    public long tick;
    public final int chunkX;
    public final int chunkZ;

    public ChunkUpdate(final int chunkX, final int chunkZ) {
      this.tick = 2;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
    }
  }
}
