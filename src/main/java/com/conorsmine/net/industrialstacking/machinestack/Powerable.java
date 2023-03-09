package com.conorsmine.net.industrialstacking.machinestack;

public interface Powerable {

    /**
     * @return The power required for one machine with the given upgrades.
     */
    long getRegularMachinePower();

    /**
     * @return The amount of power required by the machine stack.
     */
    long getMachineStackPower();
}
