package exterminatorjeff.undergroundbiomes.common.block.stairs;

import javax.annotation.ParametersAreNonnullByDefault;

import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.common.block.UBStone;
import exterminatorjeff.undergroundbiomes.common.itemblock.StairsItemBlock;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import static exterminatorjeff.undergroundbiomes.api.enums.SedimentaryVariant.SEDIMENTARY_VARIANTS;
import static exterminatorjeff.undergroundbiomes.api.enums.SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UBStairsSedimentary extends UBStoneStairs {

  public UBStairsSedimentary(IBlockState modelState, EnumFacing facing, StairsItemBlock itemBlock) {
    super(modelState, facing, itemBlock);
    setDefaultState(getDefaultState().withProperty(SEDIMENTARY_VARIANT_PROPERTY, SEDIMENTARY_VARIANTS[0]));
  }

  @Override
  public UBStone baseStone() {
    return (UBStone) API.SEDIMENTARY_STONE.getBlock();
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING, HALF, SHAPE, SEDIMENTARY_VARIANT_PROPERTY);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return super.getStateFromMeta(meta).withProperty(SEDIMENTARY_VARIANT_PROPERTY, SEDIMENTARY_VARIANTS[meta & 7]);
  }

}
