package exterminatorjeff.undergroundbiomes.common.block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import com.google.common.base.Predicate;

import exterminatorjeff.undergroundbiomes.api.enums.UBStoneStyle;
import exterminatorjeff.undergroundbiomes.intermod.OresRegistry;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SedimentarySandstone extends SedimentaryStone {
  public static final String internal_name = "sedimentary_sandstone";

  public SedimentarySandstone() {
    super();
  }

  @Override
  public String getInternalName() {
    return internal_name;
  }

  @Override
  public UBStoneStyle getStoneStyle() {
    return UBStoneStyle.SANDSTONE;
  }

  @Override
  public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target) {
    OresRegistry.INSTANCE.setRecheck(world, pos);
    return target.apply(Blocks.SANDSTONE.getDefaultState());
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return itemBlock;
  }

  @Override
  public void getDrops(NonNullList<ItemStack> stacks, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    int meta = state.getBlock().getMetaFromState(state);
    ItemStack itemStack = new ItemStack(itemBlock, 1, meta);
    stacks.add(itemStack);
  }
}
