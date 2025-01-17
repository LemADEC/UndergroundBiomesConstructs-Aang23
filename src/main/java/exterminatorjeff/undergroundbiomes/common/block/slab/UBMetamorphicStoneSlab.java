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

import static exterminatorjeff.undergroundbiomes.api.enums.MetamorphicVariant.METAMORPHIC_VARIANTS;
import static exterminatorjeff.undergroundbiomes.api.enums.MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class UBMetamorphicStoneSlab extends UBStoneSlab {

  public UBMetamorphicStoneSlab() {
    setDefaultState(getDefaultState().withProperty(METAMORPHIC_VARIANT_PROPERTY, METAMORPHIC_VARIANTS[0]));
  }

  @Override
  public UBStone baseStone() {
    return (UBStone) API.METAMORPHIC_STONE.getBlock();
  }

  @Override
  public IProperty<?> getVariantProperty() {
    return METAMORPHIC_VARIANT_PROPERTY;
  }

  @Override
  public Comparable<?> getTypeForItem(ItemStack stack) {
    return METAMORPHIC_VARIANTS[stack.getMetadata() & 7];
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return super.getStateFromMeta(meta).withProperty(METAMORPHIC_VARIANT_PROPERTY, METAMORPHIC_VARIANTS[meta & 7]);
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    return new ItemStack(getItemBlock(), 1, METAMORPHIC_VARIANTS[getMetaFromState(state) & 7].ordinal());
  }
}
