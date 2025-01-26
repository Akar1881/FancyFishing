package com.fancyfishing.managers;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class FishingItem {
    private final UUID id;
    private final ItemStack item;
    private double chance;
    private int catcherLevel;

    public FishingItem(UUID id, ItemStack item, double chance, int catcherLevel) {
        this.id = id;
        this.item = item;
        this.chance = chance;
        this.catcherLevel = catcherLevel;
    }

    public UUID getId() {
        return id;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getCatcherLevel() {
        return catcherLevel;
    }

    public void setCatcherLevel(int catcherLevel) {
        this.catcherLevel = catcherLevel;
    }
}