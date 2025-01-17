/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.common;

import dan200.computercraft.shared.common.BlockDirectional;
import dan200.computercraft.shared.common.TileGeneric;
import dan200.computercraft.shared.peripheral.PeripheralType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class BlockPeripheralBase extends BlockDirectional
{
    public BlockPeripheralBase()
    {
        super( Material.ROCK );
    }

    protected abstract BlockState getDefaultBlockState( PeripheralType type, Direction placedSide );
    protected abstract PeripheralType getPeripheralType( int damage );
    protected abstract PeripheralType getPeripheralType( BlockState state );
    protected abstract TilePeripheralBase createTile( PeripheralType type );

    @Override
    public final boolean canPlaceBlockOnSide( @Nonnull World world, @Nonnull BlockPos pos, Direction side )
    {
        return true; // ItemPeripheralBase handles this
    }

    @Override
    protected final BlockState getDefaultBlockState( int damage, Direction placedSide )
    {
        ItemPeripheralBase item = (ItemPeripheralBase)Item.getItemFromBlock( this );
        return getDefaultBlockState( item.getPeripheralType( damage ), placedSide );
    }

    @Override
    public final TileGeneric createTile( BlockState state )
    {
        return createTile( getPeripheralType( state ) );
    }

    @Override
    public final TileGeneric createTile( int damage )
    {
        return createTile( getPeripheralType( damage ) );
    }

    public final PeripheralType getPeripheralType( IBlockAccess world, BlockPos pos )
    {
        return getPeripheralType( world.getBlockState( pos ) );
    }
}
