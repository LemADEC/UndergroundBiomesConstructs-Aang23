package exterminatorjeff.undergroundbiomes.common.block;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Predicate;
import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneStyle;
import exterminatorjeff.undergroundbiomes.api.enums.UBStoneType;
import exterminatorjeff.undergroundbiomes.intermod.OresRegistry;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.entity.monster.EntitySilverfish;
import java.util.Random;

import static exterminatorjeff.undergroundbiomes.api.enums.SedimentaryVariant.*;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SedimentaryMonsterStone extends UBStone {
  public static final String internal_name = "sedimentary_monster_stone";

  @Override
  public String getInternalName() {
    return internal_name;
  }

  public SedimentaryMonsterStone() {
    setDefaultState(blockState.getBaseState().withProperty(SEDIMENTARY_VARIANT_PROPERTY, LIMESTONE));
  }

  @Override
  public UBStoneType getStoneType() {
    return UBStoneType.SEDIMENTARY;
  }

  @Override
  public UBStoneStyle getStoneStyle() {
    return UBStoneStyle.MONSTER_STONE;
  }

  @Override
  public BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, SEDIMENTARY_VARIANT_PROPERTY);
  }

  @Override
  public int getNbVariants() {
    return NB_VARIANTS;
  }

  @Override
  public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
  {
      if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops"))
      {
          EntitySilverfish entitysilverfish = new EntitySilverfish(worldIn);
          entitysilverfish.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
          worldIn.spawnEntity(entitysilverfish);
          entitysilverfish.spawnExplosionParticle();
      }
  }

  @Override
  public String getVariantName(int meta) {
    return SEDIMENTARY_VARIANTS[meta & 7].toString();
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(SEDIMENTARY_VARIANT_PROPERTY, SEDIMENTARY_VARIANTS[meta & 7]);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(SEDIMENTARY_VARIANT_PROPERTY).getMetadata();
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    if (state.getValue(SEDIMENTARY_VARIANT_PROPERTY) == LIGNITE) {
      return API.LIGNITE_COAL.getItem();
    } else
      return super.getItemDropped(state, rand, fortune);
  }

  @Override
  public int damageDropped(IBlockState state) {
    if (state.getValue(SEDIMENTARY_VARIANT_PROPERTY) == LIGNITE) {
      return 0;
    }
    return super.damageDropped(state);
  }

  @Override
  public boolean isFortuneAffected(IBlockState state) {
    return state.getValue(SEDIMENTARY_VARIANT_PROPERTY) == LIGNITE;
  }

  @Override
  public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
    return getBaseHardness() * state.getValue(SEDIMENTARY_VARIANT_PROPERTY).getHardness();
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
    return getBaseResistance() * world.getBlockState(pos).getValue(SEDIMENTARY_VARIANT_PROPERTY).getResistance();
  }

  @Override
  public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target) {
    OresRegistry.INSTANCE.setRecheck(world, pos);
    return target.apply(Blocks.MONSTER_EGG.getDefaultState());
  }

  @Override
  public UBStone baseStone() {
    return this;
  }
}
