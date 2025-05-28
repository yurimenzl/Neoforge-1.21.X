package com.ycelaschi.cobblemoncustomspawn.config;

public class SpeciesConfig {
    public final int ivValue;
    public final int ivQuantity;

    public SpeciesConfig(int ivValue, int ivQuantity) {
        this.ivValue = ivValue;
        this.ivQuantity = Math.max(1, Math.min(ivQuantity, 6));
    }
}
