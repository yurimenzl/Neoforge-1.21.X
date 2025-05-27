package com.ycelaschi.cobblemoncustomspawn;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ConfigLoader {
    private static final Path CONFIG_PATH = Paths.get("config/cobblemon_spawn_config.json");

    private static int ivValue = 31;
    private static int ivQuantity = 3;
    private static Set<String> speciesList = new HashSet<>();

    public static void loadConfig() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            ivValue = json.has("iv_value") ? json.get("iv_value").getAsInt() : 31;
            ivQuantity = json.has("iv_quantity") ? json.get("iv_quantity").getAsInt() : 3;

            JsonArray array = json.getAsJsonArray("species_list");
            Set<String> loadedSpecies = new HashSet<>();
            for (JsonElement element : array) {
                loadedSpecies.add(element.getAsString().toLowerCase());
            }
            speciesList = loadedSpecies;

            System.out.println("[CobblemonSpawner] Configuração carregada com sucesso!");
        } catch (IOException e) {
            System.err.println("[CobblemonSpawner] Erro ao carregar configuração: " + e.getMessage());
        }
    }

    public static int getIvValue() {
        return ivValue;
    }

    public static int getIvQuantity() {
        return Math.max(1, Math.min(ivQuantity, 6));
    }

    public static boolean isSpeciesAllowed(String speciesName) {
        return speciesList.contains(speciesName.toLowerCase());
    }
}

