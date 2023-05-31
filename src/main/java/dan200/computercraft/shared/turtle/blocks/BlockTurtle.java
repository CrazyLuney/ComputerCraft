/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.turtle.blocks;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.blocks.BlockComputerBase;
import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockTurtle extends BlockComputerBase
{
    // Statics

    public static class Properties
    {
        public static final PropertyDirection FACING = PropertyDirection.create( "facing", Direction.Plane.HORIZONTAL );
    }

    public static BlockTurtle createTurtleBlock()
    {
        return new BlockTurtle();
    }

    // Members

    public BlockTurtle()
    {
        super( Material.IRON );
        setHardness( 2.5f );
        setUnlocalizedName( "computercraft:turtle" );
        setCreativeTab( ComputerCraft.mainCreativeTab );
        setDefaultState( this.blockState.getBaseState()
            .withProperty( Properties.FACING, Direction.NORTH )
        );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockRenderType getRenderType( BlockState state )
    {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube( BlockState state )
    {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube( BlockState state )
    {
        return false;
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape( IBlockAccess world, BlockState state, BlockPos pos, Direction side )
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, Properties.FACING );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateFromMeta( int meta )
    {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState( BlockState state )
    {
        return 0;
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getActualState( @Nonnull BlockState state, IBlockAccess world, BlockPos pos )
    {
        return state.withProperty( Properties.FACING, getDirection( world, pos ) );
    }

    @Override
    protected BlockState getDefaultBlockState( ComputerFamily family, Direction placedSide )
    {
        return getDefaultState();
    }

    private ComputerFamily getFamily()
    {
        if( this == ComputerCraft.Blocks.turtleAdvanced )
        {
            return ComputerFamily.Advanced;
        }
        else
        {
            return ComputerFamily.Normal;
        }
    }

    @Override
    public ComputerFamily getFamily( int damage )
    {
        return getFamily();
    }

    @Override
    public ComputerFamily getFamily( BlockState state )
    {
        return getFamily();
    }

    @Override
    protected TileComputerBase createTile( ComputerFamily family )
    {
        if( this == ComputerCraft.Blocks.turtleAdvanced )
        {
            return new TileTurtleAdvanced();
        }
        else if( this == ComputerCraft.Blocks.turtleExpanded )
        {
            return new TileTurtleExpanded();
        }
        else
        {
            return new TileTurtle();
        }
    }

    @Override
    public void onBlockPlacedBy( World world, BlockPos pos, BlockState state, LivingEntity player, @Nonnull ItemStack itemstack )
    {
        // Not sure why this is necessary
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileTurtle )
        {
            tile.setWorld( world ); // Not sure why this is necessary
            tile.setPos( pos ); // Not sure why this is necessary
        }

        // Set direction
        Direction dir = DirectionUtil.fromEntityRot( player );
        setDirection( world, pos, dir.getOpposite() );
    }
}
