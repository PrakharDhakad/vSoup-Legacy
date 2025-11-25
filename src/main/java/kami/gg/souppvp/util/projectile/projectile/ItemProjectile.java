/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R1.AxisAlignedBB
 *  net.minecraft.server.v1_8_R1.Block
 *  net.minecraft.server.v1_8_R1.BlockPosition
 *  net.minecraft.server.v1_8_R1.Blocks
 *  net.minecraft.server.v1_8_R1.Entity
 *  net.minecraft.server.v1_8_R1.EntityHuman
 *  net.minecraft.server.v1_8_R1.EntityItem
 *  net.minecraft.server.v1_8_R1.EntityLiving
 *  net.minecraft.server.v1_8_R1.EnumDirection
 *  net.minecraft.server.v1_8_R1.IBlockData
 *  net.minecraft.server.v1_8_R1.IProjectile
 *  net.minecraft.server.v1_8_R1.Item
 *  net.minecraft.server.v1_8_R1.ItemStack
 *  net.minecraft.server.v1_8_R1.MathHelper
 *  net.minecraft.server.v1_8_R1.MovingObjectPosition
 *  net.minecraft.server.v1_8_R1.Vec3D
 *  net.minecraft.server.v1_8_R1.World
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.craftbukkit.v1_8_R1.CraftWorld
 *  org.bukkit.craftbukkit.v1_8_R1.block.CraftBlock
 *  org.bukkit.craftbukkit.v1_8_R1.entity.CraftHumanEntity
 *  org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity
 *  org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.ItemStack
 */
package kami.gg.souppvp.util.projectile.projectile;

