package exterminatorjeff.undergroundbiomes.common.block;

import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.common.IUBOreConfig;
import exterminatorjeff.undergroundbiomes.client.UBCreativeTab;
import exterminatorjeff.undergroundbiomes.common.UBSubBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import tyra314.toolprogression.api.OverwrittenContent;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Base class to create UBified version of vanilla and modded ores.<br>
 * <br>
 * One instance per ore. Each instance has its metadata used for UB stone
 * variants.<br>
 * If the base ore also use metadata, then it's one instance per baseOre:meta
 * pairs.
 *
 * @author CurtisA, LouisDB
 * @see {@link UBOreIgneous}, {@link UBOreMetamorphic}, {@link UBOreSedimentary}
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class UBOre extends Block implements UBSubBlock {

  public static final int NO_METADATA = -1;

  public final Block baseOre;
  /**
   * {@value #NO_METADATA} if the base ore do not have metadata
   */
  public final int baseOreMeta;
  public final IBlockState baseOreState;
  protected final ItemBlock itemBlock;
  public final IUBOreConfig config;

  @SuppressWarnings("deprecation")
  public UBOre(Block baseOre, IUBOreConfig config) {
    super(baseOre.getMaterial(baseOre.getDefaultState()));
    if (Loader.isModLoaded("toolprogression")) {
      if (OverwrittenContent.blocks.containsKey(this.getTranslationKey())) {
        String toolClass = OverwrittenContent.blocks.get(this.getTranslationKey()).toolclass;
        int harvestLevel = OverwrittenContent.blocks.get(this.getTranslationKey()).level;
        setHarvestLevel(toolClass, harvestLevel);
      }
    } else {
      setHarvestLevel(baseOre.getHarvestTool(baseOre.getDefaultState()),
                      baseOre.getHarvestLevel(baseOre.getDefaultState()));
    }
    setCreativeTab(UBCreativeTab.UB_ORES_TAB);
    this.itemBlock = new UBItemOre(this);
    this.baseOre = baseOre;
    this.baseOreMeta = config.getMeta();
    this.baseOreState = baseOre.getStateFromMeta(baseOreMeta);
    this.config = config;
  }

  @Override
  public int getLightValue(IBlockState state, IBlockAccess access, BlockPos pos) {
    if (config.getLightValue() > -1) {
      return config.getLightValue();
    }
    return baseOre.getLightValue(baseOreState, access, pos);
  }

  @Override
  public Block toBlock() {
    return this;
  }

  @Override
  public final ItemBlock getItemBlock() {
    return itemBlock;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public BlockRenderLayer getRenderLayer() {
    if (config.hasAlphaOverlay())
      return BlockRenderLayer.TRANSLUCENT;
    else
      return BlockRenderLayer.CUTOUT_MIPPED;
  }

  @Override
  protected abstract BlockStateContainer createBlockState();

  @Override
  public abstract IBlockState getStateFromMeta(int meta);

  @Override
  public int getMetaFromState(IBlockState state) {
    return baseStone().toBlock().getMetaFromState(state);
  }

  @Override
  public int quantityDropped(IBlockState state, int fortune, Random random) {
    return baseOre.quantityDropped(baseOreState, fortune, random);
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for (int i = 0; i < getNbVariants(); ++i) {
      list.add(new ItemStack(this, 1, i));
    }
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return baseOre.getItemDropped(baseOreState, rand, fortune);
  }

  @Override
  public void onPlayerDestroy(World world, BlockPos pos, IBlockState state) {
    baseOre.onPlayerDestroy(world, pos, baseOreState);
  }

  @Override
  public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    baseOre.onBlockHarvested(world, pos, baseOreState, player);
  }

  @Nullable
  public String getHarvestTool(IBlockState state) {
    if (Loader.isModLoaded("toolprogression")) {
      if ( OverwrittenContent.blocks.containsKey(this.getTranslationKey())
        && API.SETTINGS.customOreBlockHardnes().contains(this.getTranslationKey())) {
        return OverwrittenContent.blocks.get(this.getTranslationKey()).toolclass;
      }
      return baseOre.getHarvestTool(baseOreState);
    }
    return baseOre.getHarvestTool(baseOreState);
  }

  public int getHarvestLevel(IBlockState state) {
    if (Loader.isModLoaded("toolprogression")) {
      if ( OverwrittenContent.blocks.containsKey(this.getTranslationKey())
        && API.SETTINGS.customOreBlockHardnes().contains(this.getTranslationKey())) {
        return OverwrittenContent.blocks.get(this.getTranslationKey()).level;
      }
      return baseOre.getHarvestLevel(baseOreState);
    }
    return baseOre.getHarvestLevel(baseOreState);
  }

  @Override
  public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    return baseOre.removedByPlayer(baseOreState, world, pos, player, willHarvest);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean canProvidePower(IBlockState state) {
    return baseOre.canProvidePower(baseOreState);
  }

  @SuppressWarnings("deprecation")
  @Override
  public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    return baseOre.getWeakPower(baseOreState, world, pos, side);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
    baseOre.randomDisplayTick(baseOreState, world, pos, rand);
  }

  @Override
  public int damageDropped(IBlockState state) {
    int test = this.baseOre.damageDropped(baseOreState);
    if (test > 0) {
      return test;
    }
    if (baseOreMeta == NO_METADATA) {
      return 0;
    } else {
      return baseOreMeta;
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    return baseOre.getDrops(world, pos, baseOreState, fortune);
  }

  @Override
  public void dropXpOnBlockBreak(World worldIn, BlockPos pos, int amount) {
    baseOre.dropXpOnBlockBreak(worldIn, pos, amount);
  }

  @SuppressWarnings("deprecation")
  @Override
  public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
    return baseOre.getBlockHardness(baseOreState, worldIn, pos);
  }

  @Override
  // Use baseOreState to support forestry ores
  public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
    return baseOre.getExpDrop(baseOreState, world, pos, fortune);
  }

  @Override
  public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
    //return player.getHeldItemMainhand().canHarvestBlock(baseOreState);
    return super.canHarvestBlock(world, pos, player); // Change restores behaviour where tools with lower harvest levels do not provide resources
  }

  @Override
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    baseOre.updateTick(worldIn, pos, baseOreState, rand);
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    return new ItemStack(itemBlock, 1, getMetaFromState(world.getBlockState(pos)));
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag tooltipFlag) {
    if (API.SETTINGS.displayTooltipModName()) {
      Map<String, ModContainer> indexedModList = Loader.instance().getIndexedModList();
      assert baseOre.getRegistryName() != null;
      String modName = indexedModList.get(baseOre.getRegistryName().getNamespace()).getName();
      infos.add(API.SETTINGS.getTooltipModNamePreTextFormatting() + API.SETTINGS.getTooltipModNamePreText() + "\u00A7r "
          + API.SETTINGS.getTooltipModNameFormatting() + modName + "\u00A7r "
          + API.SETTINGS.getTooltipModNamePostTextFormatting() + API.SETTINGS.getTooltipModNamePostText());
    }
    super.addInformation(stack, world, infos, tooltipFlag);
  }

  /**
   * @author CurtisA, LouisDB
   */
  private class UBItemOre extends ItemBlock {

    public UBItemOre(UBOre ubOre) {
      super(ubOre);
      setMaxDamage(0);
      setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
      return damage;
    }

    @Override
    public int getDamage(ItemStack stack) {
      return stack.getMetadata();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
      // Works for English but maybe not all languages. Some may have to
      // use the lang file to override this behavior
      // TODO Find a cleaner way to do this
      try {

        if (baseOreMeta == NO_METADATA) {
          return I18n.format(baseStone().getItemBlock().getUnlocalizedNameInefficiently(stack) + ".name") + " "
              + I18n.format(baseOre.getTranslationKey() + ".name");
        }
        ItemStack baseStack = new ItemStack(baseOre, 1, baseOreMeta);
        return I18n.format(baseStone().getItemBlock().getUnlocalizedNameInefficiently(stack) + ".name") + " "
            + baseStack.getDisplayName();
      } catch (Error error) {
        if (baseOreMeta == NO_METADATA) {
          return baseStone().getItemBlock().getUnlocalizedNameInefficiently(stack) + ".name" + " "
              + baseOre.getTranslationKey() + ".name";
        }
        ItemStack baseStack = new ItemStack(baseOre, 1, baseOreMeta);
        return baseStone().getItemBlock().getUnlocalizedNameInefficiently(stack) + ".name" + " "
            + baseStack.getDisplayName();
      }
    }

  }
}
