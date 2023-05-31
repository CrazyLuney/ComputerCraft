/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.computer.blocks;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockCommandComputer extends BlockComputerBase
{
    // Statics

    public static class Properties
    {
        public static final PropertyDirection FACING = PropertyDirection.create("facing", Direction.Plane.HORIZONTAL);
        public static final PropertyEnum<ComputerState> STATE = PropertyEnum.create("state", ComputerState.class);
    }

    // Members

    public BlockCommandComputer()
    {
        super( Material.IRON );
        setBlockUnbreakable();
        setResistance( 6000000.0F );
        setUnlocalizedName( "computercraft:command_computer" );
        setCreativeTab( ComputerCraft.mainCreativeTab );
        setDefaultState( this.blockState.getBaseState()
            .withProperty( Properties.FACING, Direction.NORTH )
            .withProperty( Properties.STATE, ComputerState.Off )
        );
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, Properties.FACING, Properties.STATE );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateFromMeta( int meta )
    {
        Direction dir = Direction.getFront( meta & 0x7 );
        if( dir.getAxis() == Direction.Axis.Y )
        {
            dir = Direction.NORTH;
        }
        return getDefaultState().withProperty( Properties.FACING, dir );
    }

    @Override
    public int getMetaFromState( BlockState state )
    {
        return state.getValue( Properties.FACING ).getIndex();
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getActualState( @Nonnull BlockState state, IBlockAccess world, BlockPos pos )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof IComputerTile )
        {
            IComputer computer = ((IComputerTile)tile).getComputer();
            if( computer != null && computer.isOn() )
            {
                if( computer.isCursorDisplayed() )
                {
                    return state.withProperty( Properties.STATE, ComputerState.Blinking );
                }
                else
                {
                    return state.withProperty( Properties.STATE, ComputerState.On );
                }
            }
        }
        return state.withProperty( Properties.STATE, ComputerState.Off );
    }

    @Override
    protected BlockState getDefaultBlockState( ComputerFamily family, Direction placedSide )
    {
        if( placedSide.getAxis() != Direction.Axis.Y )
        {
            return getDefaultState().withProperty( Properties.FACING, placedSide );
        }
        else
        {
            return getDefaultState();
        }
    }

    @Override
    public ComputerFamily getFamily( int damage )
    {
        return ComputerFamily.Command;
    }

    @Override
    public ComputerFamily getFamily( BlockState state )
    {
        return ComputerFamily.Command;
    }

    @Override
    protected TileComputer createTile( ComputerFamily family )
    {
        return new TileCommandComputer();
    }

    @Override
    public void onBlockPlacedBy( World world, BlockPos pos, BlockState state, LivingEntity player, @Nonnull ItemStack itemstack )
    {
        // Not sure why this is necessary
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileCommandComputer )
        {
            tile.setWorld( world ); // Not sure why this is necessary
            tile.setPos( pos ); // Not sure why this is necessary
        }

        // Set direction
        Direction dir = DirectionUtil.fromEntityRot( player );
        setDirection( world, pos, dir );
    }
}
