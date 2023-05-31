/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.proxy;

import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.network.ComputerCraftPacket;
import dan200.computercraft.shared.peripheral.diskdrive.TileDiskDrive;
import dan200.computercraft.shared.peripheral.printer.TilePrinter;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.File;

public interface IComputerCraftProxy
{
    void preInit();
    void init();
    boolean isClient();

    boolean getGlobalCursorBlink();
    long getRenderFrame();
    void deleteDisplayLists( int list, int range );
    Object getFixedWidthFontRenderer();

    String getRecordInfo( @Nonnull ItemStack item );
    void playRecord( SoundEvent record, String recordInfo, World world, BlockPos pos );

    Object getDiskDriveGUI( PlayerInventory inventory, TileDiskDrive drive );
    Object getComputerGUI( TileComputer computer );
    Object getPrinterGUI( PlayerInventory inventory, TilePrinter printer );
    Object getTurtleGUI( PlayerInventory inventory, TileTurtle turtle );
    Object getPrintoutGUI( PlayerEntity player, Hand hand );
    Object getPocketComputerGUI( PlayerEntity player, Hand hand );

    File getWorldDir( World world );
    void handlePacket( ComputerCraftPacket packet, PlayerEntity player );
}
