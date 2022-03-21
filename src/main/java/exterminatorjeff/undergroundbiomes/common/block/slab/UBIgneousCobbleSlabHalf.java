package exterminatorjeff.undergroundbiomes.common.block.slab;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import exterminatorjeff.undergroundbiomes.api.API;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UBIgneousCobbleSlabHalf extends UBIgneousCobbleSlab {

  @Override
  public boolean isDouble() {
    return false;
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    int meta = state.getBlock().getMetaFromState(state);
    return new ItemStack(API.METAMORPHIC_STONE_SLAB.getItem(), 1, meta & 7);
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return API.IGNEOUS_COBBLE_SLAB.getItem();
  }

  @Override
  public void getDrops(NonNullList<ItemStack> stacks, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    int meta = state.getBlock().getMetaFromState(state);
    ItemStack itemStack = new ItemStack(API.IGNEOUS_COBBLE_SLAB.getItem(), 1, meta & 7);
    stacks.add(itemStack);
  }
}
