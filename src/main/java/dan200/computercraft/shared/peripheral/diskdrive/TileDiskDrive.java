/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.diskdrive;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.PeripheralType;
import dan200.computercraft.shared.peripheral.common.BlockPeripheral;
import dan200.computercraft.shared.peripheral.common.TilePeripheralBase;
import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class TileDiskDrive extends TilePeripheralBase
    implements IInventory, ITickable
{
    // Statics

    private static final int BLOCKEVENT_PLAY_RECORD = 0;
    private static final int BLOCKEVENT_STOP_RECORD = 1;

    private static class MountInfo
    {
        public String mountPath;
    }

    // Members

    private final Map<IComputerAccess, MountInfo> m_computers;

    @Nonnull
    private ItemStack m_diskStack;
    private final IItemHandlerModifiable m_itemHandler = new InvWrapper( this );
    private IMount m_diskMount;
    
    private boolean m_recordQueued;
    private boolean m_recordPlaying;
    private boolean m_restartRecord;
    private boolean m_ejectQueued;

    public TileDiskDrive()
    {
        m_computers = new HashMap<>();

        m_diskStack = ItemStack.EMPTY;
        m_diskMount = null;
        
        m_recordQueued = false;
        m_recordPlaying = false;
        m_restartRecord = false;
    }

    @Override
    public void destroy()
    {
        ejectContents( true );
        synchronized( this )
        {
            if( m_recordPlaying )
            {
                sendBlockEvent( BLOCKEVENT_STOP_RECORD );
            }
        }
    }

    @Override
    public boolean onActivate( PlayerEntity player, Direction side, float hitX, float hitY, float hitZ )
    {
        if( player.isSneaking() )
        {
            // Try to put a disk into the drive
            if( !getWorld().isRemote )
            {
                ItemStack disk = player.getHeldItem( Hand.MAIN_HAND );
                if( !disk.isEmpty() && getStackInSlot(0).isEmpty() )
                {
                    if( ComputerCraft.getMedia( disk ) != null )
                    {
                        setInventorySlotContents( 0, disk );
                        player.setHeldItem( Hand.MAIN_HAND, ItemStack.EMPTY );
                        return true;
                    }
                }
            }
        }
        else
        {
            // Open the GUI
            if( !getWorld().isRemote )
            {
                ComputerCraft.openDiskDriveGUI( player, this );
            }
            return true;
        }
        return false;
    }

    @Override
    public Direction getDirection()
    {
        BlockState state = getBlockState();
        return state.getValue( BlockPeripheral.Properties.FACING );
    }

    @Override
    public void setDirection( Direction dir )
    {
        if( dir.getAxis() == Direction.Axis.Y )
        {
            dir = Direction.NORTH;
        }
        setBlockState( getBlockState().withProperty( BlockPeripheral.Properties.FACING, dir ) );
    }

    @Override
    public void readFromNBT(CompoundNBT nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        if( nbttagcompound.hasKey( "item" ) )
        {
            CompoundNBT item = nbttagcompound.getCompoundTag( "item" );
            m_diskStack = new ItemStack( item );
            m_diskMount = null;
        }
    }

    @Nonnull
    @Override
    public CompoundNBT writeToNBT(CompoundNBT nbttagcompound)
    {
        nbttagcompound = super.writeToNBT(nbttagcompound);
        if( !m_diskStack.isEmpty() )
        {
            CompoundNBT item = new CompoundNBT();
            m_diskStack.writeToNBT( item );
            nbttagcompound.setTag( "item", item );
        }
        return nbttagcompound;
    }
    
    @Override
    public void update()
    {
        super.update();

        // Ejection
        synchronized( this )
        {
            if( m_ejectQueued )
            {
                ejectContents( false );
                m_ejectQueued = false;
            }
        }
        
        // Music
        synchronized( this )
        {
            if( m_recordPlaying != m_recordQueued || m_restartRecord )
            {
                m_restartRecord = false;
                if( m_recordQueued )
                {
                    IMedia contents = getDiskMedia();
                    SoundEvent record = (contents != null) ? contents.getAudio( m_diskStack ) : null;
                    if( record != null )
                    {
                        m_recordPlaying = true;
                        sendBlockEvent( BLOCKEVENT_PLAY_RECORD );
                    }
                    else
                    {
                        m_recordQueued = false;
                    }
                }
                else
                {
                    sendBlockEvent( BLOCKEVENT_STOP_RECORD );
                    m_recordPlaying = false;
                }
            }
        }
    }

    // IInventory implementation
    
    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return m_diskStack.isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int i)
    {
        return m_diskStack;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int i)
    {
        ItemStack result = m_diskStack;
        m_diskStack = ItemStack.EMPTY;
        m_diskMount = null;
        
        return result;
    }
    
    @Nonnull
    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        if (m_diskStack.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        
        if (m_diskStack.getCount() <= j)
        {
            ItemStack disk = m_diskStack;
            setInventorySlotContents( 0, ItemStack.EMPTY );
            return disk;
        }
        
        ItemStack part = m_diskStack.splitStack(j);
        if (m_diskStack.isEmpty())
        {
            setInventorySlotContents( 0, ItemStack.EMPTY );
        }
        else
        {
            setInventorySlotContents( 0, m_diskStack );
        }
        return part;
    }

    @Override
    public void setInventorySlotContents( int i, @Nonnull ItemStack itemStack )
    {                    
        if( getWorld().isRemote )
        {
            m_diskStack = itemStack;
            m_diskMount = null;
            markDirty();
            return;
        }

        synchronized( this )
        {
            if( InventoryUtil.areItemsStackable( itemStack, m_diskStack ) )
            {
                m_diskStack = itemStack;
                return;
            }
            
            // Unmount old disk
            if( !m_diskStack.isEmpty() )
            {
                Set<IComputerAccess> computers = m_computers.keySet();
                for( IComputerAccess computer : computers )
                {
                    unmountDisk( computer );
                }
            }
            
            // Stop music
            if( m_recordPlaying )
            {
                sendBlockEvent( BLOCKEVENT_STOP_RECORD );
                m_recordPlaying = false;
                m_recordQueued = false;
            }
                
            // Swap disk over
            m_diskStack = itemStack;
            m_diskMount = null;
            markDirty();

            // Update contents
            updateAnim();

            // Mount new disk
            if( !m_diskStack.isEmpty() )
            {
                Set<IComputerAccess> computers = m_computers.keySet();
                for( IComputerAccess computer : computers )
                {
                    mountDisk( computer );
                }
            }
        }
    }

    @Override
    public boolean hasCustomName()
    {
        return getLabel() != null;
    }

    @Nonnull
    @Override
    public String getName()
    {
        String label = getLabel();
        if( label != null )
        {
            return label;
        }
        else
        {
            return "tile.computercraft:drive.name";
        }
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName()
    {
        if( hasCustomName() )
        {
            return new StringTextComponent( getName() );
        }
        else
        {
            return new TranslationTextComponent( getName() );
        }
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void openInventory( @Nonnull PlayerEntity player )
    {
    }
    
    @Override
    public void closeInventory( @Nonnull PlayerEntity player )
    {
    }

    @Override
    public boolean isItemValidForSlot( int i, @Nonnull ItemStack itemstack)
    {
        return true;
    }

    @Override
    public boolean isUsableByPlayer( @Nonnull PlayerEntity player )
    {
        return isUsable( player, false );
    }

    @Override
    public void clear()
    {
        synchronized( this )
        {
            setInventorySlotContents( 0, ItemStack.EMPTY );
        }
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
    }

    // IPeripheralTile implementation

    @Override
    public IPeripheral getPeripheral( Direction side )
    {
        return new DiskDrivePeripheral( this );
    }

    public ItemStack getDiskStack()
    {
        synchronized( this )
        {
            return getStackInSlot( 0 );
        }
    }

    public void setDiskStack( @Nonnull ItemStack stack )
    {
        synchronized( this )
        {
            setInventorySlotContents( 0, stack );
        }
    }

    public IMedia getDiskMedia()
    {
        return ComputerCraft.getMedia( getDiskStack() );
    }

    public String getDiskMountPath( IComputerAccess computer )
    {
        synchronized( this )
        {
            if( m_computers.containsKey( computer ) )
            {
                MountInfo info = m_computers.get( computer );
                return info.mountPath;
            }
        }
        return null;
    }

    public void mount( IComputerAccess computer )
    {
        synchronized( this )
        {
            m_computers.put( computer, new MountInfo() );
            mountDisk( computer );
        }
    }

    public void unmount( IComputerAccess computer )
    {
        synchronized( this )
        {
            unmountDisk( computer );
            m_computers.remove( computer );
        }
    }

    public void playDiskAudio()
    {
        synchronized( this )
        {
            IMedia media = getDiskMedia();
            if( media != null && media.getAudioTitle( m_diskStack ) != null )
            {
                m_recordQueued = true;
                m_restartRecord = m_recordPlaying;
            }
        }
    }

    public void stopDiskAudio()
    {
        synchronized( this )
        {
            m_recordQueued = false;
            m_restartRecord = false;
        }
    }

    public void ejectDisk()
    {
        synchronized( this )
        {
            if( !m_ejectQueued )
            {
                m_ejectQueued = true;
            }
        }
    }

    // private methods
    
    private synchronized void mountDisk( IComputerAccess computer )
    {
        if( !m_diskStack.isEmpty() )
        {
            MountInfo info = m_computers.get( computer );
            IMedia contents = getDiskMedia();
            if( contents != null )
            {
                if( m_diskMount == null )
                {
                    m_diskMount = contents.createDataMount( m_diskStack, getWorld() );
                }
                if( m_diskMount != null )
                {
                    if( m_diskMount instanceof IWritableMount)
                    {
                        // Try mounting at the lowest numbered "disk" name we can
                        int n = 1;
                        while( info.mountPath == null )
                        {
                            info.mountPath = computer.mountWritable( (n==1) ? "disk" : ("disk" + n), (IWritableMount)m_diskMount );
                            n++;    
                        }
                    }
                    else
                    {
                        // Try mounting at the lowest numbered "disk" name we can
                        int n = 1;
                        while( info.mountPath == null )
                        {
                            info.mountPath = computer.mount( (n==1) ? "disk" : ("disk" + n), m_diskMount );
                            n++;    
                        }
                    }
                }
                else
                {
                    info.mountPath = null;
                }
            }
            computer.queueEvent( "disk", new Object[] { computer.getAttachmentName() } );
        }
    }

    private synchronized void unmountDisk( IComputerAccess computer )
    {
        if( !m_diskStack.isEmpty() )
        {
            MountInfo info = m_computers.get( computer );
            assert( info != null );
            if( info.mountPath != null )
            {
                computer.unmount( info.mountPath );
                info.mountPath = null;
            }
            computer.queueEvent( "disk_eject", new Object[] { computer.getAttachmentName() } );
        }
    }

    private synchronized void updateAnim()
    {            
        if( !m_diskStack.isEmpty() )
        {
            IMedia contents = getDiskMedia();
            if( contents != null ) {
                setAnim( 2 );
            } else {
                setAnim( 1 );
            }
        }
        else
        {
            setAnim( 0 );
        }
    }
        
    private synchronized void ejectContents( boolean destroyed )
    {
        if( getWorld().isRemote )
        {
            return;
        }
        
        if( !m_diskStack.isEmpty() )
        {
            // Remove the disks from the inventory
            ItemStack disks = m_diskStack;
            setInventorySlotContents( 0, ItemStack.EMPTY );

            // Spawn the item in the world
            int xOff = 0;
            int zOff = 0;
            if( !destroyed )
            {
                Direction dir = getDirection();
                xOff = dir.getFrontOffsetX();
                zOff = dir.getFrontOffsetZ();
            }

            BlockPos pos = getPos();
            double x = (double)pos.getX() + 0.5 + ((double)xOff * 0.5);
            double y = (double)pos.getY() + 0.75;
            double z = (double)pos.getZ() + 0.5 + ((double)zOff * 0.5);
            ItemEntity entityitem = new ItemEntity( getWorld(), x, y, z, disks );
            entityitem.motionX = (double)xOff * 0.15;
            entityitem.motionY = 0.0;
            entityitem.motionZ = (double)zOff * 0.15;
            
            getWorld().spawnEntity(entityitem);
            if( !destroyed )
            {
                getWorld().playBroadcastSound(1000, getPos(), 0);
            }
        }
    }

    @Override
    public final void readDescription( @Nonnull CompoundNBT nbttagcompound )
    {
        super.readDescription( nbttagcompound );
        if( nbttagcompound.hasKey( "item" ) )
        {
            m_diskStack = new ItemStack( nbttagcompound.getCompoundTag( "item" ) );
        }
        else
        {
            m_diskStack = ItemStack.EMPTY;
        }
        updateBlock();
    }

    @Override
    public void writeDescription( @Nonnull CompoundNBT nbttagcompound )
    {
        super.writeDescription( nbttagcompound );
        if( !m_diskStack.isEmpty() )
        {
            CompoundNBT item = new CompoundNBT();
            m_diskStack.writeToNBT( item );
            nbttagcompound.setTag( "item", item );
        }
    }

    @Override
    public void onBlockEvent( int eventID, int eventParameter )
    {
        super.onBlockEvent( eventID, eventParameter );
        switch( eventID )
        {
            case BLOCKEVENT_PLAY_RECORD:
            {
                playRecord();
                break;
            }
            case BLOCKEVENT_STOP_RECORD:
            {
                stopRecord();
                break;
            }
        }
    }

    @Override
    public boolean shouldRefresh( World world, BlockPos pos, @Nonnull BlockState oldState, @Nonnull BlockState newState )
    {
        return super.shouldRefresh( world, pos, oldState, newState ) || ComputerCraft.Blocks.peripheral.getPeripheralType( newState ) != PeripheralType.DiskDrive;
    }

    // Private methods

    private void playRecord()
    {
        IMedia contents = getDiskMedia();
        SoundEvent record = (contents != null) ? contents.getAudio( m_diskStack ) : null;
        if( record != null )
        {
            ComputerCraft.playRecord( record, contents.getAudioTitle( m_diskStack ), getWorld(), getPos() );
        }
        else
        {
            ComputerCraft.playRecord( null, null, getWorld(), getPos() );
        }
    }

    private void stopRecord()
    {
        ComputerCraft.playRecord( null, null, getWorld(), getPos() );
    }

    @Override
    public boolean hasCapability( @Nonnull Capability<?> capability, @Nullable Direction facing )
    {
        return capability == ITEM_HANDLER_CAPABILITY ||  super.hasCapability( capability, facing );
    }

    @Nullable
    @Override
    public <T> T getCapability( @Nonnull Capability<T> capability, @Nullable Direction facing )
    {
        if( capability == ITEM_HANDLER_CAPABILITY )
        {
            return ITEM_HANDLER_CAPABILITY.cast( m_itemHandler );
        }
        return super.getCapability( capability, facing );
    }
}
