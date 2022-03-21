package exterminatorjeff.undergroundbiomes.common.block.slab;

import javax.annotation.ParametersAreNonnullByDefault;

import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.common.block.UBStone;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import static exterminatorjeff.undergroundbiomes.api.enums.IgneousVariant.IGNEOUS_VARIANTS;
import static exterminatorjeff.undergroundbiomes.api.enums.IgneousVariant.IGNEOUS_VARIANT_PROPERTY;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class UBIgneousStoneSlab extends UBStoneSlab {

  public UBIgneousStoneSlab() {
    setDefaultState(getDefaultState().withProperty(IGNEOUS_VARIANT_PROPERTY, IGNEOUS_VARIANTS[0]));
  }

  @Override
  public UBStone baseStone() {
    return (UBStone) API.IGNEOUS_STONE.getBlock();
  }

  @Override
  public IProperty<?> getVariantProperty() {
    return IGNEOUS_VARIANT_PROPERTY;
  }

  @Override
  public Comparable<?> getTypeForItem(ItemStack stack) {
    return IGNEOUS_VARIANTS[stack.getMetadata() & 7];
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return super.getStateFromMeta(meta).withProperty(IGNEOUS_VARIANT_PROPERTY, IGNEOUS_VARIANTS[meta & 7]);
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    return new ItemStack(getItemBlock(), IGNEOUS_VARIANTS[getMetaFromState(state) & 7].ordinal());
  }
}