import kami.gg.souppvp.util.projectile.TypedRunnable;
import kami.gg.souppvp.util.projectile.event.ItemProjectileHitEvent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemProjectile extends EntityItem implements IProjectile, CustomProjectile<ItemProjectile> {

    private final EntityLiving shooter;
    private final String name;
    private final List<Runnable> runnables = new ArrayList<Runnable>();
    private final List<TypedRunnable<ItemProjectile>> typedRunnables = new ArrayList<TypedRunnable<ItemProjectile>>();
    private int knockback;
    private int age;
    private ArrayList<Material> ignoredMaterials = new ArrayList();
    private Field f;

    public ItemProjectile(String name, Location loc, ItemStack itemstack, LivingEntity shooter, float power) {
        super((World)((CraftWorld)loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), null);
        if (CraftItemStack.asNMSCopy((ItemStack)itemstack) != null) {
            this.setItemStack(CraftItemStack.asNMSCopy((ItemStack)itemstack));
        } else {
            this.setItemStack(new net.minecraft.server.v1_8_R3.ItemStack(Item.getById((int)itemstack.getTypeId()), itemstack.getAmount(), (int)itemstack.getData().getData()));
        }
        if (itemstack.getTypeId() == 0) {
            System.out.println("You cannot shoot air!");
        }
        this.name = name;
        this.pickupDelay = Integer.MAX_VALUE;
        this.shooter = ((CraftLivingEntity)shooter).getHandle();
        this.a(0.25f, 0.25f);
        this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.locX -= (double)(MathHelper.cos((float)(this.yaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.locY -= (double)0.1f;
        this.locZ -= (double)(MathHelper.sin((float)(this.yaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.setPosition(this.locX, this.locY, this.locZ);
        float f = 0.4f;
        this.motX = -MathHelper.sin((float)(this.yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.pitch / 180.0f * (float)Math.PI)) * f;
        this.motZ = MathHelper.cos((float)(this.yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.pitch / 180.0f * (float)Math.PI)) * f;
        this.motY = -MathHelper.sin((float)(this.pitch / 180.0f * (float)Math.PI)) * f;
        this.shoot(this.motX, this.motY, this.motZ, power * 1.5f, 1.0f);
        this.world.addEntity(this);
        try {
            this.f = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("invulnerable");
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public ItemProjectile(String name, LivingEntity shooter, ItemStack item, float power) {
        super(((CraftLivingEntity)shooter).getHandle().world);
        this.name = name;
        this.pickupDelay = Integer.MAX_VALUE;
        this.setItemStack(CraftItemStack.asNMSCopy((ItemStack)item));
        this.shooter = ((CraftLivingEntity)shooter).getHandle();
        this.a(0.25f, 0.25f);
        this.setPositionRotation(shooter.getLocation().getX(), shooter.getLocation().getY() + shooter.getEyeHeight(), shooter.getLocation().getZ(), shooter.getLocation().getYaw(), shooter.getLocation().getPitch());
        this.locX -= (double)(MathHelper.cos((float)(this.yaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.locY -= (double)0.1f;
        this.locZ -= (double)(MathHelper.sin((float)(this.yaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.setPosition(this.locX, this.locY, this.locZ);
        float f = 0.4f;
        this.motX = -MathHelper.sin((float)(this.yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.pitch / 180.0f * (float)Math.PI)) * f;
        this.motZ = MathHelper.cos((float)(this.yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.pitch / 180.0f * (float)Math.PI)) * f;
        this.motY = -MathHelper.sin((float)(this.pitch / 180.0f * (float)Math.PI)) * f;
        this.shoot(this.motX, this.motY, this.motZ, power * 1.5f, 1.0f);
        this.world.addEntity(this);
        try {
            this.f = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("invulnerable");
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public boolean s_() {
        AxisAlignedBB axisalignedbb;
        this.K();
        BlockPosition blockposition = new BlockPosition(this.locX, this.locY, this.locZ);
        IBlockData iblockdata = this.world.getType(blockposition);
        Block block = iblockdata.getBlock();
        if (!this.ignoredMaterials.contains((Object)Material.getMaterial((int)Block.getId((Block)block))) && (axisalignedbb = block.a(this.world, blockposition, iblockdata)) != null && axisalignedbb.a(new Vec3D(this.locX, this.locY, this.locZ))) {
            float damageMultiplier = MathHelper.sqrt((double)(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ));
            ItemProjectileHitEvent event = new ItemProjectileHitEvent(this, damageMultiplier, this.world.getWorld().getBlockAt((int)this.locX, (int)this.locY, (int)this.locZ), BlockFace.UP, this.getItem());
            Bukkit.getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                this.die();
            }
        }
        ++this.age;
        Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true, false);
        vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
        }
        net.minecraft.server.v1_8_R3.Entity entity = null;
        List list = this.world.getEntities(this, this.getBoundingBox().a(this.motX, this.motY, this.motZ).grow(1.0, 1.0, 1.0));
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
                float damageMultiplier = MathHelper.sqrt((double)(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ));
                ItemProjectileHitEvent event = new ItemProjectileHitEvent((CustomProjectile)this, damageMultiplier, (LivingEntity)movingobjectposition.entity.getBukkitEntity(), this.getItem());
                Bukkit.getPluginManager().callEvent((Event)event);
                if (!event.isCancelled()) {
                    float f4;
                    if (this.getKnockback() > 0 && (f4 = MathHelper.sqrt((double)(this.motX * this.motX + this.motZ * this.motZ))) > 0.0f) {
                        movingobjectposition.entity.g(this.motX * (double)this.getKnockback() * (double)0.6f / (double)f4, 0.1, this.motZ * (double)this.getKnockback() * (double)0.6f / (double)f4);
                    }
                    this.die();
                }
            } else if (movingobjectposition.a() != null && !this.ignoredMaterials.contains((Object)Material.getMaterial((int)Block.getId((Block)block)))) {
                this.motX = (float)(movingobjectposition.pos.a - this.locX);
                this.motY = (float)(movingobjectposition.pos.b - this.locY);
                this.motZ = (float)(movingobjectposition.pos.c - this.locZ);
                float f3 = MathHelper.sqrt((double)(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ));
                this.locX -= this.motX / (double)f3 * 0.0500000007450581;
                this.locY -= this.motY / (double)f3 * 0.0500000007450581;
                this.locZ -= this.motZ / (double)f3 * 0.0500000007450581;
                float damageMultiplier = MathHelper.sqrt((double)(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ));
                ItemProjectileHitEvent event = new ItemProjectileHitEvent(this, damageMultiplier, this.world.getWorld().getBlockAt((int)movingobjectposition.pos.a, (int)movingobjectposition.pos.b, (int)movingobjectposition.pos.c), CraftBlock.notchToBlockFace((EnumDirection)movingobjectposition.direction), this.getItem());
                Bukkit.getPluginManager().callEvent((Event)event);
                if (!event.isCancelled()) {
                    this.die();
                }
            }
        }
        this.locX += this.motX;
        this.locY += this.motY;
        this.locZ += this.motZ;
        float f3 = 0.99f;
        float f1 = 0.05f;
        this.motX *= (double)f3;
        this.motY *= (double)f3;
        this.motZ *= (double)f3;
        this.motY -= (double)f1;
        this.setPosition(this.locX, this.locY, this.locZ);
        this.checkBlockCollisions();
        if (this.isAlive()) {
            if (this.age >= 1000) {
                this.die();
            }
            for (Runnable runnable : this.runnables) {
                runnable.run();
            }
            for (TypedRunnable typedRunnable : this.typedRunnables) {
                typedRunnable.run(this);
            }
        }
        return false;
    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt((double)(d0 * d0 + d1 * d1 + d2 * d2));
        d0 /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        d0 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        d1 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        d2 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        this.motX = d0 *= (double)f;
        this.motY = d1 *= (double)f;
        this.motZ = d2 *= (double)f;
        float f3 = MathHelper.sqrt((double)(d0 * d0 + d2 * d2));
        this.lastYaw = this.yaw = (float)(Math.atan2(d0, d2) * 180.0 / 3.1415927410125732);
        this.lastPitch = this.pitch = (float)(Math.atan2(d1, f3) * 180.0 / 3.1415927410125732);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DROPPED_ITEM;
    }

    @Override
    public Entity getEntity() {
        return this.getBukkitEntity();
    }

    @Override
    public LivingEntity getShooter() {
        return (LivingEntity)this.shooter.getBukkitEntity();
    }

    public void d(EntityHuman entityhuman) {
        if (entityhuman == this.shooter && this.age <= 3) {
            return;
        }
        CraftHumanEntity living = entityhuman.getBukkitEntity();
        ItemProjectileHitEvent event = new ItemProjectileHitEvent((CustomProjectile)this, 0.5f, (LivingEntity)living, this.getItem());
        Bukkit.getPluginManager().callEvent((Event)event);
        if (!event.isCancelled()) {
            this.die();
        }
    }

    @Override
    public String getProjectileName() {
        return this.name;
    }

    public ItemStack getItem() {
        return CraftItemStack.asCraftMirror((net.minecraft.server.v1_8_R3.ItemStack)this.getItemStack());
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
    public void addTypedRunnable(TypedRunnable<ItemProjectile> r) {
        this.typedRunnables.add(r);
    }

    @Override
    public void removeTypedRunnable(TypedRunnable<ItemProjectile> r) {
        this.typedRunnables.remove(r);
    }

    @Override
    public ArrayList<Material> getIgnoredBlocks() {
        return this.ignoredMaterials;
    }

    public net.minecraft.server.v1_8_R3.ItemStack getItemStack() {
        net.minecraft.server.v1_8_R3.ItemStack itemstack = this.getDataWatcher().getItemStack(10);
        if (itemstack == null) {
            return new net.minecraft.server.v1_8_R3.ItemStack(Blocks.STONE);
        }
        return itemstack;
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

