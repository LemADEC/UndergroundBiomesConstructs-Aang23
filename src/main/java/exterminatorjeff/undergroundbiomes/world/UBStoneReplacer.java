/*
 */

package exterminatorjeff.undergroundbiomes.world;

import exterminatorjeff.undergroundbiomes.api.*;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneStyle;
import exterminatorjeff.undergroundbiomes.common.block.UBStone;
import exterminatorjeff.undergroundbiomes.common.block.slab.UBStoneSlab;
import exterminatorjeff.undergroundbiomes.common.block.wall.UBStoneWall;
import exterminatorjeff.undergroundbiomes.config.UBConfig;
import exterminatorjeff.undergroundbiomes.intermod.OresRegistry;
import exterminatorjeff.undergroundbiomes.intermod.StonesRegistry;
import exterminatorjeff.undergroundbiomes.world.noise.NoiseGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.block.properties.PropertyBool;
import vazkii.quark.world.block.BlockSpeleothem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import exterminatorjeff.undergroundbiomes.common.block.*;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneType;
import exterminatorjeff.undergroundbiomes.api.API;
import net.minecraftforge.fml.common.Loader;

public abstract class UBStoneReplacer implements UBStrataColumnProvider {

  final UBBiome[] biomeList;
  final NoiseGenerator noiseGenerator;

  public UBStoneReplacer(UBBiome[] biomeList, NoiseGenerator noiseGenerator) {
    this.biomeList = biomeList;
    this.noiseGenerator = noiseGenerator;
    if (biomeList == null)
      throw new RuntimeException();
    if (noiseGenerator == null)
      throw new RuntimeException();
  }

  public abstract int[] getBiomeValues(Chunk chunk);

