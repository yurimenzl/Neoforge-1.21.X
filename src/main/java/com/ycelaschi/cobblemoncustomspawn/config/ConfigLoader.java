package com.ycelaschi.cobblemoncustomspawn.config;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ConfigLoader {
    private static final Path CONFIG_PATH = Paths.get("config/cobblemon_spawn_config.json");

    private static final Map<String, SpeciesConfig> speciesConfigMap = new HashMap<>();

    public static void loadConfig() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                JsonObject group = entry.getValue().getAsJsonObject();
                int ivValue = group.has("iv_value") ? group.get("iv_value").getAsInt() : 31;
                int ivQuantity = group.has("iv_quantity") ? group.get("iv_quantity").getAsInt() : 3;

                JsonArray speciesArray = group.getAsJsonArray("species_list");
                for (JsonElement speciesElement : speciesArray) {
                    String speciesName = speciesElement.getAsString().toLowerCase();
                    speciesConfigMap.put(speciesName, new SpeciesConfig(ivValue, ivQuantity));
                }
            }

            System.out.println("[CobblemonSpawner] Configuração carregada com sucesso!");
        } catch (IOException e) {
            System.err.println("[CobblemonSpawner] Erro ao carregar configuração: " + e.getMessage());
        }
    }

    public static SpeciesConfig getSpeciesConfig(String speciesName) {
        return speciesConfigMap.get(speciesName.toLowerCase());
    }

    public static boolean isSpeciesAllowed(String speciesName) {
        return speciesConfigMap.containsKey(speciesName.toLowerCase());
    }
}

