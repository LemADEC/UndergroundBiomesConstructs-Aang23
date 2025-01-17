package exterminatorjeff.undergroundbiomes.common.block;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Predicate;
import exterminatorjeff.undergroundbiomes.api.ModInfo;
import exterminatorjeff.undergroundbiomes.api.common.UBBlock;
import exterminatorjeff.undergroundbiomes.client.UBCreativeTab;
import exterminatorjeff.undergroundbiomes.config.UBConfig;
import exterminatorjeff.undergroundbiomes.intermod.DropsRegistry;
import exterminatorjeff.undergroundbiomes.intermod.OresRegistry;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
public abstract class UBStone extends Block implements UBBlock {

  private static final float BASE_HARDNESS = 2.25F;
  private static final float BASE_RESISTANCE = 10.0F;
  protected static final float COBBLE_HARDNESS_MODIFIER = 1.333F;

  protected ItemBlock itemBlock;

  public UBStone() {
    super(Material.ROCK);
    setCreativeTab(UBCreativeTab.UB_BLOCKS_TAB);
    setHardness(UBConfig.SPECIFIC.hardnessModifier());
    setResistance(UBConfig.SPECIFIC.resistanceModifier());
    setHarvestLevel("pickaxe", 0);
    setTranslationKey(this.getInternalName());
    setRegistryName(ModInfo.MODID, this.getInternalName());
    ((UBConfig) (UBConfig.SPECIFIC)).hardnessModifier.addTracker(hardness -> setHardness(hardness));
    ((UBConfig) (UBConfig.SPECIFIC)).resistanceModifier.addTracker(resistance -> setResistance(resistance));
    itemBlock = new UBItemBlockStone(this);
  }

  @Override
  public Block toBlock() {
    return this;
  }

  public ItemBlock getItemBlock() {
    return itemBlock;
  }

  @Override
  public Block setHardness(float hardness) {
    return super.setHardness(hardness * BASE_HARDNESS);
  }

  @Override
  public Block setResistance(float resistance) {
    return super.setResistance(resistance * BASE_RESISTANCE);
  }

  public float getBaseHardness() {
    return blockHardness;
  }

  public float getBaseResistance() {
    return blockResistance / 5.0F;
  }

  public abstract float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos);

  public abstract float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion);

  @Override
  public int quantityDropped(IBlockState state, int fortune, Random random) {
    int quantity = 1;
    if ((fortune != 0) && (((UBStone) state.getBlock()).isFortuneAffected(state))) {
      // Fortune III gives up to 4 items
      int j = random.nextInt(fortune + 2);
      quantity = Math.max(j, 1);
    }
    return quantity;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for (int i = 0; i < getNbVariants(); ++i)
      list.add(new ItemStack(this, 1, i));
  }

  public abstract int getMetaFromState(IBlockState state);

  public abstract IBlockState getStateFromMeta(int meta);

  public abstract String getInternalName();

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return itemBlock;
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public void getDrops(NonNullList<ItemStack> stacks, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    DropsRegistry.INSTANCE.addDrops(stacks, this, world, pos, state, fortune);
    super.getDrops(stacks, world, pos, state, fortune);
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    Block targetBlock = state.getBlock();
    if (targetBlock instanceof UBStone) {
      return new ItemStack(this.itemBlock, 1, getMetaFromState(state));
    }
    return targetBlock.getPickBlock(state, target, world, pos, player);
  }

  @Override
  public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target) {
    OresRegistry.INSTANCE.setRecheck(world, pos);
    return target.apply(Blocks.STONE.getDefaultState());
  }

  public abstract boolean isFortuneAffected(IBlockState state);

  /**
   * @author CurtisA, LouisDB
   */
  private class UBItemBlockStone extends ItemBlock {

    public UBItemBlockStone(UBStone block) {
      super(block);
      setMaxDamage(0);
      setTranslationKey(block.getTranslationKey());
      setRegistryName(block.getRegistryName());
      setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
      return damage;
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
      return super.getTranslationKey() + "." + getVariantName(stack.getMetadata());
    }

  }

}
