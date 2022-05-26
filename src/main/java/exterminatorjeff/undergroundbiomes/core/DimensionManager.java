/*
 */

package exterminatorjeff.undergroundbiomes.core;

import exterminatorjeff.undergroundbiomes.api.UBDimensionalStrataColumnProvider;
import exterminatorjeff.undergroundbiomes.api.UBStrataColumn;
import exterminatorjeff.undergroundbiomes.api.UBStrataColumnProvider;
import exterminatorjeff.undergroundbiomes.config.UBConfig;
import exterminatorjeff.undergroundbiomes.world.WorldGenManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;

/**
 * @author Zeno410
 */
public class DimensionManager implements UBDimensionalStrataColumnProvider {
  public HashMap<Integer, WorldGenManager> managers = new HashMap<>();
  private boolean villageRegistered = false;
  private boolean oreRegistered = false;

  public DimensionManager() {
  }

  public void refreshManagers() {
    // refresh event registration
    if (UBConfig.SPECIFIC.ubifyVillages() && !villageRegistered) {
      MinecraftForge.TERRAIN_GEN_BUS.register(this);
      villageRegistered = true;
    }
    if (!UBConfig.SPECIFIC.ubifyVillages() && villageRegistered) {
      MinecraftForge.TERRAIN_GEN_BUS.unregister(this);
      villageRegistered = false;
    }

    if (UBConfig.SPECIFIC.disableVanillaStoneVariants() && !oreRegistered) {
      MinecraftForge.ORE_GEN_BUS.register(this);
      oreRegistered = true;
    }

    if (!UBConfig.SPECIFIC.disableVanillaStoneVariants() && oreRegistered) {
      MinecraftForge.ORE_GEN_BUS.unregister(this);
      oreRegistered = false;
    }

    // clear existing managers
    for (final WorldGenManager manager : managers.values()) {
      if (manager != null) {
        MinecraftForge.EVENT_BUS.unregister(manager);
      }
    }
    managers = new HashMap<>();

    // create and register new managers
    ((UBConfig) (UBConfig.SPECIFIC)).getUBifiedDimensions().forEach(dimensionID -> {
      WorldGenManager manager = new WorldGenManager(dimensionID);
      MinecraftForge.EVENT_BUS.register(manager);
      managers.put(dimensionID, manager);
    });
  }

  @SubscribeEvent
  public void onWorldLoad(WorldEvent.Load event) {
    if (managers.size() == 0) UndergroundBiomes.PROXY.onServerLoad(event.getWorld().getMinecraftServer());
    int dimension = event.getWorld().provider.getDimension();
    WorldGenManager target = managers.get(dimension);
    if (target != null) target.onWorldLoad(event);
  }

  @SubscribeEvent
  public void onServerTick(final ServerTickEvent event) {
    if (event.side != Side.SERVER || event.phase != Phase.END) {
      return;
    }
    for (final WorldGenManager target : managers.values()) {
      if (target != null) {
        target.onTick();
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onGenerateMinable(OreGenEvent.GenerateMinable event) {
    int dimension = event.getWorld().provider.getDimension();
    WorldGenManager target = managers.get(dimension);
    if (target != null) target.onGenerateMinable(event);
  }

  public void clear() {
    managers.clear();
  }

  @Override
  public UBStrataColumnProvider ubStrataColumnProvider(int dimension) {
    WorldGenManager manager = this.managers.get(dimension);
    if (manager == null) return vanillaColumnProvider;
    return manager;
  }

  private final UBStrataColumnProvider vanillaColumnProvider = new UBStrataColumnProvider() {
    @Override
    public UBStrataColumn strataColumn(int x, int z) {
      return vanillaColumn;
    }

  };
  private final UBStrataColumn vanillaColumn = new UBStrataColumn() {

    @Override
    public IBlockState stone(int height) {
      return Blocks.STONE.getDefaultState();
    }

    @Override
    public IBlockState cobblestone(int height) {
      return Blocks.COBBLESTONE.getDefaultState();
    }

    @Override
    public IBlockState stone() {
      return Blocks.STONE.getDefaultState();
    }

    @Override
    public IBlockState cobblestone() {
      return Blocks.COBBLESTONE.getDefaultState();
    }

  };
}
