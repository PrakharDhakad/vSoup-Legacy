/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R1.AxisAlignedBB
 *  net.minecraft.server.v1_8_R1.Block
 *  net.minecraft.server.v1_8_R1.BlockPosition
 *  net.minecraft.server.v1_8_R1.Entity
 *  net.minecraft.server.v1_8_R1.EntityHuman
 *  net.minecraft.server.v1_8_R1.EntityLiving
 *  net.minecraft.server.v1_8_R1.EnumDirection
 *  net.minecraft.server.v1_8_R1.IBlockData
 *  net.minecraft.server.v1_8_R1.IProjectile
 *  net.minecraft.server.v1_8_R1.MathHelper
 *  net.minecraft.server.v1_8_R1.MovingObjectPosition
 *  net.minecraft.server.v1_8_R1.Vec3D
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.craftbukkit.v1_8_R1.block.CraftBlock
 *  org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity
 *  org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 */
package kami.gg.souppvp.util.projectile.projectile;

import kami.gg.souppvp.util.projectile.TypedRunnable;
import kami.gg.souppvp.util.projectile.event.CustomProjectileHitEvent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProjectileScheduler implements Runnable, IProjectile, CustomProjectile<ProjectileScheduler> {

    private final String name;
    private final EntityLiving shooter;
    private final net.minecraft.server.v1_8_R3.Entity e;
    private final int id;
    private final List<Runnable> runnables = new ArrayList<Runnable>();
    private final List<TypedRunnable<ProjectileScheduler>> typedRunnables = new ArrayList<TypedRunnable<ProjectileScheduler>>();
    private Random random;
    private int age;
    private int knockback;
    private ArrayList<Material> ignoredMaterials = new ArrayList();
    private Field f;

    public ProjectileScheduler(String name, Entity e, LivingEntity shooter, float power, Plugin plugin) {
        this.name = name;
        this.shooter = ((CraftLivingEntity)shooter).getHandle();
        this.e = ((CraftEntity)e).getHandle();
        try {
            Field f = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("random");
            f.setAccessible(true);
            this.random = (Random)f.get((Object)this.e);
        }
        catch (IllegalAccessException | NoSuchFieldException | SecurityException t) {
            throw new RuntimeException(t);
        }
        this.e.setPositionRotation(shooter.getLocation().getX(), shooter.getLocation().getY(), shooter.getLocation().getZ(), shooter.getLocation().getYaw(), shooter.getLocation().getPitch());
        this.e.locX -= (double)(MathHelper.cos((float)(this.e.yaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.e.locY += 1.5;
        this.e.locZ -= (double)(MathHelper.sin((float)(this.e.yaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.e.setPosition(this.e.locX, this.e.locY, this.e.locZ);
        float f = 0.4f;
        this.e.motX = -MathHelper.sin((float)(this.e.yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.e.pitch / 180.0f * (float)Math.PI)) * f;
        this.e.motZ = MathHelper.cos((float)(this.e.yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.e.pitch / 180.0f * (float)Math.PI)) * f;
        this.e.motY = -MathHelper.sin((float)(this.e.pitch / 180.0f * (float)Math.PI)) * f;
        this.shoot(this.e.motX, this.e.motY, this.e.motZ, power * 1.5f, 1.0f);
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, (Runnable)this, 1L, 1L);
        try {
            this.f = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("invulnerable");
        }
        catch (NoSuchFieldException er) {
            er.printStackTrace();
        }
    }

    @Override
    public void run() {
        AxisAlignedBB axisalignedbb;
        BlockPosition blockposition = new BlockPosition(this.e.locX, this.e.locY, this.e.locZ);
        IBlockData iblockdata = this.e.world.getType(blockposition);
        Block block = iblockdata.getBlock();
        if (!this.ignoredMaterials.contains((Object)Material.getMaterial((int)Block.getId((Block)block))) && (axisalignedbb = block.a(this.e.world, blockposition, iblockdata)) != null && axisalignedbb.a(new Vec3D(this.e.locX, this.e.locY, this.e.locZ))) {
            float damageMultiplier = MathHelper.sqrt((double)(this.e.motX * this.e.motX + this.e.motY * this.e.motY + this.e.motZ * this.e.motZ));
            CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, damageMultiplier, this.e.world.getWorld().getBlockAt((int)this.e.locX, (int)this.e.locY, (int)this.e.locZ), BlockFace.UP);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                this.e.die();
                Bukkit.getScheduler().cancelTask(this.id);
            }
        }
        ++this.age;
        Vec3D vec3d = new Vec3D(this.e.locX, this.e.locY, this.e.locZ);
        Vec3D vec3d1 = new Vec3D(this.e.locX + this.e.motX, this.e.locY + this.e.motY, this.e.locZ + this.e.motZ);
        MovingObjectPosition movingobjectposition = this.e.world.rayTrace(vec3d, vec3d1, false, true, false);
        vec3d = new Vec3D(this.e.locX, this.e.locY, this.e.locZ);
        vec3d1 = new Vec3D(this.e.locX + this.e.motX, this.e.locY + this.e.motY, this.e.locZ + this.e.motZ);
        if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
        }
        net.minecraft.server.v1_8_R3.Entity entity = null;
        List list = this.e.world.getEntities(this.e, this.e.getBoundingBox().a(this.e.motX, this.e.motY, this.e.motZ).grow(1.0, 1.0, 1.0));
        double d0 = 0.0;
        for (Object aList : list) {
            double d1;
            net.minecraft.server.v1_8_R3.Entity entity1 = (net.minecraft.server.v1_8_R3.Entity)aList;
            if (!entity1.ad() || entity1 == this.shooter && this.age < 5) continue;
            float f = 0.3f;
            AxisAlignedBB axisalignedbb1 = entity1.getBoundingBox().grow((double)f, (double)f, (double)f);
            MovingObjectPosition movingobjectposition1 = axisalignedbb1.a(vec3d, vec3d1);
            if (movingobjectposition1 == null || !((d1 = vec3d.distanceSquared(movingobjectposition1.pos)) < d0) && d0 != 0.0) continue;
            entity = entity1;
            d0 = d1;
        }
        if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
        }
        if (movingobjectposition != null && movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman)movingobjectposition.entity;
            if (entityhuman.abilities.isInvulnerable || this.shooter instanceof EntityHuman && !((EntityHuman)this.shooter).a(entityhuman)) {
                movingobjectposition = null;
            }
        }
        if (movingobjectposition != null) {
            if (movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityLiving) {
                float damageMultiplier = MathHelper.sqrt((double)(this.e.motX * this.e.motX + this.e.motY * this.e.motY + this.e.motZ * this.e.motZ));
                CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, damageMultiplier, (LivingEntity)movingobjectposition.entity.getBukkitEntity());
                Bukkit.getPluginManager().callEvent((Event)event);
                if (!event.isCancelled()) {
                    float f4;
                    if (this.getKnockback() > 0 && (f4 = MathHelper.sqrt((double)(this.e.motX * this.e.motX + this.e.motZ * this.e.motZ))) > 0.0f) {
                        movingobjectposition.entity.g(this.e.motX * (double)this.getKnockback() * (double)0.6f / (double)f4, 0.1, this.e.motZ * (double)this.getKnockback() * (double)0.6f / (double)f4);
                    }
                    this.e.die();
                    Bukkit.getScheduler().cancelTask(this.id);
                }
            } else if (movingobjectposition.a() != null && !this.ignoredMaterials.contains((Object)Material.getMaterial((int)Block.getId((Block)block)))) {
                this.e.motX = (float)(movingobjectposition.pos.a - this.e.locX);
                this.e.motY = (float)(movingobjectposition.pos.b - this.e.locY);
                this.e.motZ = (float)(movingobjectposition.pos.c - this.e.locZ);
                float f3 = MathHelper.sqrt((double)(this.e.motX * this.e.motX + this.e.motY * this.e.motY + this.e.motZ * this.e.motZ));
                this.e.locX -= this.e.motX / (double)f3 * 0.0500000007450581;
                this.e.locY -= this.e.motY / (double)f3 * 0.0500000007450581;
                this.e.locZ -= this.e.motZ / (double)f3 * 0.0500000007450581;
                float damageMultiplier = MathHelper.sqrt((double)(this.e.motX * this.e.motX + this.e.motY * this.e.motY + this.e.motZ * this.e.motZ));
                CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, damageMultiplier, this.e.world.getWorld().getBlockAt((int)movingobjectposition.pos.a, (int)movingobjectposition.pos.b, (int)movingobjectposition.pos.c), CraftBlock.notchToBlockFace((EnumDirection)movingobjectposition.direction));
                Bukkit.getPluginManager().callEvent((Event)event);
                if (!event.isCancelled()) {
                    this.e.die();
                    Bukkit.getScheduler().cancelTask(this.id);
                }
            }
        }
        this.e.locX += this.e.motX;
        this.e.locY += this.e.motY;
        this.e.locZ += this.e.motZ;
        float f3 = 0.99f;
        float f1 = 0.05f;
        this.e.motX *= (double)f3;
        this.e.motY *= (double)f3;
        this.e.motZ *= (double)f3;
        this.e.motY -= (double)f1;
        this.e.setPosition(this.e.locX, this.e.locY, this.e.locZ);
        if (this.e.isAlive()) {
            if (this.age >= 1000) {
                this.e.die();
                Bukkit.getScheduler().cancelTask(this.id);
            }
            for (Runnable runnable : this.runnables) {
                runnable.run();
            }
            for (TypedRunnable typedRunnable : this.typedRunnables) {
                typedRunnable.run(this);
            }
        } else {
            Bukkit.getScheduler().cancelTask(this.id);
        }
    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt((double)(d0 * d0 + d1 * d1 + d2 * d2));
        d0 /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        d0 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        d1 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        d2 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        this.e.motX = d0 *= (double)f;
        this.e.motY = d1 *= (double)f;
        this.e.motZ = d2 *= (double)f;
        float f3 = MathHelper.sqrt((double)(d0 * d0 + d2 * d2));
        this.e.lastYaw = this.e.yaw = (float)(Math.atan2(d0, d2) * 180.0 / 3.1415927410125732);
        this.e.lastPitch = this.e.pitch = (float)(Math.atan2(d1, f3) * 180.0 / 3.1415927410125732);
    }

    @Override
    public EntityType getEntityType() {
        return this.e.getBukkitEntity().getType();
    }

    @Override
    public Entity getEntity() {
        return this.e.getBukkitEntity();
    }

    @Override
    public LivingEntity getShooter() {
        return (LivingEntity)this.shooter.getBukkitEntity();
    }

    @Override
    public String getProjectileName() {
        return this.name;
    }

    @Override
    public boolean isInvulnerable() {
        return this.getEntity().spigot().isInvulnerable();
    }

    @Override
    public void setInvulnerable(boolean value) {
        try {
            this.f.setAccessible(true);
            this.f.set(this, value);
        }
        catch (IllegalAccessException | SecurityException t) {
            t.printStackTrace();
        }
    }

    @Override
    public void addRunnable(Runnable r) {
        this.runnables.add(r);
    }

    @Override
    public void removeRunnable(Runnable r) {
        this.runnables.remove(r);
    }

    @Override
    public void addTypedRunnable(TypedRunnable<ProjectileScheduler> r) {
        this.typedRunnables.add(r);
    }

    @Override
    public void removeTypedRunnable(TypedRunnable<ProjectileScheduler> r) {
        this.typedRunnables.remove(r);
    }

    @Override
    public ArrayList<Material> getIgnoredBlocks() {
        return this.ignoredMaterials;
    }

    @Override
    public int getKnockback() {
        return this.knockback;
    }

    @Override
    public void setKnockback(int i) {
        this.knockback = i;
    }
}

