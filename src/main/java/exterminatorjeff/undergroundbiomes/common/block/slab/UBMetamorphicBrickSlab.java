package exterminatorjeff.undergroundbiomes.common.block.slab;

import javax.annotation.ParametersAreNonnullByDefault;

import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.common.block.UBStone;
import mcp.MethodsReturnNonnullByDefault;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class UBMetamorphicBrickSlab extends UBMetamorphicStoneSlab {

  @Override
  public UBStone baseStone() {
    return (UBStone) API.METAMORPHIC_BRICK.getBlock();
  }

}
