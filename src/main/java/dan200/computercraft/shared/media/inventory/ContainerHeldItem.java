/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.media.inventory;

import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class ContainerHeldItem extends Container
{
    private final ItemStack m_stack;
    private final Hand m_hand;

    public ContainerHeldItem( PlayerEntity player, Hand hand )
    {
        m_hand = hand;
        m_stack = InventoryUtil.copyItem( player.getHeldItem( hand ) );
    }

    @Nonnull
    public ItemStack getStack()
    {
        return m_stack;
    }

    @Override
    public boolean canInteractWith( @Nonnull PlayerEntity player )
    {
        if( player != null && player.isEntityAlive() )
        {
            ItemStack stack = player.getHeldItem( m_hand );
            if( (stack == m_stack) || (!stack.isEmpty() && !m_stack.isEmpty() && stack.getItem() == m_stack.getItem()) )
            {
                return true;
            }
        }
        return false;
    }
}
