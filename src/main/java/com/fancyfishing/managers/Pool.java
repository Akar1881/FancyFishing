package com.fancyfishing.managers;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pool {
    private final String name;
    private final Location pos1;
    private final Location pos2;
    private final int catcherLevel;
    private final Map<UUID, FishingItem> items;

    public Pool(String name, Location pos1, Location pos2, int catcherLevel) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.catcherLevel = catcherLevel;
        this.items = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public int getCatcherLevel() {
        return catcherLevel;
    }

    public Map<UUID, FishingItem> getItems() {
        return new HashMap<>(items);
    }

    public void addItem(FishingItem item) {
        items.put(item.getId(), item);
    }

    public void removeItem(UUID id) {
        items.remove(id);
    }

    public FishingItem getItem(UUID id) {
        return items.get(id);
    }

    public boolean isInPool(Location location) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }
}