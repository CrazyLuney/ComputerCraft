/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.common;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.common.IDirectionalTile;
import dan200.computercraft.shared.peripheral.PeripheralType;
import net.minecraft.util.Direction;

public interface IPeripheralTile extends IDirectionalTile
{
    PeripheralType getPeripheralType();
    IPeripheral getPeripheral( Direction side );
    String getLabel();
}
