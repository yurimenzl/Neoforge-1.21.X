package com.ycelaschi.cobblemoncustomspawn;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import com.ycelaschi.cobblemoncustomspawn.util.PokemonSpawnListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import kotlin.Unit;

import java.util.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CobblemonCustomSpawn.MOD_ID)
public class CobblemonCustomSpawn  {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "cobblemonspawncustom";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CobblemonCustomSpawn(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static void initialize() {
        PokemonSpawnListener.onPokemonSpawn(Priority.HIGH, (PokemonEntity pokemonEntity) -> {

            Pokemon originalPokemon = pokemonEntity.getPokemon();

            // Lista de todos os IVs (Stats)
            List<Stats> statsList = new ArrayList<>(Arrays.asList(
                    Stats.HP,
                    Stats.ATTACK,
                    Stats.DEFENCE,
                    Stats.SPECIAL_ATTACK,
                    Stats.SPECIAL_DEFENCE,
                    Stats.SPEED
            ));

            // Embaralha a lista e pega os 3 primeiros para setar como 31
            Collections.shuffle(statsList);
            Set<Stats> maxedIVs = new HashSet<>(statsList.subList(0, 3));

            Random random = new Random();

            for (Stats stat : Stats.values()) {
                if (maxedIVs.contains(stat)) {
                    originalPokemon.setIV(stat, 31);
                } else {
                    originalPokemon.setIV(stat, random.nextInt(32)); // 0 a 31
                }
            }

            pokemonEntity.setPokemon(originalPokemon);

            return Unit.INSTANCE;
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
