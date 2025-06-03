package com.ycelaschi.cobblemoncustomspawn;


import com.ycelaschi.cobblemoncustomspawn.config.ConfigLoader;
import com.ycelaschi.cobblemoncustomspawn.config.SpeciesConfig;

import org.jline.utils.Log;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import kotlin.Unit;

import com.cobblemon.mod.common.pokemon.properties.HiddenAbilityProperty;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.ycelaschi.cobblemoncustomspawn.util.PokemonSpawnListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// The value here should match an entry in the META-INF/neoforge.neoforge.mods.toml file
@Mod(CobblemonSpawnCustom.MOD_ID)
public class CobblemonSpawnCustom  {
    public static final String MOD_ID = "cobblemonspawncustom";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CobblemonSpawnCustom(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        initialize();
    }

    public static void initialize() {
        ConfigLoader.loadConfig();

        PokemonSpawnListener.onPokemonSpawn(Priority.HIGH, (PokemonEntity pokemonEntity) -> {
            Pokemon originalPokemon = pokemonEntity.getPokemon();
            String speciesName = originalPokemon.getSpecies().getName().toLowerCase();
            SpeciesConfig config = ConfigLoader.getSpeciesConfig(speciesName);

            if (config != null) {
                int ivValue = config.ivValue;
                int ivQuantity = config.ivQuantity;
                double shinyChance = config.shinyChance;
                double haChance = config.haChance;

                System.out.println("Modificando IVs de: " + speciesName);
                System.out.println("IV Value: " + ivValue + " | IV Quantity: " + ivQuantity);

                List<Stats> allStats = Arrays.asList(
                        Stats.HP, Stats.ATTACK, Stats.DEFENCE,
                        Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED
                );

                Collections.shuffle(allStats);
                List<Stats> selectedStats = allStats.subList(0, ivQuantity);
                List<Stats> remainingStats = allStats.subList(ivQuantity, allStats.size());

                for (Stats stat : selectedStats) {
                    originalPokemon.setIV(stat, ivValue);
                    Log.info("Set IV for: " + stat.name() + "for " + speciesName);
                }

                for (Stats stat : remainingStats) {
                    Random rand = new Random();
                    int randomIv = rand.nextInt(32); // de 0 a 31
                    originalPokemon.setIV(stat, randomIv);
                    Log.info("Set RANDOM IV " + randomIv + " for: " + stat.name());
                }

                Random randomShiny = new Random();
                double shinyRandom = randomShiny.nextDouble();
                LOGGER.info("Set SHINY RAND {} for: {}", shinyRandom, shinyChance);

                if ( shinyRandom < shinyChance) {
                    originalPokemon.setShiny(true);
                }

                Random randomHa = new Random();
                double haRandom = randomHa.nextDouble();
                LOGGER.info("Set HA RAND {} for: {}", haRandom, haChance);

                if ( haRandom < haChance) {
                    new HiddenAbilityProperty(true).apply(originalPokemon);
                }

                pokemonEntity.setPokemon(originalPokemon);
            } else {
               // Log.info("Espécie " + speciesName + " não configurada. Ignorando.");
            }

            return Unit.INSTANCE;
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(CobblemonSpawnCustom::initialize);
    }
}
