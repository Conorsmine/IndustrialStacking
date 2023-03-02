package com.conorsmine.net.industrialstacking.machinestack;

public interface Powerable {

    /**
     * To increase the machines speed, change the amount of energy the machine
     * can input. To calculate it correctly though, a value is needed, before
     * all other machines increase the power input.
     * @return Power input, before recalculation
     */
    long getRegularMachinePower();

    /**
     * @return The amount of power required by the machine stack.
     */
    long getMachineStackPower();
}
