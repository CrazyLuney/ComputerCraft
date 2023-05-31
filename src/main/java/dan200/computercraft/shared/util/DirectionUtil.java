/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.util;

import dan200.computercraft.shared.common.IDirectionalTile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public class DirectionUtil
{
    public static Direction rotateRight( Direction dir )
    {
        if( dir.getAxis() != Direction.Axis.Y )
        {
            return dir.rotateY();
        }
        else
        {
            return dir;
        }
    }

    public static Direction rotateLeft( Direction dir )
    {
        if( dir.getAxis() != Direction.Axis.Y )
        {
            return dir.rotateYCCW();
        }
        else
        {
            return dir;
        }
    }

    public static Direction rotate180( Direction dir )
    {
        if( dir.getAxis() != Direction.Axis.Y )
        {
            return dir.getOpposite();
        }
        else
        {
            return dir;
        }
    }

    public static int toLocal( IDirectionalTile directional, Direction dir )
    {
        Direction front = directional.getDirection();
        if( front.getAxis() == Direction.Axis.Y )
        {
            front = Direction.NORTH;
        }

        Direction back = rotate180( front );
        Direction left = rotateLeft( front );
        Direction right = rotateRight( front );
        if( dir == front )
        {
            return 3;
        }
        else if( dir == back )
        {
            return 2;
        }
        else if( dir == left )
        {
            return 5;
        }
        else if( dir == right )
        {
            return 4;
        }
        else if( dir == Direction.UP )
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static Direction fromEntityRot( LivingEntity player )
    {
        int rot = MathHelper.floor( ( player.rotationYaw / 90.0f ) + 0.5f ) & 0x3;
        switch( rot ) {
            case 0: return Direction.NORTH;
            case 1: return Direction.EAST;
            case 2: return Direction.SOUTH;
            case 3: return Direction.WEST;
        }
        return Direction.NORTH;
    }

    public static float toYawAngle( Direction dir )
    {
        switch( dir )
        {
            case NORTH: return 180.0f;
            case SOUTH: return 0.0f;
            case WEST: return 90.0f;
            case EAST: return 270.0f;
            default: return 0.0f;
        }
    }

    public static float toPitchAngle( Direction dir )
    {
        switch( dir )
        {
            case DOWN: return 90.0f;
            case UP: return 270.0f;
            default: return 0.0f;
        }
    }
}
