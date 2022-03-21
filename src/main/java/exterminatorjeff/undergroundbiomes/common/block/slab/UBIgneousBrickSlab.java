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
public abstract class UBIgneousBrickSlab extends UBIgneousStoneSlab {

  @Override
  public UBStone baseStone() {
    return (UBStone) API.IGNEOUS_BRICK.getBlock();
  }

}
