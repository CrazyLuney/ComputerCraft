/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.computer.blocks;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.items.ItemComputer;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockComputer extends BlockComputerBase
{
    // Statics
    public static class Properties
    {
        public static final PropertyDirection FACING = PropertyDirection.create("facing", Direction.Plane.HORIZONTAL);
        public static final PropertyBool ADVANCED = PropertyBool.create("advanced");
        public static final PropertyEnum<ComputerState> STATE = PropertyEnum.create("state", ComputerState.class);
    }

    // Members
    
    public BlockComputer()
    {
        super( Material.ROCK );
        setHardness( 2.0f );
        setUnlocalizedName( "computercraft:computer" );
        setCreativeTab( ComputerCraft.mainCreativeTab );
        setDefaultState( this.blockState.getBaseState()
            .withProperty( Properties.FACING, Direction.NORTH )
            .withProperty( Properties.ADVANCED, false )
            .withProperty( Properties.STATE, ComputerState.Off )
        );
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer( this, Properties.FACING, Properties.ADVANCED, Properties.STATE );
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

        BlockState state = getDefaultState().withProperty( Properties.FACING, dir );
        if( meta > 8 )
        {
            state = state.withProperty( Properties.ADVANCED, true );
        }
        else
        {
            state = state.withProperty( Properties.ADVANCED, false );
        }
        return state;
    }

    @Override
    public int getMetaFromState( BlockState state )
    {
        int meta = state.getValue( Properties.FACING ).getIndex();
        if( state.getValue( Properties.ADVANCED ) )
        {
            meta += 8;
        }
        return meta;
    }

    @Override
    protected BlockState getDefaultBlockState( ComputerFamily family, Direction placedSide )
    {
        BlockState state = getDefaultState();
        if( placedSide.getAxis() != Direction.Axis.Y )
        {
            state = state.withProperty( Properties.FACING, placedSide );
        }

        switch( family )
        {
            case Normal:
            default:
            {
                return state.withProperty( Properties.ADVANCED, false );
            }
            case Advanced:
            {
                return state.withProperty( Properties.ADVANCED, true );
            }
        }
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
    public ComputerFamily getFamily( int damage )
    {
        return ((ItemComputer) Item.getItemFromBlock(this)).getFamily( damage );
    }

    @Override
    public ComputerFamily getFamily( BlockState state )
    {
        if( state.getValue( Properties.ADVANCED ) ) {
            return ComputerFamily.Advanced;
        } else {
            return ComputerFamily.Normal;
        }
    }

    @Override
    protected TileComputer createTile( ComputerFamily family )
    {
        return new TileComputer();
    }

    @Override
    public void onBlockPlacedBy( World world, BlockPos pos, BlockState state, LivingEntity player, @Nonnull ItemStack stack )
    {
        // Not sure why this is necessary
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileComputer )
        {
            tile.setWorld( world ); // Not sure why this is necessary
            tile.setPos( pos ); // Not sure why this is necessary
        }

        // Set direction
        Direction dir = DirectionUtil.fromEntityRot( player );
        setDirection( world, pos, dir );
    }
}
