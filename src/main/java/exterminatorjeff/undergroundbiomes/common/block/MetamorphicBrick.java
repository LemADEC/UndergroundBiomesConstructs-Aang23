package exterminatorjeff.undergroundbiomes.common.block;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Predicate;
import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneStyle;
import exterminatorjeff.undergroundbiomes.intermod.OresRegistry;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MetamorphicBrick extends MetamorphicStone {
  public static final String internal_name = "metamorphic_brick";

  @Override
  public String getInternalName() {
    return internal_name;
  }

  @Override
  public UBStoneStyle getStoneStyle() {
    return UBStoneStyle.BRICK;
  }

  @Override
  public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target) {
    OresRegistry.INSTANCE.setRecheck(world, pos);
    return target.apply(Blocks.BRICK_BLOCK.getDefaultState());
  }

  @Override
  public void getDrops(NonNullList<ItemStack> stacks, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    Block cobbleBlock = API.METAMORPHIC_BRICK.getBlock();
    int meta = state.getBlock().getMetaFromState(state);
    ItemStack itemStack = new ItemStack(cobbleBlock, 1, meta);
    stacks.add(itemStack);
  }
}
