/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.commandblock;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CommandBlockPeripheralProvider implements IPeripheralProvider
{
    @Override
    public IPeripheral getPeripheral( @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction side )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof CommandBlockTileEntity )
        {
            CommandBlockTileEntity commandBlock = (CommandBlockTileEntity)tile;
            return new CommandBlockPeripheral( commandBlock );
        }
        return null;
    }
}