  @SuppressWarnings("deprecation")
  public void replaceStoneInChunk(World world, Chunk chunk, final boolean sendUpdates) {
    boolean quarkpresent = Loader.isModLoaded("quark");
    int[] biomeValues = getBiomeValues(chunk);
    int xPos = chunk.getPos().x << 4;
    int zPos = chunk.getPos().z << 4;
    // TimeTracker.manager.start("overall");

    // For each storage array
    for (ExtendedBlockStorage storage : chunk.getBlockStorageArray()) {
      if ( storage == null
        || storage.isEmpty() ) {
        continue;
      }
      int yPos = storage.getYLocation();
      if (yPos >= UBConfig.SPECIFIC.generationHeight()) {
        return;
      }
      MutableBlockPos currentBlockPos = new MutableBlockPos();
      for (int x = 0; x < 16; ++x) {
        for (int z = 0; z < 16; ++z) {
          // Get the underground biome for the position
          UBBiome currentBiome = biomeList[biomeValues[(x << 4) + z]];
          if (currentBiome == null) {
            throw new RuntimeException("" + biomeValues[(x << 4) + z]);
          }
          currentBlockPos.setPos(x, 0, z);
          final String biomeName = Objects.requireNonNull(chunk.getBiome(currentBlockPos, chunk.getWorld().getBiomeProvider()).getRegistryName()).toString();
          //
          // Perlin noise for strata layers height variation
          int variation = (int) (noiseGenerator.noise((xPos + x) / 55.533, (zPos + z) / 55.533, 3, 1, 0.5) * 10 - 5);
          for (int y = 0; y < 16; ++y) {
            currentBlockPos.setPos(xPos + x, yPos + y, zPos + z);
            IBlockState currentBlockState = storage.get(x, y, z);
            Block currentBlock = currentBlockState.getBlock();
            /*
             * Skip air, water and our own blocks
             */
            if ( Block.isEqualTo(Blocks.AIR, currentBlock)
              || Block.isEqualTo(Blocks.WATER, currentBlock)
              || currentBlock instanceof UBStone
              || currentBlock instanceof UBOre ) {
              continue;
            }
            /*
             * Stone and variants
             */
            // Replace stone with UBified version
            if (currentBlock == Blocks.STONE) {
              setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos, currentBiome.getStrataBlockAtLayer(yPos + y + variation));
              continue;
            }
            // Replace monster egg with UBified version
            if ( currentBlock == Blocks.MONSTER_EGG
              && API.SETTINGS.replaceMonsterStone() ) {
              final BlockSilverfish.EnumType silverFishType = currentBlockState.getValue(BlockSilverfish.VARIANT);
              // skip disabled subtypes
              if ( silverFishType == BlockSilverfish.EnumType.COBBLESTONE
                && !API.SETTINGS.replaceCobblestone() ) {
                continue;
              }
              if ( silverFishType != BlockSilverfish.EnumType.STONE
                && !API.SETTINGS.replaceStoneBrick() ) {
                continue;
              }
              // replace all variants with stone (TODO: add variants for monster egg stones)
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                            StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.MONSTER_STONE).getBlock()
                              .getStateFromMeta(block.getMetaFromState(strata)));
              }
              continue;
            }
            // Replace cobblestone with UBified version
            if ( currentBlock == Blocks.COBBLESTONE
              && API.SETTINGS.replaceCobblestone() ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                            StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.COBBLE).getBlock()
                              .getStateFromMeta(block.getMetaFromState(strata)));
              }
              continue;
            }
            // Replace stone brick with UBified version
            if ( currentBlock == Blocks.STONEBRICK
              && API.SETTINGS.replaceStoneBrick() ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                // TODO: keep mossy and cracked variations?
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                            StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.BRICK).getBlock()
                              .getStateFromMeta(block.getMetaFromState(strata)));
              }
              continue;
            }
            // Replace stone half slab with UBified version
            if ( currentBlock == Blocks.STONE_SLAB
              && API.SETTINGS.replaceStoneSlab() ) {
              final BlockStoneSlab.EnumType typeVanilla = currentBlockState.getValue(BlockStoneSlab.VARIANT);
              final BlockStoneSlab.EnumBlockHalf enumBlockHalf = currentBlockState.getValue(BlockStoneSlab.HALF);
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                UBStoneSlab slab;
                switch (typeVanilla) {
                case STONE:
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.STONE).getSlab().getHalfSlab();
                  break;
                case COBBLESTONE:
                  if (!API.SETTINGS.replaceCobblestone()) {
                    continue;
                  }
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.COBBLE).getSlab().getHalfSlab();
                  break;
                case SMOOTHBRICK:
                  if (!API.SETTINGS.replaceStoneBrick()) {
                    continue;
                  }
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.BRICK).getSlab().getHalfSlab();
                  break;
                case SAND:
                  if (!API.SETTINGS.replaceSandstone()) {
                    continue;
                  }
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SANDSTONE).getSlab().getHalfSlab();
                  break;
                default:
                  continue;
                }
                // skip when base stone has no related slab
                if (slab == null) {
                  continue;
                }
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                         withProperty(slab.getDefaultState().withProperty(UBStoneSlab.HALF, enumBlockHalf),
                                      slab.getVariantProperty(), strata.getValue(slab.getVariantProperty()) ));
              }
              continue;
            }
            // Replace stone double slab with UBified version
            if ( currentBlock == Blocks.DOUBLE_STONE_SLAB
              && API.SETTINGS.replaceStoneSlab() ) {
              final BlockStoneSlab.EnumType typeVanilla = currentBlockState.getValue(BlockStoneSlab.VARIANT);
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                UBStoneSlab slab;
                switch (typeVanilla) {
                case STONE:
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.STONE).getSlab().getFullSlab();
                  break;
                case COBBLESTONE:
                  if (!API.SETTINGS.replaceCobblestone()) {
                    continue;
                  }
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.COBBLE).getSlab().getFullSlab();
                  break;
                case SMOOTHBRICK:
                  if (!API.SETTINGS.replaceStoneBrick()) {
                    continue;
                  }
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.BRICK).getSlab().getFullSlab();
                  break;
                case SAND:
                  if (!API.SETTINGS.replaceSandstone()) {
                    continue;
                  }
                  slab = (UBStoneSlab) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SANDSTONE).getSlab().getFullSlab();
                  break;
                default:
                  continue;
                }
                // skip when base stone has no related slab
                if (slab == null) {
                  continue;
                }
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                         withProperty(slab.getDefaultState(),
                                      slab.getVariantProperty(), strata.getValue(slab.getVariantProperty()) ));
              }
              continue;
            }
            // Replace wall with UBified version
            if ( currentBlock == Blocks.COBBLESTONE_WALL
              && API.SETTINGS.replaceCobblestone()
              && API.SETTINGS.replaceStoneWall() ) {
              final BlockWall.EnumType typeVanilla = currentBlockState.getValue(BlockWall.VARIANT);
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                UBStoneWall wall;
                switch (typeVanilla) {
                case NORMAL:
                case MOSSY:// TODO: add mossy cobblestone walls
                  wall = (UBStoneWall) StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.COBBLE).getWall().getBlock();
                  break;
                default:
                  continue;
                }
                // skip when base stone has no related wall
                if (wall == null) {
                  continue;
                }
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                         wall.getStateFromMeta(block.getMetaFromState(strata)) );
              }
              continue;
            }
            // Replace Biomes'o Plenty grass with UBified version
            if ( Objects.requireNonNull(currentBlock.getRegistryName()).toString().equals("biomesoplenty:grass")
              && currentBlockState.getProperties().toString().contains("=overgrown_stone")
              && API.SETTINGS.replaceOvergrown() ) {
              // TODO: replace the hack with proper API or something?
              boolean snowy = currentBlockState.getValue(PropertyBool.create("snowy"));
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                if (!snowy) {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.OVERGROWN).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata)));
                } else {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.OVERGROWN_SNOWED).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata)));
                }
              }
              continue;
            }
            // Replace mossy cobblestone with UBified version
            if ( currentBlock == Blocks.MOSSY_COBBLESTONE
              && API.SETTINGS.replaceMossyCobblestone() ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                            StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.MOSSY_COBBLE).getBlock()
                              .getStateFromMeta(block.getMetaFromState(strata)));
              }
              continue;
            }
            // Replace gravel with UBified version
            if ( currentBlock == Blocks.GRAVEL
              && API.SETTINGS.replaceGravel()
              && !API.SETTINGS.replaceGravelExcludedBiomes().contains(biomeName) ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                            StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.GRAVEL).getBlock()
                              .getStateFromMeta(block.getMetaFromState(strata)));
              }
              continue;
            }
            // Replace yellow sand with UBified version
            if ( currentBlock == Blocks.SAND
              && API.SETTINGS.replaceSand()
              && currentBlockState.getProperties().get(BlockSand.VARIANT) != BlockSand.EnumType.RED_SAND
              && !API.SETTINGS.replaceSandExcludedBiomes().contains(biomeName) ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                            StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SAND).getBlock()
                              .getStateFromMeta(block.getMetaFromState(strata)));
              }
              continue;
            }
            // Replace clay with UBified version
            if ( currentBlock == Blocks.CLAY
              && API.SETTINGS.replaceClay()
              && !API.SETTINGS.replaceClayExcludedBiomes().contains(biomeName) ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                            StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.CLAY).getBlock()
                              .getStateFromMeta(block.getMetaFromState(strata)));
              }
              continue;
            }
            // Replace sandstones with UBified version
            if ( currentBlock == Blocks.SANDSTONE
              && API.SETTINGS.replaceSandstone()
              && !API.SETTINGS.replaceSandExcludedBiomes().contains(biomeName) ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                if (currentBlockState.getProperties().get(BlockSandStone.TYPE) == BlockSandStone.EnumType.DEFAULT) {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SANDSTONE).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata)));
                } else if (currentBlockState.getProperties().get(BlockSandStone.TYPE) == BlockSandStone.EnumType.SMOOTH) {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SMOOTH_SANDSTONE).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata)));
                } else if (currentBlockState.getProperties().get(BlockSandStone.TYPE) == BlockSandStone.EnumType.CHISELED) {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.CHISELED_SANDSTONE).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata)));
                }
              }
              continue;
            }
            // Replace Quark Speleothem with UBified version
            if ( quarkpresent
              && API.SETTINGS.replaceSpeleothems()
              && currentBlock instanceof BlockSpeleothem ) {
              IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
              if (strata.getBlock() instanceof UBStone) {
                UBStone block = (UBStone) strata.getBlock();
                if (block.getStoneType() == UBStoneType.IGNEOUS) {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SPELEOTHEM).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata))
                                .withProperty(IgneousSpeleothem.SIZE,
                                              IgneousSpeleothem.EnumSize.values()[Math.max(0, currentBlockState.getValue(BlockSpeleothem.SIZE).ordinal())]));
                }
                if (block.getStoneType() == UBStoneType.METAMORPHIC) {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SPELEOTHEM).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata))
                                .withProperty(MetamorphicSpeleothem.SIZE,
                                              MetamorphicSpeleothem.EnumSize.values()[Math.max(0, currentBlockState.getValue(BlockSpeleothem.SIZE).ordinal())]));
                }
                if (block.getStoneType() == UBStoneType.SEDIMENTARY) {
                  setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos,
                              StonesRegistry.INSTANCE.stoneFor(block.getStoneType(), UBStoneStyle.SPELEOTHEM).getBlock()
                                .getStateFromMeta(block.getMetaFromState(strata))
                                .withProperty(SedimentarySpeleothem.SIZE,
                                              SedimentarySpeleothem.EnumSize.values()[Math.max(0, currentBlockState.getValue(BlockSpeleothem.SIZE).ordinal())]));
                }
              }
              continue;
            }

            /*
             * Ore
             */
            IBlockState strata = currentBiome.getStrataBlockAtLayer(yPos + y + variation);
            Block strataBlock = strata.getBlock();
            if (!(strataBlock instanceof UBStone)) {
              strata = currentBiome.filler;
              strataBlock = strata.getBlock();
            }
            if (OresRegistry.INSTANCE.isUBified(strataBlock, currentBlockState)) {
              if (strataBlock instanceof UBStone) {
                UBStone stone = ((UBStone) strataBlock);
                IBlockState ore = OresRegistry.INSTANCE.getUBifiedOre(stone, stone.getMetaFromState(strata), currentBlockState);
                setBlock(world, storage, sendUpdates, x, y, z, currentBlockPos, ore);
              }
            }
          }// for y
        }// for z
      }// for x
    }// for storage
  }

  // note: this is a workaround for crappy java type checking...
  protected <T extends Comparable<T>> IBlockState withProperty(IBlockState blockState, IProperty<T> property, Comparable<?> comparable)
  {
    return blockState.withProperty(property, (T) comparable);
  }

  private void setBlock(final World world, final ExtendedBlockStorage storage, final boolean sendUpdates,
                        final int x, final int y, final int z, final BlockPos blockPos, final IBlockState blockState) {
    storage.set(x, y, z, blockState);
    if (sendUpdates) {
      world.notifyBlockUpdate(blockPos, Blocks.STONE.getDefaultState(), blockState, 3);
    }
  }

  abstract public UBBiome UBBiomeAt(int x, int z);

  public void redoOres(World world) {
    HashMap<ChunkPos, ArrayList<BlockPos>> toRedo = OresRegistry.INSTANCE.forRedo(world);
    for (ChunkPos chunkID : toRedo.keySet()) {
      ArrayList<BlockPos> locations = toRedo.get(chunkID);
      Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkID.x, chunkID.z);
      if (chunk == null)
        continue;
      int[] biomeValues = getBiomeValues(chunk);
      for (BlockPos location : locations) {
        IBlockState currentBlockState = chunk.getBlockState(location);
        UBBiome currentBiome = biomeList[biomeValues[((location.getX() & 0xF) << 4) + (location.getZ() & 0xF)]];
        int variation = (int) (noiseGenerator.noise((location.getX()) / 55.533, (location.getZ()) / 55.533, 3, 1, 0.5) * 10 - 5);
        IBlockState strata = currentBiome.getStrataBlockAtLayer(location.getY() + variation);
        Block strataBlock = strata.getBlock();
        if (!(strataBlock instanceof UBStone)) {
          strata = currentBiome.filler;
          strataBlock = strata.getBlock();
        }
        if (OresRegistry.INSTANCE.isUBified(strataBlock, currentBlockState)) {
          if (strataBlock instanceof UBStone) {
            UBStone stone = (UBStone) strataBlock;
            IBlockState ore = OresRegistry.INSTANCE.getUBifiedOre(stone, stone.getMetaFromState(strata), currentBlockState);
            chunk.setBlockState(location, ore);
          }
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  private UBStrataColumn strataColumn(final StrataLayer[] strata, final IBlockState fillerBlockCodes, final int variation) {
    return new UBStrataColumn() {

      public IBlockState stone(int y) {
        if (y >= UBConfig.SPECIFIC.generationHeight())
          return Blocks.STONE.getDefaultState();
        for (StrataLayer stratum : strata) {
          if (stratum.heightInLayer(y + variation)) {
            return stratum.filler;
          }
        }
        return fillerBlockCodes;
      }

      public IBlockState cobblestone(int height) {
        if (height >= UBConfig.SPECIFIC.generationHeight())
          return Blocks.COBBLESTONE.getDefaultState();
        IBlockState stone = stone(height);
        if (stone.getBlock() == API.IGNEOUS_STONE.getBlock()) {
          return API.IGNEOUS_COBBLE.getBlock().getStateFromMeta(stone.getBlock().getMetaFromState(stone));
        }
        if (stone.getBlock() == API.METAMORPHIC_STONE.getBlock()) {
          return API.METAMORPHIC_COBBLE.getBlock().getStateFromMeta(stone.getBlock().getMetaFromState(stone));
        }
        return stone;
      }

      public IBlockState cobblestone() {
        IBlockState stone = stone();
        if (stone.getBlock() == API.IGNEOUS_STONE.getBlock()) {
          return API.IGNEOUS_COBBLE.getBlock().getStateFromMeta(stone.getBlock().getMetaFromState(stone));
        }
        if (stone.getBlock() == API.METAMORPHIC_STONE.getBlock()) {
          return API.METAMORPHIC_COBBLE.getBlock().getStateFromMeta(stone.getBlock().getMetaFromState(stone));
        }
        return stone;
      }

      public IBlockState stone() {
        return fillerBlockCodes;
      }
    };
  }

  public UBStrataColumn strataColumn(int x, int z) {
    // make sure we have the right chunk
    UBBiome biome = UBBiomeAt(x, z);
    int variation = (int) (noiseGenerator.noise((x) / 55.533, (z) / 55.533, 3, 1, 0.5) * 10 - 5);
    return strataColumn(biome.strata, biome.filler, variation);
  }
}
