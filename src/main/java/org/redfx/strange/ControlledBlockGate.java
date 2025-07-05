/*-
 * #%L
 * Strange
 * %%
 * Copyright (C) 2020 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strange;

import org.redfx.strange.gate.PermutationGate;
import org.redfx.strange.local.Computations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.redfx.strange.gate.SingleQubitGate;

/**
 *
 * A Gate describes an operation on one or more qubits.
 *
 * @author johan
 * @param <T> type of the Block
 * @version $Id: $Id
 */
public class ControlledBlockGate<T> extends BlockGate {

    private int control;
    private int size;
    private int high = -1;
    private int haq = -1;
    int low = 0;
    private Complex[][] matrix = null;
    
    /**
     * <p>Constructor for ControlledBlockGate.</p>
     */
    protected ControlledBlockGate() {
    }
    
    /**
     * Create a controlled blockgate
     *
     * @param bg the block gate
     * @param idx the start-index of the block gate
     * @param control the index of the control qubit
     */
    public ControlledBlockGate(BlockGate bg, int idx, int control) {
        super();
        if (bg instanceof ControlledBlockGate cbg) {
            Step s = new Step(cbg);
            Block b = new Block(bg.getSize());
            b.addStep(new Step(cbg));
            this.setBlock(b);
        } else {
            this.setBlock(bg.getBlock());
        }
        this.setMainQubitIndex(idx);
        this.control = control;
        System.err.println("CREATED CBG");
    }
    
    public int getControlIndex() {
        return this.control;
    }
    /**
     * Create a block
     *
     * @param block a {@link org.redfx.strange.Block} object
     * @param idx a int
     * @param control the control qubit
     */
    public ControlledBlockGate (Block block, int idx, int control) {
//        List<Step> steps = block.getSteps();
//        Block cBlock = new Block(block.getNQubits()+1);
//        for (Step step : steps) {
//            ControlledGate.
//        }
//        System.err.println("");
        super(block, idx);
        this.control = control;
        if (control > idx) {
            this.haq = idx + block.getNQubits();
        } else {
            this.haq = idx+block.getNQubits() -1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Integer> getAffectedQubitIndexes() {
        ArrayList answer = new ArrayList(super.getAffectedQubitIndexes());
        answer.add(control);
        return answer;
    }

    /** {@inheritDoc} */
    @Override
    public int getHighestAffectedQubitIndex() {
        if (high < 0) calculateHighLow();
        return this.haq;
    }

    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return "CB";
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "CBlockGate";
    }

    /** {@inheritDoc} */
    @Override
    public String getGroup() {
        return "CBlockGroup";
    }
    
    /**
     * <p>getControlQubit.</p>
     *
     * @return a int
     */
    public int getControlQubit() {
        return this.control;
    }

    /**
     * <p>calculateHighLow.</p>
     */
    public void calculateHighLow() {
        this.high = control;
        int gap = control - idx;
        int bs = block.getNQubits();
        low = 0;
         if (control > idx) {
             low =idx;
                if (gap < bs) {
                    throw new IllegalArgumentException("Can't have control at " + control + " for gate with size " + bs + " starting at " + idx);
                }
                if (gap > bs) {
                    high = control;
                }
            } else {
                low = control;
                high = idx + bs - 1;
         }
         size = high - low + 1;
    }
    
    /**
     * <p>Getter for the field <code>low</code>.</p>
     *
     * @return a int
     */
    public int getLow() {
        return this.low;
    }
    
    /**
     * <p>correctHigh.</p>
     *
     * @param h a int
     */
    public void correctHigh(int h) {
        this.high = h;
    }
    
    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix() {
        return getMatrix(null);
    }

    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix(QuantumExecutionEnvironment qee) {
        System.err.println("GETMATRIX");
        if (matrix == null) {
            int low = 0;
            this.high = control;
            this.size = super.getSize() + 1;
            int gap = control - idx;
            List<PermutationGate> perm = new LinkedList<>();
            int bs = block.getNQubits();
            if (control > idx) {
                if (gap < bs) {
                    throw new IllegalArgumentException("Can't have control at " + control + " for gate with size " + bs + " starting at " + idx);
                }
                low = idx;
                if (gap > bs) {
                    high = control;
                    size = high - low + 1;
                    PermutationGate pg = new PermutationGate(control-low, control-low - gap + bs, size);
                    perm.add(pg);
                }
            } else {
                low = control;
                high = idx + bs - 1;
                size = high - low + 1;
                for (int i = 0; i < size - 1; i++) {
                    PermutationGate pg = new PermutationGate(i, i + 1, size);
                    perm.add(0,pg);
                }
            }
            Complex[][] part = block.getMatrix(qee);
          
            int dim = part.length;
            matrix = Computations.createIdentity(2 * dim);
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    matrix[i + dim][j + dim] = part[i][j];
                }
            }
        } else {
            System.err.println("Matrix was cached");
        }
        return matrix;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean hasOptimization() {
        System.err.println("HASOPT");
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Complex[] applyOptimize(Complex[] v) {
        
        System.err.println("[CBG] appopt for v = ");
        Complex.printArray(v);
        for (Step step : block.getSteps()) {
            System.err.println("STEP "+step);

            List<Gate> gates = step.getGates();
                     v =     Computations.calculateNewState(gates, v, size);

//            for (Gate gate: gates) {
//                Gate singleGate = new SingleControlledGate(this.control, this.getMainQubitIndex()+ gate.getMainQubitIndex(), gate);
//                v = Computations.simpleNextProb(singleGate, v);
//                System.err.println("After this gate, probs = ");
//                Complex.printArray(v);
//            }
        }
        System.err.println("[CBG] done");
        return v;
    }

    static class SingleControlledGate extends SingleQubitGate implements ControlledGate {

        private final int ctrl;
        private final int index;
        private final Gate root;

        SingleControlledGate(int ctrl, int index, Gate root) {
            System.err.println("Created SCG with ctr at "+ctrl+" and index = "+ index);
            this.ctrl = ctrl;
            this.index = index;
            this.root = root;
        }
        @Override
        public Complex[][] getMatrix() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public int getControllQubitIndex() {
            return ctrl;
        }

        @Override
        public List<Integer> getControllIndexes() {
            return List.of(ctrl);
        }

        @Override
        public int getRootGateIndex() {
            return index;
        }

        @Override
        public Gate getRootGate() {
            return root;
        }
        
    }
    /**
     * <p>Getter for the field <code>size</code>.</p>
     *
     * @return a int
     */
    public int getSize() {
        return block.getNQubits()+1;
    }
    
    /** {@inheritDoc} */
    @Override public String toString() {
        return "ControlledBlockGate with control at " + control+" for "+super.toString();
    }
    
}
