package com.willfp.ecoweapons.effects.effects;

import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.ecoweapons.effects.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class EffectArrowStorm extends Effect {
    public EffectArrowStorm() {
        super("arrow-storm");
    }
    @Override
    public void handleMeleeAttack(@NotNull final Player player,
                                  @NotNull final LivingEntity victim,
                                  @NotNull final EntityDamageByEntityEvent event,
                                  @NotNull final JSONConfig args) {
        handle(victim.getLocation(), args);
    }

    @Override
    public void handleProjectileHitEntity(@NotNull final Player player,
                                          @NotNull final LivingEntity victim,
                                          @NotNull final Projectile projectile,
                                          @NotNull final ProjectileHitEvent event,
                                          @NotNull final JSONConfig args) {
        handle(victim.getLocation(), args);
    }

    @Override
    public void handleProjectileHit(@NotNull final Player player,
                                    @NotNull final Projectile projectile,
                                    @NotNull final ProjectileHitEvent event,
                                    @NotNull final JSONConfig args) {
        handle(projectile.getLocation(), args);
    }

    @Override
    public void handleAltClick(@NotNull final Player player,
                               @NotNull final RayTraceResult blockRay,
                               @NotNull final RayTraceResult entityRay,
                               @NotNull final PlayerInteractEvent event,
                               @NotNull final JSONConfig args) {
        handle(blockRay.getHitPosition().toLocation(player.getWorld()), args);
    }

    private void handle(@NotNull final Location location,
                        @NotNull final JSONConfig args) {
        int amount = args.getInt("amount");

        Location apex = location.clone().add(0, 5, 0);

        World world = location.getWorld();
        assert world != null;

        double angle = (Math.PI * 2) / amount;

        for (int i = 0; i < amount; i++) {
            Location spawn = apex.clone();

            spawn.add(
                    Math.sin(angle),
                    0,
                    Math.cos(angle)
            );

            world.spawn(
                    spawn,
                    Arrow.class
            ).setVelocity(new Vector(0, -1, 0));
        }
    }
}
