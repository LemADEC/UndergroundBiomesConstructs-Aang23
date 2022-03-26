package exterminatorjeff.undergroundbiomes.common.block;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneStyle;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneType;
import exterminatorjeff.undergroundbiomes.intermod.DropsRegistry;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static exterminatorjeff.undergroundbiomes.api.enums.SedimentaryVariant.*;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SedimentaryStone extends UBStone {
  public static final String internal_name = "sedimentary_stone";

  public SedimentaryStone() {
    super();
    setDefaultState(blockState.getBaseState().withProperty(SEDIMENTARY_VARIANT_PROPERTY, LIMESTONE));
  }

  @Override
  public String getInternalName() {
    return internal_name;
  }

  @Override
  public UBStoneType getStoneType() {
    return UBStoneType.SEDIMENTARY;
  }

  @Override
  public UBStoneStyle getStoneStyle() {
    return UBStoneStyle.STONE;
  }

  @Override
  public int getNbVariants() {
    return NB_VARIANTS;
  }

  @Override
  public String getVariantName(int meta) {
    return SEDIMENTARY_VARIANTS[meta & 7].toString();
  }

  @Override
  public BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, SEDIMENTARY_VARIANT_PROPERTY);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(SEDIMENTARY_VARIANT_PROPERTY, SEDIMENTARY_VARIANTS[meta & 7]);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(SEDIMENTARY_VARIANT_PROPERTY).getMetadata();
  }

  // note: there's no sedimentary cobble and lignite is a thing
  @Override
  public void getDrops(NonNullList<ItemStack> stacks, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    Item stoneBlock = this.getItemDropped(state, new Random(), fortune);
    ItemStack itemStack;
    if (stoneBlock == itemBlock) {
      int meta = state.getBlock().getMetaFromState(state);
      itemStack = new ItemStack(stoneBlock, 1, meta);
    } else {
      itemStack = new ItemStack(stoneBlock, 1);
    }
    stacks.add(itemStack);
    DropsRegistry.INSTANCE.addDrops(stacks, this, world, pos, state, fortune);
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    if (state.getValue(SEDIMENTARY_VARIANT_PROPERTY) == LIGNITE) {
      return API.LIGNITE_COAL.getItem();
    } else {
      return super.getItemDropped(state, rand, fortune);
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    if (state.getValue(SEDIMENTARY_VARIANT_PROPERTY) == LIGNITE) {
      return 0;
    }
    return super.damageDropped(state);
  }

  @Override
  public boolean isFortuneAffected(IBlockState state) {
    return state.getValue(SEDIMENTARY_VARIANT_PROPERTY) == LIGNITE;
  }

  @Override
  public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
    return getBaseHardness() * state.getValue(SEDIMENTARY_VARIANT_PROPERTY).getHardness();
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
    return getBaseResistance() * world.getBlockState(pos).getValue(SEDIMENTARY_VARIANT_PROPERTY).getResistance();
  }

  @Override
  public UBStone baseStone() {
    return this;
  }
}
