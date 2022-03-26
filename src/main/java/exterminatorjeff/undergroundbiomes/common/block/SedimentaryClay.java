package exterminatorjeff.undergroundbiomes.common.block;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Predicate;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneStyle;
import exterminatorjeff.undergroundbiomes.intermod.DropsRegistry;
import exterminatorjeff.undergroundbiomes.intermod.OresRegistry;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.NonNullList;

import net.minecraft.item.ItemStack;

/**
 * @author Aang23
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SedimentaryClay extends SedimentaryStone {
  public static final String internal_name = "sedimentary_clay";

  public SedimentaryClay() {
    super();
    setSoundType(SoundType.GROUND);
  }

  @Override
  public String getInternalName() {
    return internal_name;
  }

  @Override
  public UBStoneStyle getStoneStyle() {
    return UBStoneStyle.CLAY;
  }

  @Override
  public Material getMaterial(IBlockState state) {
    return Material.CLAY;
  }

  @Override
  public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target) {
    OresRegistry.INSTANCE.setRecheck(world, pos);
    return target.apply(Blocks.CLAY.getDefaultState());
  }

  @Override
  public void getDrops(NonNullList<ItemStack> stacks, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    stacks.add(new ItemStack(Items.CLAY_BALL, 4));
    DropsRegistry.INSTANCE.addDrops(stacks, this, world, pos, state, fortune);
  }

  @Override
  public String getHarvestTool(IBlockState state) {
    return "shovel";
  }

  @Override
  public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
    return Blocks.CLAY.getBlockHardness(state, worldIn, pos);
  }
}
