/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.diskdrive;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerDiskDrive extends Container
{
    private final TileDiskDrive m_diskDrive;

    public ContainerDiskDrive( IInventory playerInventory, TileDiskDrive diskDrive )
    {
        m_diskDrive = diskDrive;
        addSlotToContainer(new Slot( m_diskDrive, 0, 8 + 4 * 18, 35));
        
        for(int j = 0; j < 3; j++)
        {
            for(int i1 = 0; i1 < 9; i1++)
            {
                addSlotToContainer(new Slot(playerInventory, i1 + j * 9 + 9, 8 + i1 * 18, 84 + j * 18));
            }
        }

        for(int k = 0; k < 9; k++)
        {
            addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith( @Nonnull PlayerEntity player )
    {
        return m_diskDrive.isUsableByPlayer( player );
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot( PlayerEntity player, int i )
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(i);
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack().copy();
            itemstack = itemstack1.copy();
            if(i == 0 )
            {
                if(!mergeItemStack(itemstack1, 1, 37, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if( !mergeItemStack(itemstack1, 0, 1, false) )
            {
                return ItemStack.EMPTY;
            }
            
            if(itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
            
            if(itemstack1.getCount() != itemstack.getCount())
            {
                slot.onTake(player, itemstack1);
            }
            else
            {
                return ItemStack.EMPTY;
            }
        }
        return itemstack;
    }
}
