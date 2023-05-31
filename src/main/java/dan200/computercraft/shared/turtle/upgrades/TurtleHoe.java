/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.turtle.upgrades;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleVerb;
import dan200.computercraft.shared.turtle.core.TurtlePlaceCommand;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TurtleHoe extends TurtleTool
{
    public TurtleHoe( ResourceLocation id, int legacyId, String adjective, Item item )
    {
        super( id, legacyId, adjective, item );
    }

    @Override
    protected boolean canBreakBlock( World world, BlockPos pos )
    {
        if( super.canBreakBlock( world, pos ) )
        {
            BlockState state = world.getBlockState( pos );
            Material material = state.getMaterial( );
            return
                material == Material.PLANTS ||
                material == Material.CACTUS ||
                material == Material.GOURD ||
                material == Material.LEAVES ||
                material == Material.VINE;
        }
        return false;
    }

    @Nonnull
    @Override
    public TurtleCommandResult useTool( @Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side, @Nonnull TurtleVerb verb, @Nonnull Direction direction )
    {
        if( verb == TurtleVerb.Dig )
        {
            ItemStack hoe = m_item.copy();
            ItemStack remainder = TurtlePlaceCommand.deploy( hoe, turtle, direction, null, null );
            if( remainder != hoe )
            {
                return TurtleCommandResult.success();
            }
        }
        return super.useTool( turtle, side, verb, direction );
    }
}
