/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.server.proxy;

import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.peripheral.diskdrive.TileDiskDrive;
import dan200.computercraft.shared.peripheral.printer.TilePrinter;
import dan200.computercraft.shared.proxy.ComputerCraftProxyCommon;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.File;

public class ComputerCraftProxyServer extends ComputerCraftProxyCommon
{
    public ComputerCraftProxyServer()
    {
    }
    
    // IComputerCraftProxy implementation
    
    @Override
    public void init()
    {
        super.init();
    }

    @Override
    public Object getTurtleGUI( PlayerInventory inventory, TileTurtle turtle )
    {
        return null;
    }

    @Override
    public boolean isClient()
    {
        return false;
    }

    @Override
    public boolean getGlobalCursorBlink()
    {
        return false;
    }

    @Override
    public long getRenderFrame()
    {
        return 0;
    }

    @Override
    public Object getFixedWidthFontRenderer()
    {
        return null;
    }
    
    @Override
    public void playRecord( SoundEvent record, String recordInfo, World world, BlockPos pos )
    {
    }

    @Override
    public Object getDiskDriveGUI( PlayerInventory inventory, TileDiskDrive drive )
    {
        return null;
    }
    
    @Override
    public Object getComputerGUI( TileComputer computer )
    {
        return null;
    }

    @Override
    public Object getPrinterGUI( PlayerInventory inventory, TilePrinter printer )
    {
        return null;
    }

    @Override
    public Object getPrintoutGUI( PlayerEntity player, Hand hand )
    {
        return null;
    }

    @Override
    public Object getPocketComputerGUI( PlayerEntity player, Hand hand )
    {
        return null;
    }

    @Override
    public File getWorldDir( World world )
    {
        return DimensionManager.getWorld( 0 ).getSaveHandler().getWorldDirectory();
    }
}
