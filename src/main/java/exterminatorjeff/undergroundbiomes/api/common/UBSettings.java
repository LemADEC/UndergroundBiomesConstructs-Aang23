package exterminatorjeff.undergroundbiomes.api.common;

import net.minecraft.block.state.IBlockState;

import java.util.Set;

/**
 * Underground Biomes settings.<br>
 * See the UB configuration file for more information.
 *
 * @author LouisDB
 */
public interface UBSettings {

  /*
   * General
   */

  boolean crashOnProblems();

  boolean realistic();

  boolean ubifyRecipes();

  boolean ubifyOres();

  int regularStoneCrafting();

  float hardnessModifier();

  float resistanceModifier();

  int biomeSize();

  int generationHeight();

  boolean regularStoneBiomes();

  boolean harmoniousStrata();

  Set<Integer> includedDimensions();

  Set<Integer> excludedDimensions();

  boolean dimensionSpecificSeeds();

  boolean ubifyVillages();

  boolean replaceCobblestone();

  boolean replaceStoneBrick();

  boolean replaceStoneSlab();

  boolean replaceStoneWall();

  boolean replaceMonsterStone();

  boolean replaceGravel();

  boolean replaceClay();

  String replaceGravelExcludedBiomes();

  String replaceSandExcludedBiomes();

  String replaceClayExcludedBiomes();

  boolean replaceSand();

  boolean replaceSandstone();

  boolean replaceOvergrown();

  boolean replaceMossyCobblestone();

  boolean buttonsOn();

  boolean igneousButtonsOn();

  boolean metamorphicButtonsOn();

  boolean sedimentaryButtonsOn();

  boolean stoneButtonsOn();

  boolean cobbleButtonsOn();

  boolean brickButtonsOn();

  boolean stairsOn();

  boolean igneousStairsOn();

  boolean metamorphicStairsOn();

  boolean sedimentaryStairsOn();

  boolean stoneStairsOn();

  boolean cobbleStairsOn();

  boolean brickStairsOn();

  boolean wallsOn();

  boolean igneousWallsOn();

  boolean metamorphicWallsOn();

  boolean sedimentaryWallsOn();

  boolean stoneWallsOn();

  boolean cobbleWallsOn();

  boolean brickWallsOn();

  boolean replaceSpeleothems();

  int buttonRecipeResult();

  boolean disableVanillaStoneVariants();

  String customOreBlockHardnes();

  /*
   * Client
   */

  boolean plainSlabTextures();

  boolean alternativeSlabTextures();

  boolean generationAllowed(IBlockState block);

  boolean displayTooltipModName();

  String getTooltipModNamePreText();

  String getTooltipModNamePreTextFormatting();

  String getTooltipModNameFormatting();

  String getTooltipModNamePostText();

  String getTooltipModNamePostTextFormatting();

}
