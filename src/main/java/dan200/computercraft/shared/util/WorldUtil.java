/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class WorldUtil
{
    public static boolean isBlockInWorld( World world, BlockPos pos )
    {
        return pos.getY() >= 0 && pos.getY() < world.getHeight();
    }

    public static boolean isLiquidBlock( World world, BlockPos pos )
    {
        return isBlockInWorld( world, pos ) && world.getBlockState( pos ).getMaterial().isLiquid();
    }

    public static BlockPos moveCoords( BlockPos pos, Direction dir )
    {
        return pos.offset( dir );
    }

    public static Pair<Entity, Vec3d> rayTraceEntities( World world, Vec3d vecStart, Vec3d vecDir, double distance )
    {
        Vec3d vecEnd = vecStart.addVector( vecDir.x * distance, vecDir.y * distance, vecDir.z * distance );

        // Raycast for blocks
        RayTraceResult result = world.rayTraceBlocks( vecStart.addVector(0.0,0.0,0.0), vecEnd.addVector(0.0,0.0,0.0) );
        if( result != null && result.typeOfHit == RayTraceResult.Type.BLOCK )
        {
            distance = vecStart.distanceTo( result.hitVec );
            vecEnd = vecStart.addVector( vecDir.x * distance, vecDir.y * distance, vecDir.z * distance );
        }

        // Check for entities
        float xStretch = (Math.abs(vecDir.x) > 0.25f) ? 0.0f : 1.0f;
        float yStretch = (Math.abs(vecDir.y) > 0.25f) ? 0.0f : 1.0f;
        float zStretch = (Math.abs(vecDir.z) > 0.25f) ? 0.0f : 1.0f;
        AxisAlignedBB bigBox = new AxisAlignedBB(
            Math.min(vecStart.x, vecEnd.x) - 0.375f * xStretch,
            Math.min(vecStart.y, vecEnd.y) - 0.375f * yStretch,
            Math.min(vecStart.z, vecEnd.z) - 0.375f * zStretch,
            Math.max(vecStart.x, vecEnd.x) + 0.375f * xStretch,
            Math.max(vecStart.y, vecEnd.y) + 0.375f * yStretch,
            Math.max(vecStart.z, vecEnd.z) + 0.375f * zStretch
        );

        Entity closest = null;
        double closestDist = 99.0;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity( null, bigBox );
        for( Entity entity : list )
        {
            if( entity.isDead || !entity.canBeCollidedWith() )
            {
                continue;
            }

            AxisAlignedBB littleBox = entity.getEntityBoundingBox();
            if( littleBox == null )
            {
                littleBox = entity.getCollisionBoundingBox();
                if( littleBox == null )
                {
                    continue;
                }
            }

            if( littleBox.contains( vecStart ) )
            {
                closest = entity;
                closestDist = 0.0f;
                continue;
            }

            RayTraceResult littleBoxResult = littleBox.calculateIntercept( vecStart, vecEnd );
            if( littleBoxResult != null )
            {
                double dist = vecStart.distanceTo( littleBoxResult.hitVec );
                if( closest == null || dist <= closestDist )
                {
                    closest = entity;
                    closestDist = dist;
                }
            }
            else if( littleBox.intersects( bigBox ) )
            {
                if( closest == null )
                {
                    closest = entity;
                    closestDist = distance;
                }
            }
        }
        if( closest != null && closestDist <= distance )
        {
            Vec3d closestPos = vecStart.addVector( vecDir.x * closestDist, vecDir.y * closestDist, vecDir.z * closestDist );
            return Pair.of( closest, closestPos );
        }
        return null;
    }

    public static void dropItemStack( @Nonnull ItemStack stack, World world, BlockPos pos )
    {
        dropItemStack( stack, world, pos, null );
    }

    public static void dropItemStack( @Nonnull ItemStack stack, World world, BlockPos pos, Direction direction )
    {
        double xDir;
        double yDir;
        double zDir;
        if( direction != null )
        {
            xDir = (double)direction.getFrontOffsetX();
            yDir = (double)direction.getFrontOffsetY();
            zDir = (double)direction.getFrontOffsetZ();
        }
        else
        {
            xDir = 0.0;
            yDir = 0.0;
            zDir = 0.0;
        }

        double xPos = pos.getX() + 0.5 + xDir * 0.4;
        double yPos = pos.getY() + 0.5 + yDir * 0.4;
        double zPos = pos.getZ() + 0.5 + zDir * 0.4;
        dropItemStack( stack, world, xPos, yPos, zPos, xDir, yDir, zDir );
    }

    public static void dropItemStack( @Nonnull ItemStack stack, World world, double xPos, double yPos, double zPos )
    {
        dropItemStack( stack, world, xPos, yPos, zPos, 0.0, 0.0, 0.0 );
    }

    public static void dropItemStack( @Nonnull ItemStack stack, World world, double xPos, double yPos, double zPos, double xDir, double yDir, double zDir )
    {
        ItemEntity entityItem = new ItemEntity( world, xPos, yPos, zPos, stack.copy() );
        entityItem.motionX = xDir * 0.7 + world.rand.nextFloat() * 0.2 - 0.1;
        entityItem.motionY = yDir * 0.7 + world.rand.nextFloat() * 0.2 - 0.1;
        entityItem.motionZ = zDir * 0.7 + world.rand.nextFloat() * 0.2 - 0.1;
        entityItem.setDefaultPickupDelay();
        world.spawnEntity( entityItem );
    }
}
