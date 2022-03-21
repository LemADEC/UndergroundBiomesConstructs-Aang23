package exterminatorjeff.undergroundbiomes.common.block.wall;

import javax.annotation.ParametersAreNonnullByDefault;

import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.names.BlockEntry;
import exterminatorjeff.undergroundbiomes.common.block.UBStone;
import mcp.MethodsReturnNonnullByDefault;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UBWallIgneousBrick extends UBWallIgneous {

  public UBWallIgneousBrick(BlockEntry baseStoneEntry) {
    super(baseStoneEntry);
  }

  @Override
  public UBStone baseStone() {
    return (UBStone) API.IGNEOUS_BRICK.getBlock();
  }

}
