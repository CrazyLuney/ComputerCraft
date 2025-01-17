/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.common;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockGeneric extends Block implements
    ITileEntityProvider
{
    protected BlockGeneric( Material material )
    {
        super( material );
        this.isBlockContainer = true;
    }

    protected abstract BlockState getDefaultBlockState( int damage, Direction placedSide );
    protected abstract TileGeneric createTile( BlockState state );
    protected abstract TileGeneric createTile( int damage );

    @Override
    public final void dropBlockAsItemWithChance( World world, @Nonnull BlockPos pos, @Nonnull BlockState state, float chance, int fortune )
    {
    }

    @Override
    public final void getDrops( @Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull BlockState state, int fortune )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric) tile;
            generic.getDroppedItems( drops, false );
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public final BlockState getStateForPlacement( World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, int damage, LivingEntity placer )
    {
        return getDefaultBlockState( damage, side );
    }

    @Override
    public final boolean removedByPlayer( @Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, boolean willHarvest )
    {
        if( !world.isRemote )
        {
            // Drop items
            boolean creative = player.capabilities.isCreativeMode;
            dropAllItems( world, pos, creative );
        }

        // Remove block
        return super.removedByPlayer( state, world, pos, player, willHarvest );
    }

    public final void dropAllItems( World world, BlockPos pos, boolean creative )
    {
        // Get items to drop
        NonNullList<ItemStack> drops = NonNullList.create();
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric) tile;
            generic.getDroppedItems( drops, creative );
        }

        // Drop items
        if( drops.size() > 0 )
        {
            for (ItemStack item : drops)
            {
                dropItem( world, pos, item );
            }
        }
    }

    public final void dropItem( World world, BlockPos pos, @Nonnull ItemStack stack )
    {
        Block.spawnAsEntity( world, pos, stack );
    }

    @Override
    public final void breakBlock( @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            generic.destroy();
        }
        super.breakBlock( world, pos, newState );
        world.removeTileEntity( pos );
    }

    @Nonnull
    @Override
    public final ItemStack getPickBlock( @Nonnull BlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.getPickedItem();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public final boolean onBlockActivated( World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.onActivate( player, side, hitX, hitY, hitZ );
        }
        return false;
    }

    @Override
    @Deprecated
    public final void neighborChanged( BlockState state, World world, BlockPos pos, Block block, BlockPos neighorPos )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            generic.onNeighbourChange();
        }
    }

    @Override
    public final void onNeighborChange( IBlockAccess world, BlockPos pos, BlockPos neighbour )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            generic.onNeighbourTileEntityChange( neighbour );
        }
    }

    @Override
    @Deprecated
    public final boolean isSideSolid( BlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, Direction side )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.isSolidOnSide( side.ordinal() );
        }
        return false;
    }

    @Override
    public final boolean canBeReplacedByLeaves( @Nonnull BlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos )
    {
        return false; // Generify me if anyone ever feels the need to change this
    }

    @Override
    public float getExplosionResistance( World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric && tile.hasWorld() )
        {
            TileGeneric generic = (TileGeneric)tile;
            if( generic.isImmuneToExplosion( exploder ) )
            {
                return 2000.0f;
            }
        }
        return super.getExplosionResistance( world, pos, exploder, explosion );
    }

    @Nonnull
    @Override
    @Deprecated
    public final AxisAlignedBB getBoundingBox( BlockState state, IBlockAccess world, BlockPos pos )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric && tile.hasWorld() )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.getBounds();
        }
        return FULL_BLOCK_AABB;
    }

    @Nonnull
    @Override
    @Deprecated
    public final AxisAlignedBB getSelectedBoundingBox( BlockState state, @Nonnull World world, @Nonnull BlockPos pos )
    {
        return getBoundingBox( state, world, pos ).offset( pos );
    }

    @Override
    @Deprecated
    public final AxisAlignedBB getCollisionBoundingBox( BlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric && tile.hasWorld() )
        {
            TileGeneric generic = (TileGeneric)tile;

            // Get collision bounds
            List<AxisAlignedBB> collision = new ArrayList<>( 1 );
            generic.getCollisionBounds( collision );

            // Return the union of the collision bounds
            if( collision.size() > 0 )
            {
                AxisAlignedBB aabb = collision.get( 0 );
                for( int i = 1; i < collision.size(); i++ )
                {
                    aabb = aabb.union( collision.get( i ) );
                }
                return aabb;
            }
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    @Deprecated
    public final void addCollisionBoxToList( BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB bigBox, @Nonnull List<AxisAlignedBB> list, Entity entity, boolean p_185477_7_ )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric && tile.hasWorld() )
        {
            TileGeneric generic = (TileGeneric)tile;

            // Get collision bounds
            List<AxisAlignedBB> collision = new ArrayList<>( 1 );
            generic.getCollisionBounds( collision );

            // Add collision bounds to list
            if( collision.size() > 0 )
            {
                for (AxisAlignedBB localBounds : collision)
                {
                    addCollisionBoxToList( pos, bigBox, list, localBounds );
                }
            }
        }
    }

    @Override
    @Deprecated
    public final boolean canProvidePower( BlockState state )
    {
        return true;
    }

    @Override
    public final boolean canConnectRedstone( BlockState state, IBlockAccess world, BlockPos pos, Direction side )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.getRedstoneConnectivity( side );
        }
        return false;
    }

    @Override
    @Deprecated
    public final int getStrongPower( BlockState state, IBlockAccess world, BlockPos pos, Direction oppositeSide )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric && tile.hasWorld() )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.getRedstoneOutput( oppositeSide.getOpposite() );
        }
        return 0;
    }

    @Override
    @Deprecated
    public final int getWeakPower( BlockState state, IBlockAccess world, BlockPos pos, Direction oppositeSide )
    {
        return getStrongPower( state, world, pos, oppositeSide );
    }

    public boolean getBundledRedstoneConnectivity( World world, BlockPos pos, Direction side )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.getBundledRedstoneConnectivity( side );
        }
        return false;
    }

    public int getBundledRedstoneOutput( World world, BlockPos pos, Direction side )
    {
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TileGeneric && tile.hasWorld() )
        {
            TileGeneric generic = (TileGeneric)tile;
            return generic.getBundledRedstoneOutput( side );
        }
        return 0;
    }

    @Override
    @Deprecated
    public boolean eventReceived( BlockState state, World world, BlockPos pos, int eventID, int eventParameter )
    {
        if( world.isRemote )
        {
            TileEntity tile = world.getTileEntity( pos );
            if( tile != null && tile instanceof TileGeneric )
            {
                TileGeneric generic = (TileGeneric)tile;
                generic.onBlockEvent( eventID, eventParameter );
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public final TileEntity createTileEntity( @Nonnull World world, @Nonnull BlockState state )
    {
        return createTile( state );
    }

    @Nonnull
    @Override
    public final TileEntity createNewTileEntity( @Nonnull World world, int damage )
    {
        return createTile( damage );
    }
}
