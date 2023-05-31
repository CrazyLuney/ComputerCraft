/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.util;

import dan200.computercraft.ComputerCraft;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneUtil
{
    public static int getRedstoneOutput( World world, BlockPos pos, Direction side )
    {
        int power = 0;
        BlockState state = world.getBlockState( pos );
        Block block =   state.getBlock();
        if( block != Blocks.AIR )
        {
            if( block == Blocks.REDSTONE_WIRE )
            {
                if( side != Direction.UP )
                {
                    power = state.getValue( RedstoneWireBlock.POWER );
                }
                else
                {
                    power = 0;
                }
            }
            else if( state.canProvidePower( ) )
            {
                power = state.getWeakPower( world, pos, side.getOpposite() );
            }
            if( block.isNormalCube( state, world, pos ) )
            {
                for( Direction testSide : Direction.VALUES )
                {
                    if( testSide != side )
                    {
                        BlockPos testPos = pos.offset( testSide );
                        BlockState neighbour = world.getBlockState( testPos );
                        if( neighbour.canProvidePower( ) )
                        {
                            power = Math.max( power, neighbour.getStrongPower( world, testPos, testSide.getOpposite() ) );
                        }
                    }
                }
            }
        }
        return power;
    }

    public static int getBundledRedstoneOutput( World world, BlockPos pos, Direction side )
    {
        int signal = ComputerCraft.getBundledRedstoneOutput( world, pos, side );
        if( signal >= 0 )
        {
            return signal;
        }
        return 0;
    }

    public static void propagateRedstoneOutput( World world, BlockPos pos, Direction side )
    {
        // Propagate ordinary output
        BlockState block = world.getBlockState( pos );
        BlockPos neighbourPos = pos.offset( side );
        BlockState neighbour = world.getBlockState( neighbourPos );
        if( neighbour.getBlock() != Blocks.AIR )
        {
            world.neighborChanged( neighbourPos, block.getBlock(), pos );
            if( neighbour.getBlock().isNormalCube( neighbour, world, neighbourPos ) )
            {
                world.notifyNeighborsOfStateExcept( neighbourPos, block.getBlock(), side.getOpposite() );
            }
        }
    }
}
