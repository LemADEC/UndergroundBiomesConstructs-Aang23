package exterminatorjeff.undergroundbiomes.common.block.slab;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import exterminatorjeff.undergroundbiomes.client.UBCreativeTab;
import exterminatorjeff.undergroundbiomes.common.UBSubBlock;
import exterminatorjeff.undergroundbiomes.common.itemblock.SlabItemBlock;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class UBStoneSlab extends BlockSlab implements UBSubBlock {

  private SlabItemBlock itemBlock;

  public UBStoneSlab() {
    super(Material.ROCK);
    Block baseStone = baseStone().toBlock();
    setHarvestLevel(baseStone.getHarvestTool(baseStone.getDefaultState()), baseStone.getHarvestLevel(baseStone.getDefaultState()));
    if (!isDouble()) {
      setDefaultState(blockState.getBaseState().withProperty(HALF, EnumBlockHalf.BOTTOM));
      setCreativeTab(UBCreativeTab.UB_BLOCKS_TAB);
    }
    useNeighborBrightness = !isDouble();
  }

  public final void setItemBlock(SlabItemBlock itemBlock) {
    this.itemBlock = itemBlock;
  }

  @Override
  public Block toBlock() {
    return this;
  }

  @Override
  public int quantityDropped(Random random) {
    return isDouble() ? 2 : 1;
  }

  @Override
  public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    drops.add(new ItemStack(itemBlock, quantityDropped(new Random()), damageDropped(state)));
  }

  @Override
  public final ItemBlock getItemBlock() {
    return itemBlock;
  }

  @Override
  public String getTranslationKey(int meta) {
    return super.getTranslationKey() + "." + getVariantName(meta);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    if (isDouble())
      return new BlockStateContainer(this, getVariantProperty());
    else
      return new BlockStateContainer(this, getVariantProperty(), HALF);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    IBlockState state = getDefaultState();
    if (!isDouble())
      return state.withProperty(HALF, (meta < 8 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP));
    return state;
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    int meta = baseStone().getMetaFromState(state);

    if (!isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP)
      meta |= 8;

    return meta;
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state) & 7;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for (int i = 0; i < getNbVariants(); ++i)
      list.add(new ItemStack(this, 1, i));
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    return new ItemStack(itemBlock, 1, state.getBlock().getMetaFromState(state));
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return itemBlock;
  }

  @Override
  public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
    return baseStone().getBlockHardness(blockState, worldIn, pos);
  }

  @SuppressWarnings("deprecation")
  @Override
  public float getExplosionResistance(Entity exploder) {
    return baseStone().getExplosionResistance(exploder);
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
    return baseStone().getExplosionResistance(world, pos, exploder, explosion);
  }
}
