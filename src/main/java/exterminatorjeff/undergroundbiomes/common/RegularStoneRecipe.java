package exterminatorjeff.undergroundbiomes.common;

import javax.annotation.ParametersAreNonnullByDefault;

import exterminatorjeff.undergroundbiomes.api.common.UBLogger;
import exterminatorjeff.undergroundbiomes.config.SettingTracker;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;

import mcp.MethodsReturnNonnullByDefault;
import org.apache.logging.log4j.Level;

/**
 * @author CurtisA, LouisDB
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RegularStoneRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe, SettingTracker<Integer> {

  private static final UBLogger LOGGER = new UBLogger(RegularStoneRecipe.class, Level.INFO);

  private IRecipe recipe;

  public RegularStoneRecipe() {
    recipe = new ShapedOreRecipe(getRegistryName(), new ItemStack(Blocks.COBBLESTONE, 4), "XX", "XX", 'X', "cobblestone");
    setRegistryName("undergroundbiomes:regular_cobblestone");
  }

  @Override
  public void update(Integer value) {
    LOGGER.info("Choosing regular stone recipe n°" + value);

    // Create the new recipes
    switch (value) {
      case 1:
        recipe = new ShapelessOreRecipe(null, Blocks.COBBLESTONE, new OreIngredient("cobblestone"));
        break;
      case 2:
        recipe = new ShapelessOreRecipe(null, Blocks.COBBLESTONE, new OreIngredient("cobblestone"), Items.REDSTONE);
        break;
      case 3:
        recipe = new ShapedOreRecipe(null, new ItemStack(Blocks.COBBLESTONE, 1), "XX", "XX", 'X', "cobblestone");
        break;
      case 4:
        recipe = new ShapedOreRecipe(null, new ItemStack(Blocks.COBBLESTONE, 4), "XX", "XX", 'X', "cobblestone");
        break;
    }
  }

  @Override
  public boolean matches(InventoryCrafting inv, World worldIn)
  {
    return recipe.matches(inv, worldIn);
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    return recipe.getCraftingResult(inv);
  }

  @Override
  public boolean canFit(int width, int height) {
    return recipe.canFit(width, height);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return recipe.getRecipeOutput();
  }
}
