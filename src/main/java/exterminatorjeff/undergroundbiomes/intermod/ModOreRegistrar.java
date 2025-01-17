package exterminatorjeff.undergroundbiomes.intermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.ModInfo;
import exterminatorjeff.undergroundbiomes.api.common.UBLogger;
import exterminatorjeff.undergroundbiomes.api.common.UBModOreRegistrar;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ModOreRegistrar implements UBModOreRegistrar {

  private static final UBLogger LOGGER = new UBLogger(OresRegistry.class, Level.INFO);
  private final File directory;
  private final HashMap<String, UBOreConfig> ores = new HashMap<>();
  private final Type jsonType = new TypeToken<ArrayList<UBOreConfig>>() {
  }.getType();

  public ModOreRegistrar(FMLPreInitializationEvent event) {
    this.directory = Paths.get(event.getModConfigurationDirectory().toString(), "undergroundbiomes", "ores").toFile();
    try {
      Files.createDirectories(this.directory.toPath());
      createDefaults();
      setupOres();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void requestOreSetup(RegistryEvent.Register<Block> event, UBOreConfig ore) {
    ResourceLocation location = new ResourceLocation(ore.getInternalOreName());
    IForgeRegistry<Block> registry = event.getRegistry();
    if (!registry.containsKey(location)) {
      return;
    }
    Block block = registry.getValue(location);
    API.ORES_REGISTRY.requestOreSetup(block, ore);
    API.ORES_REGISTRY.registerOreOverlay(block, ore.getMeta(), new ResourceLocation(ore.getOverlay()));
  }

  public void requestOreSetups(RegistryEvent.Register<Block> event) {
    for (UBOreConfig ore : this.ores.values()) {
      requestOreSetup(event, ore);
    }
  }

  private void createDefaults() {
    writeDefaults(getMinecraftOres()               , "minecraft.json");
    writeDefaults(getActuallyAdditionsOres()       , "actuallyadditions.json");
    writeDefaults(getAoa3Ores()                    , "aoa3.json");
    writeDefaults(getAppliedEnergisticsOres()      , "appliedenergistics2.json");
    writeDefaults(getAromasDimensionalOres()       , "aromasdimensionalworld.json");
    writeDefaults(getAstralSorceryOres()           , "astralsorcery.json");
    writeDefaults(getBaseMetalsOres()              , "basemetals.json");
    writeDefaults(getBaseMineralsOres()            , "baseminerals.json");
    writeDefaults(getBetterUndergroundOres()       , "betterunderground.json");
    writeDefaults(getBiomesOPlentyOres()           , "biomesoplenty.json");
    writeDefaults(getBiomeYoullGoOres()            , "byg.json");
    writeDefaults(getBluePowerOres()               , "bluepower.json");
    writeDefaults(getCandyWorldOres()              , "candyworld.json");
    writeDefaults(getCommunityModOres()            , "communitymod.json");
    writeDefaults(getCurioOres()                   , "curio.json");
    writeDefaults(getCustomIdeasOres()             , "customideas.json");
    writeDefaults(getDartcraftOres()               , "dartcraftreloaded.json");
    writeDefaults(getDenseMetalsOres()             , "densemetals.json");
    writeDefaults(getDraconicEvolutionOres()       , "draconicevolution.json");
    writeDefaults(getEnderOreOres()                , "enderore.json");
    writeDefaults(getExtremeReactorsOres()         , "extremereactors.json");
    writeDefaults(getFloricraftOres()              , "floricraft.json");
    writeDefaults(getForestyOres()                 , "forestry.json");
    writeDefaults(getGeolosysOres()                , "geolosys.json");
    writeDefaults(getGregtechCEuOres()             , "gtceu.json");
    writeDefaults(getGrimpackOres()                , "grimpack.json");
    writeDefaults(getGrowthcraftOres()             , "growthcraft.json");
    writeDefaults(getImmersiveEngineeringOres()    , "immersiveengineering.json");
    writeDefaults(getIndustrialCraftClassicOres()  , "ic2classic.json");
    writeDefaults(getIndustrialCraftOres()         , "industrialcraft.json");
    writeDefaults(getMatterOverdriveLegacyOres()   , "matteroverdrivelegacy.json");
    writeDefaults(getMekanismOres()                , "mekanism.json");
    writeDefaults(getMetallurgy4Ores()             , "metallurgy4.json");
    writeDefaults(getModernMetalsOres()            , "modernmetals.json");
    writeDefaults(getMoGlowstoneOres()             , "moglowstone.json");
    writeDefaults(getMubbleOres()                  , "mubble.json");
    writeDefaults(getMysticalAgricultureOres()     , "mysticalagriculture.json");
    writeDefaults(getMysticalWorldOres()           , "mysticalworld.json");
    writeDefaults(getNuclearcraftOres()            , "nuclearcraft.json");
    writeDefaults(getPoorMetalsOres()              , "poormetals.json");
    writeDefaults(getProjectRedOres()              , "projectred.json");
    writeDefaults(getPyrotechOres()                , "pyrotech.json");
    writeDefaults(getRailcraftOres()               , "railcraft.json");
    writeDefaults(getRFToolsOres()                 , "rftools.json");
    writeDefaults(getScalingHealthOres()           , "scalinghealth.json");
    writeDefaults(getStarWarsOres()                , "starwars.json");
    writeDefaults(getTaigaOres()                   , "taiga.json");
    writeDefaults(getTechRebornOres()              , "techreborn.json");
    writeDefaults(getThaumcraftOres()              , "thaumcraft.json");
    writeDefaults(getThermalFoundationOres()       , "thermalfoundation.json");
    writeDefaults(getTreasure2Ores()               , "treasure2.json");
    writeDefaults(getWildnatureOres()              , "wildnature.json");
    writeDefaults(getWorldsRetoldOres()            , "worldsretold.json");
  }

  private void writeDefaults(ArrayList<UBOreConfig> ores, String filename) {
    try {
      Path filepath = Paths.get(this.directory.toString(), filename);
      if (!filepath.toFile().exists()) {
        Gson gson = new GsonBuilder()
          .setPrettyPrinting()
          .create();
        String json = gson.toJson(ores, jsonType);
        Files.write(filepath, Arrays.asList(json.split("\n")), StandardCharsets.UTF_8);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setupOres() {
    for (final File fileEntry : directory.listFiles()) {
      if (fileEntry.isFile() && FilenameUtils.getExtension(fileEntry.getPath()).equals("json")) {
        readFile(fileEntry);
      }
    }
  }

  private void readFile(File file) {
    try {
      Gson gson = new Gson();
      String json = FileUtils.readFileToString(file, "UTF8");
      ArrayList<UBOreConfig> ores = gson.fromJson(json, jsonType);
      if (ores != null) {
        for (UBOreConfig ore : ores) {
          if (this.ores.containsKey(ore.toKey())) {
            String message = "Ore " + ore.toKey() + " has already been defined elsewhere!\nFound while checking: " + file.getAbsolutePath();
            if (API.SETTINGS.crashOnProblems()) {
              LOGGER.fatal(message);
            } else {
              LOGGER.warn(message);
            }
          }
          this.ores.put(ore.toKey(), ore);
        }
      } else {
        LOGGER.warn("No ores found in " + file.getPath());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ArrayList<UBOreConfig> getMinecraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("minecraft:diamond_ore", ModInfo.MODID + ":blocks/overlays/minecraft/diamond"));
    ores.add(new UBOreConfig("minecraft:iron_ore", ModInfo.MODID + ":blocks/overlays/minecraft/iron"));
    ores.add(new UBOreConfig("minecraft:coal_ore", ModInfo.MODID + ":blocks/overlays/minecraft/coal"));
    ores.add(new UBOreConfig("minecraft:lapis_ore", ModInfo.MODID + ":blocks/overlays/minecraft/lapis"));
    ores.add(new UBOreConfig("minecraft:redstone_ore", ModInfo.MODID + ":blocks/overlays/minecraft/redstone"));
    ores.add(new UBOreConfig("minecraft:gold_ore", ModInfo.MODID + ":blocks/overlays/minecraft/gold"));
    ores.add(new UBOreConfig("minecraft:emerald_ore", ModInfo.MODID + ":blocks/overlays/minecraft/emerald"));
    return ores;
  }

  private ArrayList<UBOreConfig> getActuallyAdditionsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "actuallyadditions:block_misc";
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/custom/black_quarz"));
    return ores;
  }

  private ArrayList<UBOreConfig> getAoa3Ores() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("aoa3:amethyst_ore", ModInfo.MODID + ":blocks/overlays/aoa3/amethyst_ore"));
    ores.add(new UBOreConfig("aoa3:jade_ore", ModInfo.MODID + ":blocks/overlays/aoa3/jade_ore"));
    ores.add(new UBOreConfig("aoa3:limonite_ore", ModInfo.MODID + ":blocks/overlays/aoa3/limonite_ore"));
    ores.add(new UBOreConfig("aoa3:rosite_ore", ModInfo.MODID + ":blocks/overlays/aoa3/rosite_ore"));
    ores.add(new UBOreConfig("aoa3:runium_ore", ModInfo.MODID + ":blocks/overlays/aoa3/runium_ore"));
    ores.add(new UBOreConfig("aoa3:sapphire_ore", ModInfo.MODID + ":blocks/overlays/aoa3/sapphire_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getAppliedEnergisticsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("appliedenergistics2:quartz_ore", "appliedenergistics2:blocks/charged_quartz_ore_light"));
    ores.add(new UBOreConfig("appliedenergistics2:charged_quartz_ore", "appliedenergistics2:blocks/charged_quartz_ore_light"));
    return ores;
  }

  private ArrayList<UBOreConfig> getAromasDimensionalOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("aroma1997sdimension:miningore", 0, ModInfo.MODID + ":blocks/overlays/aroma1997sdimension/stickyore"));
    ores.add(new UBOreConfig("aroma1997sdimension:miningore", 1, ModInfo.MODID + ":blocks/overlays/aroma1997sdimension/clayore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getAstralSorceryOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("astralsorcery:blockcustomsandore", 1, ModInfo.MODID + ":blocks/overlays/astralsorcery/aquamarine"));
    ores.add(new UBOreConfig("astralsorcery:blockcustomore", 0, ModInfo.MODID + ":blocks/overlays/astralsorcery/rockcrystal"));
    ores.add(new UBOreConfig("astralsorcery:blockcustomore", 1, ModInfo.MODID + ":blocks/overlays/astralsorcery/starmetal"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBaseMetalsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("basemetals:antimony_ore", ModInfo.MODID + ":blocks/overlays/basemetals/antimony"));
    ores.add(new UBOreConfig("basemetals:bismuth_ore", ModInfo.MODID + ":blocks/overlays/basemetals/bismuth"));
    ores.add(new UBOreConfig("basemetals:copper_ore", ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig("basemetals:lead_ore", ModInfo.MODID + ":blocks/overlays/basemetals/lead"));
    ores.add(new UBOreConfig("basemetals:mercury_ore", ModInfo.MODID + ":blocks/overlays/basemetals/mercury"));
    ores.add(new UBOreConfig("basemetals:nickel_ore", ModInfo.MODID + ":blocks/overlays/basemetals/nickel"));
    ores.add(new UBOreConfig("basemetals:platinum_ore", ModInfo.MODID + ":blocks/overlays/basemetals/platinum"));
    ores.add(new UBOreConfig("basemetals:silver_ore", ModInfo.MODID + ":blocks/overlays/thermalfoundation/silver"));
    ores.add(new UBOreConfig("basemetals:tin_ore", ModInfo.MODID + ":blocks/overlays/basemetals/tin"));
    ores.add(new UBOreConfig("basemetals:zinc_ore", ModInfo.MODID + ":blocks/overlays/basemetals/zinc"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBaseMineralsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("baseminerals:lithium_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/lithium"));
    ores.add(new UBOreConfig("baseminerals:niter_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/niter"));
    ores.add(new UBOreConfig("baseminerals:phosphorus_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/phosphorus"));
    ores.add(new UBOreConfig("baseminerals:potash_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/potash"));
    ores.add(new UBOreConfig("baseminerals:salt_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/salt"));
    ores.add(new UBOreConfig("baseminerals:saltpeter_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/saltpeter"));
    ores.add(new UBOreConfig("baseminerals:sulfur_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/sulfur"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBetterUndergroundOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("betterunderground:blockfossils", ModInfo.MODID + ":blocks/overlays/custom/fossil"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBiomesOPlentyOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "biomesoplenty:gem_ore";
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/custom/ruby"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/custom/peridot"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/custom/topaz"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/custom/tanzanite"));
    ores.add(new UBOreConfig(oreName, 5, ModInfo.MODID + ":blocks/overlays/custom/malachite"));
    ores.add(new UBOreConfig(oreName, 6, ModInfo.MODID + ":blocks/overlays/custom/sapphire"));
    ores.add(new UBOreConfig(oreName, 7, ModInfo.MODID + ":blocks/overlays/custom/amber"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBiomeYoullGoOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("byg:fossilstone", ModInfo.MODID + ":blocks/overlays/byg/fossil_stone"));
    ores.add(new UBOreConfig("byg:lathariumore", ModInfo.MODID + ":blocks/overlays/byg/latharium_ore"));
    ores.add(new UBOreConfig("byg:pendoriteore", ModInfo.MODID + ":blocks/overlays/byg/pendorite_ore"));
    ores.add(new UBOreConfig("byg:tamreliteore", ModInfo.MODID + ":blocks/overlays/byg/tamrelite_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBluePowerOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("bluepower:teslatite_ore", ModInfo.MODID + ":blocks/overlays/bluepower/teslatite_ore"));
    ores.add(new UBOreConfig("bluepower:ruby_ore", ModInfo.MODID + ":blocks/overlays/bluepower/ruby_ore"));
    ores.add(new UBOreConfig("bluepower:sapphire_ore", ModInfo.MODID + ":blocks/overlays/bluepower/sapphire_ore"));
    ores.add(new UBOreConfig("bluepower:amethyst_ore", ModInfo.MODID + ":blocks/overlays/bluepower/amethyst_ore"));
    ores.add(new UBOreConfig("bluepower:malachite_ore", ModInfo.MODID + ":blocks/overlays/bluepower/malachite_ore"));
    ores.add(new UBOreConfig("bluepower:copper_ore", ModInfo.MODID + ":blocks/overlays/bluepower/copper_ore"));
    ores.add(new UBOreConfig("bluepower:silver_ore", ModInfo.MODID + ":blocks/overlays/bluepower/silver_ore"));
    ores.add(new UBOreConfig("bluepower:zinc_ore", ModInfo.MODID + ":blocks/overlays/bluepower/zinc_ore"));
    ores.add(new UBOreConfig("bluepower:tungsten_ore", ModInfo.MODID + ":blocks/overlays/bluepower/tungsten_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getCandyWorldOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("candymod:cookie_ore_block", ModInfo.MODID + ":blocks/overlays/candyworld/cookie_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getCommunityModOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("community_mod:squirrel_ore", ModInfo.MODID + ":blocks/overlays/community_mod/squirrel_ore"));
    ores.add(new UBOreConfig("community_mod:poke_ore", ModInfo.MODID + ":blocks/overlays/community_mod/poke_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getCurioOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("curio:blockmysticore", ModInfo.MODID + ":blocks/overlays/curio/mystic_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getCustomIdeasOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("custom:silversteel_ore", ModInfo.MODID + ":blocks/overlays/customideas/silversteel_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getDartcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("dartcraftreloaded:orepower", ModInfo.MODID + ":blocks/overlays/dartcraftreloaded/orepower"));
    return ores;
  }

  private ArrayList<UBOreConfig> getDenseMetalsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("densemetals:dense_antimony_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_antimony_ore"));
    ores.add(new UBOreConfig("densemetals:dense_bismuth_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_bismuth_ore"));
    ores.add(new UBOreConfig("densemetals:dense_coal_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_coal_ore"));
    ores.add(new UBOreConfig("densemetals:dense_copper_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_copper_ore"));
    ores.add(new UBOreConfig("densemetals:dense_diamond_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_diamond_ore"));
    ores.add(new UBOreConfig("densemetals:dense_emerald_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_emerald_ore"));
    ores.add(new UBOreConfig("densemetals:dense_gold_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_gold_ore"));
    ores.add(new UBOreConfig("densemetals:dense_iron_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_iron_ore"));
    ores.add(new UBOreConfig("densemetals:dense_lapis_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_lapis_ore"));
    ores.add(new UBOreConfig("densemetals:dense_lead_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_lead_ore"));
    ores.add(new UBOreConfig("densemetals:dense_mercury_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_mercury_ore"));
    ores.add(new UBOreConfig("densemetals:dense_nickel_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_nickel_ore"));
    ores.add(new UBOreConfig("densemetals:dense_platinum_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_platinum_ore"));
    ores.add(new UBOreConfig("densemetals:dense_redstone_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_redstone_ore"));
    ores.add(new UBOreConfig("densemetals:dense_silver_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_silver_ore"));
    ores.add(new UBOreConfig("densemetals:dense_tin_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_tin_ore"));
    ores.add(new UBOreConfig("densemetals:dense_zinc_ore", ModInfo.MODID + ":blocks/overlays/densemetals/dense_zinc_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getDraconicEvolutionOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("draconicevolution:draconium_ore", 0, ModInfo.MODID + ":blocks/overlays/draconicevolution/draconium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getEnderOreOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("enderore:ore_ender", ModInfo.MODID + ":blocks/overlays/enderore/ender"));
    return ores;
  }

  private ArrayList<UBOreConfig> getExtremeReactorsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("bigreactors:brore", 0, ModInfo.MODID + ":blocks/overlays/bigreactors/yellorite"));
    return ores;
  }

  private ArrayList<UBOreConfig> getFloricraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("floricraft:ore_salt", ModInfo.MODID + ":blocks/overlays/floricraft/ore_salt"));
    return ores;
  }

  private ArrayList<UBOreConfig> getForestyOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "forestry:resources";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/custom/apatite"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/tin"));
    return ores;
  }

  private ArrayList<UBOreConfig> getGeolosysOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("geolosys:ore", 0, ModInfo.MODID + ":blocks/overlays/geolosys/hematite"));
    ores.add(new UBOreConfig("geolosys:ore", 1, ModInfo.MODID + ":blocks/overlays/geolosys/limonite"));
    ores.add(new UBOreConfig("geolosys:ore", 2, ModInfo.MODID + ":blocks/overlays/geolosys/malachite"));
    ores.add(new UBOreConfig("geolosys:ore", 3, ModInfo.MODID + ":blocks/overlays/geolosys/azurite"));
    ores.add(new UBOreConfig("geolosys:ore", 4, ModInfo.MODID + ":blocks/overlays/geolosys/cassiterite"));
    ores.add(new UBOreConfig("geolosys:ore", 5, ModInfo.MODID + ":blocks/overlays/geolosys/teallite"));
    ores.add(new UBOreConfig("geolosys:ore", 6, ModInfo.MODID + ":blocks/overlays/geolosys/galena"));
    ores.add(new UBOreConfig("geolosys:ore", 7, ModInfo.MODID + ":blocks/overlays/geolosys/bauxite"));
    ores.add(new UBOreConfig("geolosys:ore", 8, ModInfo.MODID + ":blocks/overlays/geolosys/platinum"));
    ores.add(new UBOreConfig("geolosys:ore", 9, ModInfo.MODID + ":blocks/overlays/geolosys/autunite"));
    ores.add(new UBOreConfig("geolosys:ore", 10, ModInfo.MODID + ":blocks/overlays/geolosys/sphalerite"));

    ores.add(new UBOreConfig("geolosys:ore_vanilla", 0, ModInfo.MODID + ":blocks/overlays/geolosys/coal"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 1, ModInfo.MODID + ":blocks/overlays/geolosys/cinnabar"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 2, ModInfo.MODID + ":blocks/overlays/geolosys/gold"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 3, ModInfo.MODID + ":blocks/overlays/geolosys/lapis"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 4, ModInfo.MODID + ":blocks/overlays/geolosys/quartz"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 5, ModInfo.MODID + ":blocks/overlays/geolosys/kimberlite"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 6, ModInfo.MODID + ":blocks/overlays/geolosys/beryl"));
    return ores;
  }

  private ArrayList<UBOreConfig> getGregtechCEuOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>(119);
    ores.add(new UBOreConfig("gregtech:ore_almandine_0"            , 0, "gregtech:blocks/material_sets/gem_vertical/ore"  , "#FF0000"));
    ores.add(new UBOreConfig("gregtech:ore_aluminium_0"            , 0, "gregtech:blocks/material_sets/dull/ore"          , "#80C8F0"));
    ores.add(new UBOreConfig("gregtech:ore_alunite_0"              , 0, "gregtech:blocks/material_sets/dull/ore"          , "#E1B441"));
    ores.add(new UBOreConfig("gregtech:ore_amethyst_0"             , 0, "gregtech:blocks/material_sets/ruby/ore"          , "#D232D2"));
    ores.add(new UBOreConfig("gregtech:ore_apatite_0"              , 0, "gregtech:blocks/material_sets/emerald/ore"       , "#C8C8FF"));
    ores.add(new UBOreConfig("gregtech:ore_asbestos_0"             , 0, "gregtech:blocks/material_sets/dull/ore"          , "#E6E6E6"));
    ores.add(new UBOreConfig("gregtech:ore_banded_iron_0"          , 0, "gregtech:blocks/material_sets/dull/ore"          , "#915A5A"));
    ores.add(new UBOreConfig("gregtech:ore_barite_0"               , 0, "gregtech:blocks/material_sets/dull/ore"          , "#E6EBEB"));
    ores.add(new UBOreConfig("gregtech:ore_basaltic_mineral_sand_0", 0, "gregtech:blocks/material_sets/fine/ore"          , "#283228"));
    ores.add(new UBOreConfig("gregtech:ore_bastnasite_0"           , 0, "gregtech:blocks/material_sets/fine/ore"          , "#C86E2D"));
    ores.add(new UBOreConfig("gregtech:ore_bauxite_0"              , 0, "gregtech:blocks/material_sets/rough/ore"         , "#C86400"));
    ores.add(new UBOreConfig("gregtech:ore_bentonite_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#F5D7D2"));
    ores.add(new UBOreConfig("gregtech:ore_beryllium_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#64B464"));
    ores.add(new UBOreConfig("gregtech:ore_bismuth_0"              , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#64A0A0"));
    ores.add(new UBOreConfig("gregtech:ore_blue_topaz_0"           , 0, "gregtech:blocks/material_sets/gem_horizontal/ore", "#7B96DC"));
    ores.add(new UBOreConfig("gregtech:ore_bornite_0"              , 0, "gregtech:blocks/material_sets/dull/ore"          , "#97662B"));
    ores.add(new UBOreConfig("gregtech:ore_fullers_earth_0"        , 0, "gregtech:blocks/material_sets/fine/ore"          , "#A0A078"));
    ores.add(new UBOreConfig("gregtech:ore_brown_limonite_0"       , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#C86400"));
    ores.add(new UBOreConfig("gregtech:ore_calcite_0"              , 0, "gregtech:blocks/material_sets/dull/ore"          , "#FAE6DC"));
    ores.add(new UBOreConfig("gregtech:ore_cassiterite_0"          , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#DCDCDC"));
    ores.add(new UBOreConfig("gregtech:ore_cassiterite_sand_0"     , 0, "gregtech:blocks/material_sets/sand/ore"          , "#DCDCDC"));
    ores.add(new UBOreConfig("gregtech:ore_certus_quartz_0"        , 0, "gregtech:blocks/material_sets/quartz/ore"        , "#D2D2E6"));
    ores.add(new UBOreConfig("gregtech:ore_chalcocite_0"           , 0, "gregtech:blocks/material_sets/gem_vertical/ore"  , "#353535"));
    ores.add(new UBOreConfig("gregtech:ore_chalcopyrite_0"         , 0, "gregtech:blocks/material_sets/dull/ore"          , "#A07828"));
    ores.add(new UBOreConfig("gregtech:ore_chromite_0"             , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#23140F"));
    ores.add(new UBOreConfig("gregtech:ore_cinnabar_0"             , 0, "gregtech:blocks/material_sets/emerald/ore"       , "#960000"));
    ores.add(new UBOreConfig("gregtech:ore_coal_0"                 , 0, "gregtech:blocks/material_sets/lignite/ore"       , "#464646"));
    ores.add(new UBOreConfig("gregtech:ore_cobalt_0"               , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#5050FA"));
    ores.add(new UBOreConfig("gregtech:ore_cobaltite_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#5050FA"));
    ores.add(new UBOreConfig("gregtech:ore_cooperite_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#FFFFC8"));
    ores.add(new UBOreConfig("gregtech:ore_copper_0"               , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#FF6400"));
    ores.add(new UBOreConfig("gregtech:ore_diamond_0"              , 0, "gregtech:blocks/material_sets/diamond/ore"       , "#C8FFFF"));
    ores.add(new UBOreConfig("gregtech:ore_diatomite_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#E1E1E1"));
    ores.add(new UBOreConfig("gregtech:ore_emerald_0"              , 0, "gregtech:blocks/material_sets/emerald/ore"       , "#50FF50"));
    ores.add(new UBOreConfig("gregtech:ore_electrotine_0"          , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#3CB4C8"));
    ores.add(new UBOreConfig("gregtech:ore_galena_0"               , 0, "gregtech:blocks/material_sets/rough/ore"         , "#643C64"));
    ores.add(new UBOreConfig("gregtech:ore_garnet_red_0"           , 0, "gregtech:blocks/material_sets/ruby/ore"          , "#C85050"));
    ores.add(new UBOreConfig("gregtech:ore_garnet_sand_0"          , 0, "gregtech:blocks/material_sets/fine/ore"          , "#C86400"));
    ores.add(new UBOreConfig("gregtech:ore_garnet_yellow_0"        , 0, "gregtech:blocks/material_sets/ruby/ore"          , "#C8C850"));
    ores.add(new UBOreConfig("gregtech:ore_garnierite_0"           , 0, "gregtech:blocks/material_sets/rough/ore"         , "#32C846"));
    ores.add(new UBOreConfig("gregtech:ore_glauconite_sand_0"      , 0, "gregtech:blocks/material_sets/fine/ore"          , "#82B43C"));
    ores.add(new UBOreConfig("gregtech:ore_gold_0"                 , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#FFE650"));
    ores.add(new UBOreConfig("gregtech:ore_granitic_mineral_sand_0", 0, "gregtech:blocks/material_sets/fine/ore"          , "#283C3C"));
    ores.add(new UBOreConfig("gregtech:ore_graphite_0"             , 0, "gregtech:blocks/material_sets/dull/ore"          , "#808080"));
    ores.add(new UBOreConfig("gregtech:ore_green_sapphire_0"       , 0, "gregtech:blocks/material_sets/gem_horizontal/ore", "#64C882"));
    ores.add(new UBOreConfig("gregtech:ore_grossular_0"            , 0, "gregtech:blocks/material_sets/gem_vertical/ore"  , "#C86400"));
    ores.add(new UBOreConfig("gregtech:ore_gypsum_0"               , 0, "gregtech:blocks/material_sets/dull/ore"          , "#E6E6FA"));
    ores.add(new UBOreConfig("gregtech:ore_ilmenite_0"             , 0, "gregtech:blocks/material_sets/rough/ore"         , "#463732"));
    ores.add(new UBOreConfig("gregtech:ore_iridium_0"              , 0, "gregtech:blocks/material_sets/dull/ore"          , "#A1E4E4"));
    ores.add(new UBOreConfig("gregtech:ore_iron_0"                 , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#C8C8C8"));
    ores.add(new UBOreConfig("gregtech:ore_kyanite_0"              , 0, "gregtech:blocks/material_sets/lapis/ore"         , "#6E6EFA"));
    ores.add(new UBOreConfig("gregtech:ore_lapis_0"                , 0, "gregtech:blocks/material_sets/lapis/ore"         , "#4646DC"));
    ores.add(new UBOreConfig("gregtech:ore_lazurite_0"             , 0, "gregtech:blocks/material_sets/lapis/ore"         , "#6478FF"));
    ores.add(new UBOreConfig("gregtech:ore_lead_0"                 , 0, "gregtech:blocks/material_sets/dull/ore"          , "#8C648C"));
    ores.add(new UBOreConfig("gregtech:ore_lepidolite_0"           , 0, "gregtech:blocks/material_sets/fine/ore"          , "#F0328C"));
    ores.add(new UBOreConfig("gregtech:ore_lithium_0"              , 0, "gregtech:blocks/material_sets/dull/ore"          , "#BDC7DB"));
    ores.add(new UBOreConfig("gregtech:ore_magnesite_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#FAFAB4"));
    ores.add(new UBOreConfig("gregtech:ore_magnetite_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#1E1E1E"));
    ores.add(new UBOreConfig("gregtech:ore_malachite_0"            , 0, "gregtech:blocks/material_sets/lapis/ore"         , "#055F05"));
    ores.add(new UBOreConfig("gregtech:ore_mica_0"                 , 0, "gregtech:blocks/material_sets/fine/ore"          , "#C3C3CD"));
    ores.add(new UBOreConfig("gregtech:ore_molybdenite_0"          , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#191919"));
    ores.add(new UBOreConfig("gregtech:ore_molybdenum_0"           , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#B4B4DC"));
    ores.add(new UBOreConfig("gregtech:ore_monazite_0"             , 0, "gregtech:blocks/material_sets/gem_vertical/ore"  , "#324632"));
    ores.add(new UBOreConfig("gregtech:ore_naquadah_0"             , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#323232"));
    ores.add(new UBOreConfig("gregtech:ore_naquadah_enriched_0"    , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#3C3C3C"));
    ores.add(new UBOreConfig("gregtech:ore_neodymium_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#646464"));
    ores.add(new UBOreConfig("gregtech:ore_nether_quartz_0"        , 0, "gregtech:blocks/material_sets/quartz/ore"        , "#E6D2D2"));
    ores.add(new UBOreConfig("gregtech:ore_nickel_0"               , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#C8C8FA"));
    ores.add(new UBOreConfig("gregtech:ore_oilsands_0"             , 0, "gregtech:blocks/material_sets/sand/ore"          , "#0A0A0A"));
    ores.add(new UBOreConfig("gregtech:ore_olivine_0"              , 0, "gregtech:blocks/material_sets/ruby/ore"          , "#96FF96"));
    ores.add(new UBOreConfig("gregtech:ore_opal_0"                 , 0, "gregtech:blocks/material_sets/opal/ore"          , "#0000FF"));
    ores.add(new UBOreConfig("gregtech:ore_osmium_0"               , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#3232FF"));
    ores.add(new UBOreConfig("gregtech:ore_palladium_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#808080"));
    ores.add(new UBOreConfig("gregtech:ore_pentlandite_0"          , 0, "gregtech:blocks/material_sets/rough/ore"         , "#A59605"));
    ores.add(new UBOreConfig("gregtech:ore_phosphate_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#FFFF00"));
    ores.add(new UBOreConfig("gregtech:ore_pitchblende_0"          , 0, "gregtech:blocks/material_sets/rough/ore"         , "#C8D200"));
    ores.add(new UBOreConfig("gregtech:ore_platinum_0"             , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#FFFFC8"));
    ores.add(new UBOreConfig("gregtech:ore_plutonium_0"            , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#F03232"));
    ores.add(new UBOreConfig("gregtech:ore_pollucite_0"            , 0, "gregtech:blocks/material_sets/dull/ore"          , "#F0D2D2"));
    ores.add(new UBOreConfig("gregtech:ore_powellite_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#FFFF00"));
    ores.add(new UBOreConfig("gregtech:ore_pyrite_0"               , 0, "gregtech:blocks/material_sets/rough/ore"         , "#967828"));
    ores.add(new UBOreConfig("gregtech:ore_pyrochlore_0"           , 0, "gregtech:blocks/material_sets/dull/ore"          , "#2B1100"));
    ores.add(new UBOreConfig("gregtech:ore_pyrolusite_0"           , 0, "gregtech:blocks/material_sets/rough/ore"         , "#9696AA"));
    ores.add(new UBOreConfig("gregtech:ore_pyrope_0"               , 0, "gregtech:blocks/material_sets/ruby/ore"          , "#783264"));
    ores.add(new UBOreConfig("gregtech:ore_quartzite_0"            , 0, "gregtech:blocks/material_sets/quartz/ore"        , "#D2E6D2"));
    ores.add(new UBOreConfig("gregtech:ore_realgar_0"              , 0, "gregtech:blocks/material_sets/emerald/ore"       , "#9D2123"));
    ores.add(new UBOreConfig("gregtech:ore_redstone_0"             , 0, "gregtech:blocks/material_sets/rough/ore"         , "#C80000"));
    ores.add(new UBOreConfig("gregtech:ore_rock_salt_0"            , 0, "gregtech:blocks/material_sets/fine/ore"          , "#F0C8C8"));
    ores.add(new UBOreConfig("gregtech:ore_ruby_0"                 , 0, "gregtech:blocks/material_sets/ruby/ore"          , "#FF6464"));
    ores.add(new UBOreConfig("gregtech:ore_rutile_0"               , 0, "gregtech:blocks/material_sets/gem_horizontal/ore", "#D40D5C"));
    ores.add(new UBOreConfig("gregtech:ore_salt_0"                 , 0, "gregtech:blocks/material_sets/sand/ore"          , "#FAFAFA"));
    ores.add(new UBOreConfig("gregtech:ore_saltpeter_0"            , 0, "gregtech:blocks/material_sets/fine/ore"          , "#E6E6E6"));
    ores.add(new UBOreConfig("gregtech:ore_sapphire_0"             , 0, "gregtech:blocks/material_sets/gem_vertical/ore"  , "#6464C8"));
    ores.add(new UBOreConfig("gregtech:ore_scheelite_0"            , 0, "gregtech:blocks/material_sets/dull/ore"          , "#C88C14"));
    ores.add(new UBOreConfig("gregtech:ore_silver_0"               , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#DCDCFF"));
    ores.add(new UBOreConfig("gregtech:ore_soapstone_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#5F915F"));
    ores.add(new UBOreConfig("gregtech:ore_sodalite_0"             , 0, "gregtech:blocks/material_sets/lapis/ore"         , "#1414FF"));
    ores.add(new UBOreConfig("gregtech:ore_spessartine_0"          , 0, "gregtech:blocks/material_sets/gem_vertical/ore"  , "#FF6464"));
    ores.add(new UBOreConfig("gregtech:ore_sphalerite_0"           , 0, "gregtech:blocks/material_sets/rough/ore"         , "#FFFFFF"));
    ores.add(new UBOreConfig("gregtech:ore_spodumene_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#BEAAAA"));
    ores.add(new UBOreConfig("gregtech:ore_stibnite_0"             , 0, "gregtech:blocks/material_sets/rough/ore"         , "#464646"));
    ores.add(new UBOreConfig("gregtech:ore_sulfur_0"               , 0, "gregtech:blocks/material_sets/rough/ore"         , "#C8C800"));
    ores.add(new UBOreConfig("gregtech:ore_talc_0"                 , 0, "gregtech:blocks/material_sets/fine/ore"          , "#5AB45A"));
    ores.add(new UBOreConfig("gregtech:ore_tantalite_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#915028"));
    ores.add(new UBOreConfig("gregtech:ore_tetrahedrite_0"         , 0, "gregtech:blocks/material_sets/rough/ore"         , "#C82000"));
    ores.add(new UBOreConfig("gregtech:ore_thorium_0"              , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#001E00"));
    ores.add(new UBOreConfig("gregtech:ore_tin_0"                  , 0, "gregtech:blocks/material_sets/dull/ore"          , "#DCDCDC"));
    ores.add(new UBOreConfig("gregtech:ore_topaz_0"                , 0, "gregtech:blocks/material_sets/gem_horizontal/ore", "#FF8000"));
    ores.add(new UBOreConfig("gregtech:ore_tricalcium_phosphate_0" , 0, "gregtech:blocks/material_sets/flint/ore"         , "#FFFF00"));
    ores.add(new UBOreConfig("gregtech:ore_trona_0"                , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#87875F"));
    ores.add(new UBOreConfig("gregtech:ore_tungstate_0"            , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#373223"));
    ores.add(new UBOreConfig("gregtech:ore_uraninite_0"            , 0, "gregtech:blocks/material_sets/rough/ore"         , "#232323"));
    ores.add(new UBOreConfig("gregtech:ore_uranium235_0"           , 0, "gregtech:blocks/material_sets/shiny/ore"         , "#46FA46"));
    ores.add(new UBOreConfig("gregtech:ore_uranium_0"              , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#32F032"));
    ores.add(new UBOreConfig("gregtech:ore_vanadium_magnetite_0"   , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#23233C"));
    ores.add(new UBOreConfig("gregtech:ore_wulfenite_0"            , 0, "gregtech:blocks/material_sets/dull/ore"          , "#FF8000"));
    ores.add(new UBOreConfig("gregtech:ore_yellow_limonite_0"      , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#C8C800"));
    ores.add(new UBOreConfig("gregtech:ore_zeolite_0"              , 0, "gregtech:blocks/material_sets/dull/ore"          , "#F0E6E6"));
    ores.add(new UBOreConfig("gregtech:ore_zinc_0"                 , 0, "gregtech:blocks/material_sets/metallic/ore"      , "#EBEBFA"));
    return ores;
  }

  private ArrayList<UBOreConfig> getGrimpackOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("grimpack:aluminum_ore", ModInfo.MODID + ":blocks/overlays/grimpack/aluminum_ore"));
    ores.add(new UBOreConfig("grimpack:oil_ore", ModInfo.MODID + ":blocks/overlays/grimpack/oil_ore"));
    ores.add(new UBOreConfig("grimpack:black_diamond_ore", ModInfo.MODID + ":blocks/overlays/grimpack/black_diamond_ore"));
    ores.add(new UBOreConfig("grimpack:element_115_ore", ModInfo.MODID + ":blocks/overlays/grimpack/element_115_ore"));
    ores.add(new UBOreConfig("grimpack:randomite", ModInfo.MODID + ":blocks/overlays/grimpack/randomite"));
    return ores;
  }

  private ArrayList<UBOreConfig> getGrowthcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("growthcraft:salt_ore", ModInfo.MODID + ":blocks/overlays/growthcraft/salt_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getImmersiveEngineeringOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "immersiveengineering:ore";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/aluminum"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/lead"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/thermalfoundation/silver"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/thermalfoundation/nickel"));
    ores.add(new UBOreConfig(oreName, 5, ModInfo.MODID + ":blocks/overlays/custom/uranium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getIndustrialCraftClassicOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "ic2-classic-spmod:blockmetal";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/thermalfoundation/lead"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/tin"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/custom/uranium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getIndustrialCraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "ic2:resource";
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/lead"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/thermalfoundation/tin"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/custom/uranium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMatterOverdriveLegacyOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("matteroverdrive:dilithium_ore", 0, ModInfo.MODID + ":blocks/overlays/matteroverdrivelegacy/dilithium_ore"));
    ores.add(new UBOreConfig("matteroverdrive:tritanium_ore", 0, ModInfo.MODID + ":blocks/overlays/matteroverdrivelegacy/tritanium_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMekanismOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "mekanism:oreblock";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/mekanism/osmium"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/mekanism/copper"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/mekanism/tin"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMetallurgy4Ores() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("metallurgy:adamantine_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/adamantine_ore"));
    ores.add(new UBOreConfig("metallurgy:astral_silver_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/astral_silver_ore"));
    ores.add(new UBOreConfig("metallurgy:atlarus_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/atlarus_ore"));
    ores.add(new UBOreConfig("metallurgy:bitumen_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/bitumen_ore"));
    ores.add(new UBOreConfig("metallurgy:carmot_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/carmot_ore"));
    ores.add(new UBOreConfig("metallurgy:copper_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/copper_ore"));
    ores.add(new UBOreConfig("metallurgy:deep_iron_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/deep_iron_ore"));
    ores.add(new UBOreConfig("metallurgy:infuscolium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/infuscolium_ore"));
    ores.add(new UBOreConfig("metallurgy:lutetium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/lutetium_ore"));
    ores.add(new UBOreConfig("metallurgy:magnesium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/magnesium_ore"));
    ores.add(new UBOreConfig("metallurgy:manganese_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/manganese_ore"));
    ores.add(new UBOreConfig("metallurgy:mithril_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/mithril_ore"));
    ores.add(new UBOreConfig("metallurgy:orichalcum_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/orichalcum_ore"));
    ores.add(new UBOreConfig("metallurgy:osmium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/osmium_ore"));
    ores.add(new UBOreConfig("metallurgy:oureclase_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/oureclase_ore"));
    ores.add(new UBOreConfig("metallurgy:phosphorite_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/phosphorite_ore"));
    ores.add(new UBOreConfig("metallurgy:platinum_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/platinum_ore"));
    ores.add(new UBOreConfig("metallurgy:potash_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/potash_ore"));
    ores.add(new UBOreConfig("metallurgy:prometheum_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/prometheum_ore"));
    ores.add(new UBOreConfig("metallurgy:rubracium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/rubracium_ore"));
    ores.add(new UBOreConfig("metallurgy:salpeter_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/salpeter_ore"));
    ores.add(new UBOreConfig("metallurgy:silver_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/silver_ore"));
    ores.add(new UBOreConfig("metallurgy:sulfur_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/sulfur_ore"));
    ores.add(new UBOreConfig("metallurgy:tin_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/tin_ore"));
    ores.add(new UBOreConfig("metallurgy:zinc_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/zinc_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getModernMetalsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("modernmetals:aluminum_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/aluminum"));
    ores.add(new UBOreConfig("modernmetals:beryllium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/beryllium"));
    ores.add(new UBOreConfig("modernmetals:boron_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/boron"));
    ores.add(new UBOreConfig("modernmetals:cadmium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/cadmium"));
    ores.add(new UBOreConfig("modernmetals:chromium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/chromium"));
    ores.add(new UBOreConfig("modernmetals:iridium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/iridium"));
    ores.add(new UBOreConfig("modernmetals:magnesium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/magnesium"));
    ores.add(new UBOreConfig("modernmetals:manganese_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/manganese"));
    ores.add(new UBOreConfig("modernmetals:osmium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/osmium"));
    ores.add(new UBOreConfig("modernmetals:plutonium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/plutonium"));
    ores.add(new UBOreConfig("modernmetals:rutile_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/rutile"));
    ores.add(new UBOreConfig("modernmetals:tantalum_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/tantalum"));
    ores.add(new UBOreConfig("modernmetals:titanium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/titanium"));
    ores.add(new UBOreConfig("modernmetals:tungsten_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/tungsten"));
    ores.add(new UBOreConfig("modernmetals:uranium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/uranium"));
    ores.add(new UBOreConfig("modernmetals:zirconium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/zirconium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMoGlowstoneOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("moglowstone:glowstone_ore", ModInfo.MODID + ":blocks/overlays/moglowstone/glowstone_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMubbleOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("mubble:vanadium_ore", ModInfo.MODID + ":blocks/overlays/mubble/vanadium_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMysticalAgricultureOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("mysticalagriculture:inferium_ore", "mysticalagriculture:blocks/inferium_ore"));
    ores.add(new UBOreConfig("mysticalagriculture:prosperity_ore", "mysticalagriculture:blocks/prosperity_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMysticalWorldOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("mysticalworld:amethyst_ore", ModInfo.MODID + ":blocks/overlays/mysticalworld/amethyst"));
    ores.add(new UBOreConfig("mysticalworld:copper_ore", ModInfo.MODID + ":blocks/overlays/mysticalworld/copper"));
    ores.add(new UBOreConfig("mysticalworld:silver_ore", ModInfo.MODID + ":blocks/overlays/mysticalworld/silver"));
    return ores;
  }

  private ArrayList<UBOreConfig> getNuclearcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("nuclearcraft:ore", 0, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_copper"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 1, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_tin"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 2, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_lead"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 3, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_thorium"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 4, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_uranium"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 5, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_boron"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 6, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_lithium"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 7, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_magnesium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getPoorMetalsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("poormetals:poor_aluminum_ore", 0, "poormetals:blocks/overworld/poor_aluminum_ore"));
    ores.add(new UBOreConfig("poormetals:poor_antimony_ore", 0, "poormetals:blocks/overworld/poor_antimony_ore"));
    ores.add(new UBOreConfig("poormetals:poor_beryllium_ore", 0, "poormetals:blocks/overworld/poor_beryllium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_bismuth_ore", 0, "poormetals:blocks/overworld/poor_bismuth_ore"));
    ores.add(new UBOreConfig("poormetals:poor_boron_ore", 0, "poormetals:blocks/overworld/poor_boron_ore"));
    ores.add(new UBOreConfig("poormetals:poor_cadmium_ore", 0, "poormetals:blocks/overworld/poor_cadmium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_chromium_ore", 0, "poormetals:blocks/overworld/poor_chromium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_copper_ore", 0, "poormetals:blocks/overworld/poor_copper_ore"));
    ores.add(new UBOreConfig("poormetals:poor_gold_ore", 0, "poormetals:blocks/overworld/poor_gold_ore"));
    ores.add(new UBOreConfig("poormetals:poor_iridium_ore", 0, "poormetals:blocks/overworld/poor_iridium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_iron_ore", 0, "poormetals:blocks/overworld/poor_iron_ore"));
    ores.add(new UBOreConfig("poormetals:poor_lead_ore", 0, "poormetals:blocks/overworld/poor_lead_ore"));
    ores.add(new UBOreConfig("poormetals:poor_maganese_ore", 0, "poormetals:blocks/overworld/poor_maganese_ore"));
    ores.add(new UBOreConfig("poormetals:poor_magnesium_ore", 0, "poormetals:blocks/overworld/poor_magnesium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_mercury_ore", 0, "poormetals:blocks/overworld/poor_mercury_ore"));
    ores.add(new UBOreConfig("poormetals:poor_nickel_ore", 0, "poormetals:blocks/overworld/poor_nickel_ore"));
    ores.add(new UBOreConfig("poormetals:poor_osmium_ore", 0, "poormetals:blocks/overworld/poor_osmium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_platinum_ore", 0, "poormetals:blocks/overworld/poor_platinum_ore"));
    ores.add(new UBOreConfig("poormetals:poor_plutonium_ore", 0, "poormetals:blocks/overworld/poor_plutonium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_rutile_ore", 0, "poormetals:blocks/overworld/poor_rutile_ore"));
    ores.add(new UBOreConfig("poormetals:poor_silver_ore", 0, "poormetals:blocks/overworld/poor_silver_ore"));
    ores.add(new UBOreConfig("poormetals:poor_tantalum_ore", 0, "poormetals:blocks/overworld/poor_tantalum_ore"));
    ores.add(new UBOreConfig("poormetals:poor_thorium_ore", 0, "poormetals:blocks/overworld/poor_thorium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_tin_ore", 0, "poormetals:blocks/overworld/poor_tin_ore"));
    ores.add(new UBOreConfig("poormetals:poor_titanium_ore", 0, "poormetals:blocks/overworld/poor_titanium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_tungsten_ore", 0, "poormetals:blocks/overworld/poor_tungsten_ore"));
    ores.add(new UBOreConfig("poormetals:poor_uranium_ore", 0, "poormetals:blocks/overworld/poor_uranium_ore"));
    ores.add(new UBOreConfig("poormetals:poor_zinc_ore", 0, "poormetals:blocks/overworld/poor_zinc_ore"));
    ores.add(new UBOreConfig("poormetals:poor_zirconium_ore", 0, "poormetals:blocks/overworld/poor_zirconium_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getProjectRedOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("projectred-exploration:ore", 0, ModInfo.MODID + ":blocks/overlays/projectred/ruby_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 1, ModInfo.MODID + ":blocks/overlays/projectred/sapphire_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 2, ModInfo.MODID + ":blocks/overlays/projectred/peridot_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 3, ModInfo.MODID + ":blocks/overlays/projectred/copper_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 4, ModInfo.MODID + ":blocks/overlays/projectred/tin_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 5, ModInfo.MODID + ":blocks/overlays/projectred/silver_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 6, ModInfo.MODID + ":blocks/overlays/projectred/electrotine_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getPyrotechOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("pyrotech:fossil_ore", ModInfo.MODID + ":blocks/overlays/pyrotech/fossil_ore"));
    ores.add(new UBOreConfig("pyrotech:dense_coal_ore", ModInfo.MODID + ":blocks/overlays/byg/dense_coal_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getRailcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "railcraft:ore_metal";
    String oreNamePoor = "railcraft:ore_metal_poor";
    ores.add(new UBOreConfig(oreName, 0, "railcraft:blocks/ore_copper"));
    ores.add(new UBOreConfig(oreName, 1, "railcraft:blocks/ore_tin"));
    ores.add(new UBOreConfig(oreName, 2, "railcraft:blocks/ore_lead"));
    ores.add(new UBOreConfig(oreName, 3, "railcraft:blocks/ore_silver"));
    ores.add(new UBOreConfig(oreName, 4, "railcraft:blocks/ore_nickel"));
    ores.add(new UBOreConfig(oreName, 5, "railcraft:blocks/ore_zinc"));
    ores.add(new UBOreConfig(oreNamePoor, 0, "railcraft:blocks/ore_poor_iron"));
    ores.add(new UBOreConfig(oreNamePoor, 1, "railcraft:blocks/ore_poor_gold"));
    ores.add(new UBOreConfig(oreNamePoor, 2, "railcraft:blocks/ore_poor_copper"));
    ores.add(new UBOreConfig(oreNamePoor, 3, "railcraft:blocks/ore_poor_tin"));
    ores.add(new UBOreConfig(oreNamePoor, 4, "railcraft:blocks/ore_poor_lead"));
    ores.add(new UBOreConfig(oreNamePoor, 5, "railcraft:blocks/ore_poor_silver"));
    ores.add(new UBOreConfig(oreNamePoor, 6, "railcraft:blocks/ore_poor_nickel"));
    ores.add(new UBOreConfig(oreNamePoor, 7, "railcraft:blocks/ore_poor_zinc"));
    return ores;
  }

  private ArrayList<UBOreConfig> getRFToolsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("rftools:dimensional_shard_ore", 0, ModInfo.MODID + ":blocks/overlays/rftools/dimensionalshard"));
    return ores;
  }

  private ArrayList<UBOreConfig> getScalingHealthOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("scalinghealth:crystalore", ModInfo.MODID + ":blocks/overlays/scalinghealth/crystalore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getStarWarsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("starwars:ore_crystal", ModInfo.MODID + ":blocks/overlays/starwars/ore_crystal"));
    return ores;
  }

  private ArrayList<UBOreConfig> getTaigaOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("taiga:dilithium_ore", 0, ModInfo.MODID + ":blocks/overlays/taiga/dilithium"));
    ores.add(new UBOreConfig("taiga:vibranium_ore", 0, ModInfo.MODID + ":blocks/overlays/taiga/vibranium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getTechRebornOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "techreborn:ore";
    String oreName2 = "techreborn:ore2";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/techreborn/galena"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/techreborn/iridium"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/techreborn/ruby"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/techreborn/sapphire"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/techreborn/bauxite"));
    ores.add(new UBOreConfig(oreName, 12, ModInfo.MODID + ":blocks/overlays/techreborn/lead"));
    ores.add(new UBOreConfig(oreName, 13, ModInfo.MODID + ":blocks/overlays/techreborn/silver"));
    ores.add(new UBOreConfig(oreName2, 0, ModInfo.MODID + ":blocks/overlays/techreborn/copper"));
    ores.add(new UBOreConfig(oreName2, 1, ModInfo.MODID + ":blocks/overlays/techreborn/tin"));
    return ores;
  }

  private ArrayList<UBOreConfig> getThaumcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("thaumcraft:ore_cinnabar", ModInfo.MODID + ":blocks/overlays/thaumcraft/ore_cinnabar"));
    ores.add(new UBOreConfig("thaumcraft:ore_amber", ModInfo.MODID + ":blocks/overlays/thaumcraft/ore_amber"));
    ores.add(new UBOreConfig("thaumcraft:ore_quartz", ModInfo.MODID + ":blocks/overlays/thaumcraft/ore_quartz"));
    return ores;
  }

  private ArrayList<UBOreConfig> getThermalFoundationOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "thermalfoundation:ore";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/tin"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/thermalfoundation/aluminum"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/thermalfoundation/lead"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/silver"));
    ores.add(new UBOreConfig(oreName, 5, ModInfo.MODID + ":blocks/overlays/thermalfoundation/nickel"));
    ores.add(new UBOreConfig(oreName, 6, ModInfo.MODID + ":blocks/overlays/thermalfoundation/platinum"));
    ores.add(new UBOreConfig(oreName, 7, ModInfo.MODID + ":blocks/overlays/thermalfoundation/iridium"));
    ores.add(new UBOreConfig(oreName, 8, ModInfo.MODID + ":blocks/overlays/thermalfoundation/mana_infused"));
    ores.add(new UBOreConfig("thermalfoundation:ore_fluid", 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/destabilized_redstone"));
    ores.add(new UBOreConfig("thermalfoundation:ore_fluid", 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/oil"));
    ores.add(new UBOreConfig("thermalfoundation:ore_fluid", 0, ModInfo.MODID + ":blocks/overlays/thermalfoundation/oil"));
    return ores;
  }

  private ArrayList<UBOreConfig> getTreasure2Ores() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("tresure2:ruby_ore", ModInfo.MODID + ":blocks/overlays/tresure2/ruby_nugget"));
    ores.add(new UBOreConfig("tresure2:sapphire_ore", ModInfo.MODID + ":blocks/overlays/tresure2/sapphire_nugget"));
    return ores;
  }

  private ArrayList<UBOreConfig> getWildnatureOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("wildnature:amethyst_ore", ModInfo.MODID + ":blocks/overlays/wildnature/amethyst_ore"));
    ores.add(new UBOreConfig("wildnature:malachite_ore", ModInfo.MODID + ":blocks/overlays/wildnature/malachite_ore"));
    ores.add(new UBOreConfig("wildnature:ruby_ore", ModInfo.MODID + ":blocks/overlays/wildnature/ruby_ore"));
    ores.add(new UBOreConfig("wildnature:sapphire_ore", ModInfo.MODID + ":blocks/overlays/wildnature/sapphire_ore"));
    ores.add(new UBOreConfig("wildnature:silver_ore", ModInfo.MODID + ":blocks/overlays/wildnature/silver_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getWorldsRetoldOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("worldsretold:cryonite_ore", ModInfo.MODID + ":blocks/overlays/worldsretold/cryonite_ore"));
    return ores;
  }

}
