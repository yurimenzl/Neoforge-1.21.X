package com.ycelaschi.cobblemoncustomspawn;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType;
import com.cobblemon.mod.common.pokemon.properties.HiddenAbilityProperty;
import com.mojang.logging.LogUtils;
import com.ycelaschi.cobblemoncustomspawn.config.ConfigLoader;
import com.ycelaschi.cobblemoncustomspawn.config.SpeciesConfig;
import com.ycelaschi.cobblemoncustomspawn.util.PokemonSpawnListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import kotlin.Unit;

import java.util.*;
import java.util.stream.Collectors;

@Mod(CobblemonSpawnCustom.MOD_ID)
public class CobblemonSpawnCustom  {
    public static final String MOD_ID = "cobblemonspawncustom";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CobblemonSpawnCustom(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
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

                Random randomShiny = new Random();
                double shinyRandom = randomShiny.nextDouble();

                if ( shinyRandom < shinyChance) {
                    originalPokemon.setShiny(true);
                }

                Random randomHa = new Random();
                double haRandom = randomHa.nextDouble();

                if ( haRandom < haChance) {
                    new HiddenAbilityProperty(true).apply(originalPokemon);
                }

                Collections.shuffle(allStats);
                List<Stats> selectedStats = allStats.subList(0, ivQuantity);

                for (Stats stat : selectedStats) {
                    originalPokemon.setIV(stat, ivValue);
                    System.out.println("Set IV para " + stat.name());
                }

                pokemonEntity.setPokemon(originalPokemon);
            } else {
                System.out.println("Espécie " + speciesName + " não configurada. Ignorando.");
            }

            return Unit.INSTANCE;
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CobblemonSpawnCustom::initialize);
    }
}
