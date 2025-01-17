/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.computer.blocks;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.apis.CommandAPI;
import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class TileCommandComputer extends TileComputer
{
    public class CommandSender extends CommandBlockLogic
    {
        private Map<Integer, String> m_outputTable;

        public CommandSender()
        {
            m_outputTable = new HashMap<>();
        }

        public void clearOutput()
        {
            m_outputTable.clear();
        }

        public Map<Integer, String> getOutput()
        {
            return m_outputTable;
        }

        public Map<Integer, String> copyOutput()
        {
            return new HashMap<>( m_outputTable );
        }

        // ICommandSender

        @Nonnull
        @Override
        public ITextComponent getDisplayName()
        {
            IComputer computer = TileCommandComputer.this.getComputer();
            if( computer != null )
            {
                String label = computer.getLabel();
                if( label != null )
                {
                    return new StringTextComponent( computer.getLabel() );
                }
            }
            return new StringTextComponent( "@" );
        }

        @Override
        public void sendMessage( @Nonnull ITextComponent chatComponent )
        {
            m_outputTable.put( m_outputTable.size() + 1, chatComponent.getUnformattedText() );
        }

        @Override
        public boolean canUseCommand( int level, String command )
        {
            return level <= 2;
        }

        @Nonnull
        @Override
        public BlockPos getPosition()
        {
            return TileCommandComputer.this.getPos();
        }

        @Nonnull
        @Override
        public Vec3d getPositionVector()
        {
            BlockPos pos = getPosition();
            return new Vec3d( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 );
        }

        @Nonnull
        @Override
        public World getEntityWorld()
        {
            return TileCommandComputer.this.getWorld();
        }

        @Override
        public MinecraftServer getServer()
        {
            return TileCommandComputer.this.getWorld().getMinecraftServer();
        }

        @Override
        public Entity getCommandSenderEntity()
        {
            return null;
        }

        // CommandBlockLogic members intentionally left empty
        // The only reason we extend it at all is so that "gameRule commandBlockOutput" applies to us

        @Override
        public void updateCommand()
        {
        }

        @Override
        public int getCommandBlockType()
        {
            return 0;
        }

        @Override
        public void fillInInfo( @Nonnull ByteBuf buf )
        {
        }
    }

    private CommandSender m_commandSender;

    public TileCommandComputer()
    {
        m_commandSender = new CommandSender();
    }

    @Override
    public Direction getDirection()
    {
        BlockState state = getBlockState();
        return state.getValue( BlockCommandComputer.Properties.FACING );
    }

    @Override
    public void setDirection( Direction dir )
    {
        if( dir.getAxis() == Direction.Axis.Y )
        {
            dir = Direction.NORTH;
        }
        setBlockState( getBlockState().withProperty( BlockCommandComputer.Properties.FACING, dir ) );
        updateInput();
    }

    public CommandSender getCommandSender()
    {
        return m_commandSender;
    }

    @Override
    protected ServerComputer createComputer( int instanceID, int id )
    {
        ServerComputer computer = super.createComputer( instanceID, id );
        computer.addAPI( new CommandAPI( this ) );
        return computer;
    }

    @Override
    public boolean isUsable( PlayerEntity player, boolean ignoreRange )
    {
        MinecraftServer server = player.getServer();
        if( server == null || !server.isCommandBlockEnabled() )
        {
            player.sendMessage( new TranslationTextComponent( "advMode.notEnabled" ) );
            return false;
        }
        else if( ComputerCraft.canPlayerUseCommands( player ) && player.capabilities.isCreativeMode )
        {
            return super.isUsable( player, ignoreRange );
        }
        else
        {
            player.sendMessage( new TranslationTextComponent( "advMode.notAllowed" ) );
            return false;
        }
    }
}
