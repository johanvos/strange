package org.redfx.strange;

import java.util.List;
import org.redfx.strange.gate.SingleQubitGate;

/**
 *
 * @author johan
 */
public interface ControlledGate extends Gate {

    public int getControllQubitIndex();

    default public List<Integer> getControllIndexes() {
        return List.of(getControllQubitIndex());
    }

    public int getRootGateIndex();

    public Gate getRootGate();
    
    default public int getSecondControlQubitIndex() {return -1;};
    
    default void setRootGateIndex(int i) {}

    public static ControlledGate createControlledGate(Gate g, int idx) {
        return createControlledGate(g, List.of(idx));
    }

    public static ControlledGate createControlledGate(Gate g, List<Integer> idx) {
        System.err.println("CREATE CONTROLLED GATE for "+g);
        return new ControlledGate() {
            
            @Override
            public int getControllQubitIndex() {
                return idx.get(0);
            }

            @Override
            public List<Integer> getControllIndexes() {
                return idx;
            }
            public void setRootGateIndex(int i) {
                g.setMainQubitIndex(i);
            }
            @Override
            public int getRootGateIndex() {
                return g.getMainQubitIndex();
            }

            @Override
            public Gate getRootGate() {
                return g;
            }

            @Override
            public void setMainQubitIndex(int idx) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public int getMainQubitIndex() {
                return g.getMainQubitIndex();
            }

            @Override
            public void setAdditionalQubit(int idx, int cnt) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public List<Integer> getAffectedQubitIndexes() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public int getHighestAffectedQubitIndex() {
                return Integer.max(g.getHighestAffectedQubitIndex(), idx.get(0));
            }

            @Override
            public String getCaption() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public String getName() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public String getGroup() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public Complex[][] getMatrix() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public int getSize() {
                return (g.getSize()+1);
            }

            @Override
            public void setInverse(boolean inv) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            
            @Override
            public String toString() {
                return "ControlledGate with ctrl at "+idx+" and gate = "+g;
            }
        };
    }    
}
