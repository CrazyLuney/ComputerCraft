/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.modem;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.PeripheralType;
import dan200.computercraft.shared.peripheral.common.BlockPeripheral;
import dan200.computercraft.shared.peripheral.common.BlockPeripheralVariant;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileWirelessModem extends TileModemBase
{
    // Statics

    private static class Peripheral extends WirelessModemPeripheral
    {
        private TileModemBase m_entity;
        
        public Peripheral( TileModemBase entity )
        {
            super( false );
            m_entity = entity;
        }

        @Nonnull
        @Override
        public World getWorld()
        {
            return m_entity.getWorld();
        }
        
        @Nonnull
        @Override
        public Vec3d getPosition()
        {
            BlockPos pos = m_entity.getPos().offset( m_entity.getDirection() );
            return new Vec3d( (double)pos.getX(), (double)pos.getY(), (double)pos.getZ() );
        }

        @Override
        public boolean equals( IPeripheral other )
        {
            if( other instanceof Peripheral )
            {
                Peripheral otherModem = (Peripheral)other;
                return otherModem.m_entity == m_entity;
            }
            return false;
        }
    }

    // Members

    public TileWirelessModem()
    {
    }

    @Override
    public Direction getDirection()
    {
        // Wireless Modem
        BlockState state = getBlockState();
        switch( state.getValue( BlockPeripheral.Properties.VARIANT ) )
        {
            case WirelessModemDownOff:
            case WirelessModemDownOn:
            {
                return Direction.DOWN;
            }
            case WirelessModemUpOff:
            case WirelessModemUpOn:
            {
                return Direction.UP;
            }
            default:
            {
                return state.getValue( BlockPeripheral.Properties.FACING );
            }
        }
    }

    @Override
    public void setDirection( Direction dir )
    {
        // Wireless Modem
        if( dir == Direction.UP )
        {
            setBlockState( getBlockState()
                .withProperty( BlockPeripheral.Properties.VARIANT, BlockPeripheralVariant.WirelessModemUpOff )
                .withProperty( BlockPeripheral.Properties.FACING, Direction.NORTH )
            );
        }
        else if( dir == Direction.DOWN )
        {
            setBlockState( getBlockState()
                .withProperty( BlockPeripheral.Properties.VARIANT, BlockPeripheralVariant.WirelessModemDownOff )
                .withProperty( BlockPeripheral.Properties.FACING, Direction.NORTH )
            );
        }
        else
        {
            setBlockState( getBlockState()
                .withProperty( BlockPeripheral.Properties.VARIANT, BlockPeripheralVariant.WirelessModemOff )
                .withProperty( BlockPeripheral.Properties.FACING, dir )
            );
        }
    }

    @Override
    protected ModemPeripheral createPeripheral()
    {
        return new Peripheral( this );
    }

    @Override
    public boolean shouldRefresh( World world, BlockPos pos, @Nonnull BlockState oldState, @Nonnull BlockState newState )
    {
        return super.shouldRefresh( world, pos, oldState, newState ) || ComputerCraft.Blocks.peripheral.getPeripheralType( newState ) != PeripheralType.WirelessModem;
    }
}
