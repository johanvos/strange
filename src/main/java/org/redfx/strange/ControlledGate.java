package org.redfx.strange;

/**
 *
 * @author johan
 */
public interface ControlledGate {

    public int getControllQubitIndex();
    
    public int getRootGateIndex();

    public Gate getRootGate();
}
