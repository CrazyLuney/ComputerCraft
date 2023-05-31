/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.common;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.peripheral.PeripheralType;
import dan200.computercraft.shared.peripheral.diskdrive.TileDiskDrive;
import dan200.computercraft.shared.peripheral.modem.TileWirelessModem;
import dan200.computercraft.shared.peripheral.monitor.TileMonitor;
import dan200.computercraft.shared.peripheral.printer.TilePrinter;
import dan200.computercraft.shared.peripheral.speaker.TileSpeaker;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class BlockPeripheral extends BlockPeripheralBase
{
    public static class Properties
    {
        public static final PropertyDirection FACING = PropertyDirection.create( "facing", Direction.Plane.HORIZONTAL );
        public static final PropertyEnum<BlockPeripheralVariant> VARIANT = PropertyEnum.create( "variant", BlockPeripheralVariant.class );
    }

    public BlockPeripheral()
    {
        setHardness( 2.0f );
        setUnlocalizedName( "computercraft:peripheral" );
        setCreativeTab( ComputerCraft.mainCreativeTab );
        setDefaultState( this.blockState.getBaseState()
            .withProperty( Properties.FACING, Direction.NORTH )
            .withProperty( Properties.VARIANT, BlockPeripheralVariant.DiskDriveEmpty )
        );
    }

    @Override
    @Nonnull
    @SideOnly( Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer( this, Properties.FACING, Properties.VARIANT );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateFromMeta( int meta )
    {
        BlockState state = getDefaultState();
        if( meta >= 2 && meta <= 5 )
        {
            state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.DiskDriveEmpty );
            state = state.withProperty( Properties.FACING, Direction.getFront( meta ) );
        }
        else if( meta <= 9 )
        {
            if( meta == 0 )
            {
                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemDownOff );
                state = state.withProperty( Properties.FACING, Direction.NORTH );
            }
            else if( meta == 1 )
            {
                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemUpOff );
                state = state.withProperty( Properties.FACING, Direction.NORTH );
            }
            else
            {
                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemOff );
                state = state.withProperty( Properties.FACING, Direction.getFront( meta - 4 ) );
            }
        }
        else if( meta == 10 )
        {
            state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.Monitor );
        }
        else if( meta == 11 )
        {
            state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.PrinterEmpty );
        }
        else if( meta == 12 )
        {
            state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.AdvancedMonitor );
        }
        else if (meta == 13)
        {
            state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.Speaker);
        }
        return state;
    }

    @Override
    public int getMetaFromState( BlockState state )
    {
        int meta = 0;
        BlockPeripheralVariant variant = state.getValue( Properties.VARIANT );
        switch( variant.getPeripheralType() )
        {
            case DiskDrive:
            {
                Direction dir = state.getValue( Properties.FACING );
                if( dir.getAxis() == Direction.Axis.Y ) {
                    dir = Direction.NORTH;
                }
                meta = dir.getIndex();
                break;
            }
            case WirelessModem:
            {
                switch( variant )
                {
                    case WirelessModemDownOff:
                    case WirelessModemDownOn:
                    {
                        meta = 0;
                        break;
                    }
                    case WirelessModemUpOff:
                    case WirelessModemUpOn:
                    {
                        meta = 1;
                        break;
                    }
                    default:
                    {
                        Direction dir = state.getValue( Properties.FACING );
                        meta = dir.getIndex() + 4;
                        break;
                    }
                }
                break;
            }
            case Monitor:
            {
                meta = 10;
                break;
            }
            case Printer:
            {
                meta = 11;
                break;
            }
            case AdvancedMonitor:
            {
                meta = 12;
                break;
            }
            case Speaker:
            {
                meta = 13;
                break;
            }
        }
        return meta;
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getActualState( @Nonnull BlockState state, IBlockAccess world, BlockPos pos )
    {
        int anim;
        Direction dir;
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TilePeripheralBase )
        {
            TilePeripheralBase peripheral = (TilePeripheralBase)tile;
            anim = peripheral.getAnim();
            dir = peripheral.getDirection();
        }
        else
        {
            anim = 0;
            dir = state.getValue( Properties.FACING );
            switch( state.getValue( Properties.VARIANT ) )
            {
                case WirelessModemDownOff:
                case WirelessModemDownOn:
                {
                    dir = Direction.DOWN;
                    break;
                }
                case WirelessModemUpOff:
                case WirelessModemUpOn:
                {
                    dir = Direction.UP;
                    break;
                }
            }
        }

        PeripheralType type = getPeripheralType( state );
        switch( type )
        {
            case DiskDrive:
            {
                state = state.withProperty( Properties.FACING, dir );
                switch( anim )
                {
                    case 0:
                    default:
                    {
                        state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.DiskDriveEmpty );
                        break;
                    }
                    case 1:
                    {
                        state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.DiskDriveInvalid );
                        break;
                    }
                    case 2:
                    {
                        state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.DiskDriveFull );
                        break;
                    }
                }
                break;
            }
            case Printer:
            {
                state = state.withProperty( Properties.FACING, dir );
                switch( anim )
                {
                    case 0:
                    default:
                    {
                        state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.PrinterEmpty );
                        break;
                    }
                    case 1:
                    {
                        state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.PrinterTopFull );
                        break;
                    }
                    case 2:
                    {
                        state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.PrinterBottomFull );
                        break;
                    }
                    case 3:
                    {
                        state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.PrinterBothFull );
                        break;
                    }
                }
                break;
            }
            case WirelessModem:
            {
                switch( dir )
                {
                    case UP:
                    {
                        state = state.withProperty( Properties.FACING, Direction.NORTH );
                        switch( anim )
                        {
                            case 0:
                            default:
                            {
                                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemUpOff );
                                break;
                            }
                            case 1:
                            {
                                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemUpOn );
                                break;
                            }
                        }
                        break;
                    }
                    case DOWN:
                    {
                        state = state.withProperty( Properties.FACING, Direction.NORTH );
                        switch( anim )
                        {
                            case 0:
                            default:
                            {
                                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemDownOff );
                                break;
                            }
                            case 1:
                            {
                                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemDownOn );
                                break;
                            }
                        }
                        break;
                    }
                    default:
                    {
                        state = state.withProperty( Properties.FACING, dir );
                        switch( anim )
                        {
                            case 0:
                            default:
                            {
                                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemOff );
                                break;
                            }
                            case 1:
                            {
                                state = state.withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemOn );
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case Speaker:
            {
                state = state.withProperty( Properties.FACING, dir );
                break;
            }
            case Monitor:
            case AdvancedMonitor:
            {
                Direction front;
                int xIndex, yIndex, width, height;
                if( tile != null && tile instanceof TileMonitor )
                {
                    TileMonitor monitor = (TileMonitor)tile;
                    dir = monitor.getDirection();
                    front = monitor.getFront();
                    xIndex = monitor.getXIndex();
                    yIndex = monitor.getYIndex();
                    width = monitor.getWidth();
                    height = monitor.getHeight();
                }
                else
                {
                    dir = Direction.NORTH;
                    front = Direction.NORTH;
                    xIndex = 0;
                    yIndex = 0;
                    width = 1;
                    height = 1;
                }

                BlockPeripheralVariant baseVariant;
                if( front == Direction.UP )
                {
                    baseVariant = (type == PeripheralType.AdvancedMonitor) ?
                        BlockPeripheralVariant.AdvancedMonitorUp :
                        BlockPeripheralVariant.MonitorUp;
                }
                else if( front == Direction.DOWN )
                {
                    baseVariant = (type == PeripheralType.AdvancedMonitor) ?
                        BlockPeripheralVariant.AdvancedMonitorDown :
                        BlockPeripheralVariant.MonitorDown;
                }
                else
                {
                    baseVariant = (type == PeripheralType.AdvancedMonitor) ?
                        BlockPeripheralVariant.AdvancedMonitor :
                        BlockPeripheralVariant.Monitor;
                }

                int subType;
                if( width == 1 && height == 1 )
                {
                    subType = 0;
                }
                else if( height == 1 )
                {
                    if( xIndex == 0 )
                    {
                        subType = 1;
                    }
                    else if( xIndex == width - 1 )
                    {
                        subType = 3;
                    }
                    else
                    {
                        subType = 2;
                    }
                }
                else if( width == 1 )
                {
                    if( yIndex == 0 )
                    {
                        subType = 6;
                    }
                    else if( yIndex == height - 1 )
                    {
                        subType = 4;
                    }
                    else
                    {
                        subType = 5;
                    }
                }
                else
                {
                    if( xIndex == 0 )
                    {
                        subType = 7;
                    }
                    else if( xIndex == width - 1 )
                    {
                        subType = 9;
                    }
                    else
                    {
                        subType = 8;
                    }
                    if( yIndex == 0 )
                    {
                        subType += 6;
                    }
                    else if( yIndex < height - 1 )
                    {
                        subType += 3;
                    }
                }

                state = state.withProperty( Properties.FACING, dir );
                state = state.withProperty( Properties.VARIANT,
                    BlockPeripheralVariant.values()[ baseVariant.ordinal() + subType ]
                );
                break;
            }
        }
        return state;
    }

    @Override
    public BlockState getDefaultBlockState( PeripheralType type, Direction placedSide )
    {
        switch( type )
        {
            case DiskDrive:
            default:
            {
                BlockState state = getDefaultState().withProperty( Properties.VARIANT, BlockPeripheralVariant.DiskDriveEmpty );
                if( placedSide.getAxis() != Direction.Axis.Y )
                {
                    return state.withProperty( Properties.FACING, placedSide );
                }
                else
                {
                    return state.withProperty( Properties.FACING, Direction.NORTH );
                }
            }
            case WirelessModem:
            {
                Direction dir = placedSide.getOpposite();
                if( dir == Direction.DOWN )
                {
                    return getDefaultState()
                        .withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemDownOff )
                        .withProperty( Properties.FACING, Direction.NORTH );
                }
                else if( dir == Direction.UP )
                {
                    return getDefaultState()
                        .withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemUpOff )
                        .withProperty( Properties.FACING, Direction.NORTH );
                }
                else
                {
                    return getDefaultState()
                        .withProperty( Properties.VARIANT, BlockPeripheralVariant.WirelessModemOff )
                        .withProperty( Properties.FACING, dir );
                }
            }
            case Monitor:
            {
                return getDefaultState().withProperty( Properties.VARIANT, BlockPeripheralVariant.Monitor );
            }
            case Printer:
            {
                return getDefaultState().withProperty( Properties.VARIANT, BlockPeripheralVariant.PrinterEmpty );
            }
            case AdvancedMonitor:
            {
                return getDefaultState().withProperty( Properties.VARIANT, BlockPeripheralVariant.AdvancedMonitor );
            }
            case Speaker:
            {
                return getDefaultState().withProperty( Properties.VARIANT, BlockPeripheralVariant.Speaker );
            }
        }
    }

    @Override
    public PeripheralType getPeripheralType( int damage )
    {
        return ((ItemPeripheral)Item.getItemFromBlock(this)).getPeripheralType( damage );
    }

    @Override
    public PeripheralType getPeripheralType( BlockState state )
    {
        return state.getValue( Properties.VARIANT ).getPeripheralType();
    }

    @Override
    public TilePeripheralBase createTile( PeripheralType type )
    {
        switch( type )
        {
            case DiskDrive:
            default:
            {
                return new TileDiskDrive();
            }
            case WirelessModem:
            {
                return new TileWirelessModem();
            }
            case Monitor:
            case AdvancedMonitor:
            {
                return new TileMonitor();
            }
            case Printer:
            {
                return new TilePrinter();
            }
            case Speaker:
            {
                return new TileSpeaker();
            }
        }
    }

    @Override
    public void onBlockPlacedBy( World world, BlockPos pos, BlockState state, LivingEntity player, @Nonnull ItemStack stack )
    {
        // Not sure why this is necessary
        TileEntity tile = world.getTileEntity( pos );
        if( tile != null && tile instanceof TilePeripheralBase )
        {
            tile.setWorld( world ); // Not sure why this is necessary
            tile.setPos( pos ); // Not sure why this is necessary
        }

        switch( getPeripheralType( state ) )
        {
            case Speaker:
            case DiskDrive:
            case Printer:
            {
                Direction dir = DirectionUtil.fromEntityRot( player );
                setDirection( world, pos, dir );
                if( stack.hasDisplayName() && tile != null && tile instanceof TilePeripheralBase )
                {
                    TilePeripheralBase peripheral = (TilePeripheralBase)tile;
                    peripheral.setLabel( stack.getDisplayName() );
                }
                break;
            }
            case Monitor:
            case AdvancedMonitor:
            {
                if( tile != null && tile instanceof TileMonitor )
                {
                    int direction = DirectionUtil.fromEntityRot( player ).getIndex();
                    if( player.rotationPitch > 66.5F )
                    {
                        direction += 12;
                    }
                    else if( player.rotationPitch < -66.5F )
                    {
                        direction += 6;
                    }

                    TileMonitor monitor = (TileMonitor)tile;
                    if( world.isRemote )
                    {
                        monitor.setDir( direction );
                    }
                    else
                    {
                        monitor.contractNeighbours();
                        monitor.setDir( direction );
                        monitor.contract();
                        monitor.expand();
                    }
                }
                break;
            }
        }
    }

    @Override
    @Deprecated
    public final boolean isOpaqueCube( BlockState state )
    {
        PeripheralType type = getPeripheralType( state );
        return type == PeripheralType.DiskDrive || type == PeripheralType.Printer
            || type == PeripheralType.Monitor || type == PeripheralType.AdvancedMonitor
            || type == PeripheralType.Speaker;
    }

    @Override
    @Deprecated
    public final boolean isFullCube( BlockState state )
    {
        return isOpaqueCube( state );
    }

    @Override
    @Deprecated
    public boolean isFullBlock( BlockState state )
    {
        return isOpaqueCube( state );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape( IBlockAccess world, BlockState state, BlockPos pos, Direction side )
    {
        return isOpaqueCube( state ) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean causesSuffocation(BlockState state)
    {
        // This normally uses the default state 
        return blockMaterial.blocksMovement() && state.isOpaqueCube();
    }

    @Override
    @Deprecated
    public int getLightOpacity( BlockState state )
    {
        // This normally uses the default state
        return isOpaqueCube( state ) ? 255 : 0;
    }
}
