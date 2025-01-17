/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.network;
import net.minecraft.entity.player.PlayerEntity;

public interface INetworkedThing
{
    void handlePacket( ComputerCraftPacket packet, PlayerEntity sender );
}
