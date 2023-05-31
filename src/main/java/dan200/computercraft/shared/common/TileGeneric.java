/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.common;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.network.ComputerCraftPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class TileGeneric extends TileEntity
{
    public TileGeneric()
    {
    }

    public void requestTileEntityUpdate()
    {
        if( getWorld().isRemote )
        {
            ComputerCraftPacket packet = new ComputerCraftPacket();
            packet.m_packetType = ComputerCraftPacket.RequestTileEntityUpdate;

            BlockPos pos = getPos();
            packet.m_dataInt = new int[]{ pos.getX(), pos.getY(), pos.getZ() };
            ComputerCraft.sendToServer( packet );
        }
    }

    public void destroy()
    {
    }

    @Nullable
    public BlockGeneric getBlock()
    {
        Block block = getWorld().getBlockState( getPos() ).getBlock();
        if( block != null && block instanceof BlockGeneric )
        {
            return (BlockGeneric)block;
        }
        return null;
    }

    protected final BlockState getBlockState()
    {
        return getWorld().getBlockState( getPos() );
    }

    public final void updateBlock()
    {
        markDirty();
        BlockPos pos = getPos();
        BlockState state = getWorld().getBlockState( pos );
        getWorld().markBlockRangeForRenderUpdate( pos, pos );
        getWorld().notifyBlockUpdate( getPos(), state, state, 3 );
    }

    protected final void setBlockState( BlockState newState )
    {
        getWorld().setBlockState( getPos(), newState, 3 );
    }

    public void getDroppedItems( @Nonnull NonNullList<ItemStack> drops, boolean creative )
    {
    }

    public ItemStack getPickedItem()
    {
        return null;
    }

    public boolean onActivate( PlayerEntity player, Direction side, float hitX, float hitY, float hitZ )
    {
        return false;
    }

    public void onNeighbourChange()
    {
    }

    public void onNeighbourTileEntityChange( @Nonnull BlockPos neighbour )
    {
    }

    public boolean isSolidOnSide( int side )
    {
        return true;
    }

    public boolean isImmuneToExplosion( Entity exploder )
    {
        return false;
    }

    @Nonnull
    public AxisAlignedBB getBounds()
    {
        return new AxisAlignedBB( 0.0, 0.0, 0.0, 1.0, 1.0, 1.0 );
    }

    public void getCollisionBounds( @Nonnull List<AxisAlignedBB> bounds )
    {
        bounds.add( getBounds() );
    }

    public boolean getRedstoneConnectivity( Direction side )
    {
        return false;
    }

    public int getRedstoneOutput( Direction side )
    {
        return 0;
    }

    public boolean getBundledRedstoneConnectivity( @Nonnull Direction side )
    {
        return false;
    }

    public int getBundledRedstoneOutput( @Nonnull Direction side )
    {
        return 0;
    }

    protected double getInteractRange( PlayerEntity player )
    {
        return 8.0;
    }

    public boolean isUsable( PlayerEntity player, boolean ignoreRange )
    {
        if( player != null && player.isEntityAlive() )
        {
            if( getWorld().getTileEntity( getPos() ) == this )
            {
                if( !ignoreRange )
                {
                    double range = getInteractRange( player );
                    BlockPos pos = getPos();
                    return player.getEntityWorld() == getWorld() &&
                           player.getDistanceSq( (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5 ) <= ( range * range );
                }
                return true;
            }
        }
        return false;
    }

    protected void writeDescription( @Nonnull CompoundNBT nbttagcompound )
    {
    }

    protected void readDescription( @Nonnull CompoundNBT nbttagcompound )
    {
    }

    public final void sendBlockEvent( int eventID )
    {
        sendBlockEvent( eventID, 0 );
    }

    public final void sendBlockEvent( int eventID, int eventParameter )
    {
        getWorld().addBlockEvent( getPos(), getWorld().getBlockState( getPos() ).getBlock(), eventID, eventParameter );
    }

    public void onBlockEvent( int eventID, int eventParameter )
    {
    }

    @Override
    public boolean shouldRefresh( World world, BlockPos pos, @Nonnull BlockState oldState, @Nonnull BlockState newState )
    {
        return newState.getBlock() != oldState.getBlock();
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket()
    {
        // Communicate properties
        CompoundNBT nbttagcompound = new CompoundNBT();
        writeDescription( nbttagcompound );
        return new SUpdateTileEntityPacket( getPos(), 0, nbttagcompound );
    }

    @Override
    public final void onDataPacket( NetworkManager net, SUpdateTileEntityPacket packet )
    {
        switch( packet.getTileEntityType() )
        {
            case 0:
            {
                // Receive properties
                CompoundNBT nbttagcompound = packet.getNbtCompound();
                readDescription( nbttagcompound );
                break;
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag ()
    {
        CompoundNBT tag = super.getUpdateTag();
        writeDescription( tag );
        return tag;
    }

    @Override
    public void handleUpdateTag ( @Nonnull CompoundNBT tag)
    {
        super.handleUpdateTag(tag);
        readDescription( tag );
    }
}
