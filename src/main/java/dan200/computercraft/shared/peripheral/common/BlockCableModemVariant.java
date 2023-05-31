/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.common;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum BlockCableModemVariant implements IStringSerializable
{
    None( "none", null ),
    DownOff( "down_off", Direction.DOWN ),
    UpOff( "up_off", Direction.UP ),
    NorthOff( "north_off", Direction.NORTH ),
    SouthOff( "south_off", Direction.SOUTH ),
    WestOff( "west_off", Direction.WEST ),
    EastOff( "east_off", Direction.EAST ),
    DownOn( "down_on", Direction.DOWN ),
    UpOn( "up_on", Direction.UP ),
    NorthOn( "north_on", Direction.NORTH ),
    SouthOn( "south_on", Direction.SOUTH ),
    WestOn( "west_on", Direction.WEST ),
    EastOn( "east_on", Direction.EAST ),
    DownOffPeripheral( "down_off_peripheral", Direction.DOWN ),
    UpOffPeripheral( "up_off_peripheral", Direction.UP ),
    NorthOffPeripheral( "north_off_peripheral", Direction.NORTH ),
    SouthOffPeripheral( "south_off_peripheral", Direction.SOUTH ),
    WestOffPeripheral( "west_off_peripheral", Direction.WEST ),
    EastOffPeripheral( "east_off_peripheral", Direction.EAST ),
    DownOnPeripheral( "down_on_peripheral", Direction.DOWN ),
    UpOnPeripheral( "up_on_peripheral", Direction.UP ),
    NorthOnPeripheral( "north_on_peripheral", Direction.NORTH ),
    SouthOnPeripheral( "south_on_peripheral", Direction.SOUTH ),
    WestOnPeripheral( "west_on_peripheral", Direction.WEST ),
    EastOnPeripheral( "east_on_peripheral", Direction.EAST );

    public static BlockCableModemVariant fromFacing( Direction facing )
    {
        switch( facing )
        {
            case DOWN: return DownOff;
            case UP: return UpOff;
            case NORTH: return NorthOff;
            case SOUTH: return SouthOff;
            case WEST: return WestOff;
            case EAST: return EastOff;
        }
        return NorthOff;
    }

    private String m_name;
    private Direction m_facing;

    BlockCableModemVariant( String name, Direction facing )
    {
        m_name = name;
        m_facing = facing;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return m_name;
    }

    public Direction getFacing()
    {
        return m_facing;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
