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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * A Gate describes an operation on one or more qubits.
 *
 * @author johan
 * @param <T> Type of the Gate
 * @version $Id: $Id
 */
public class BlockGate<T extends Gate> implements Gate {

    protected Block block;
    protected int idx;
    protected boolean inverse = false;
    
    /**
     * <p>Constructor for BlockGate.</p>
     */
    protected BlockGate() {
    }
    
    /**
     * Create a block
     *
     * @param block a {@link org.redfx.strange.Block} object
     * @param idx a int
     */
    public BlockGate (Block block, int idx) {
        this.block = block;
        this.idx = idx;
    }
    
    /**
     * <p>Setter for the field <code>block</code>.</p>
     *
     * @param b a {@link org.redfx.strange.Block} object
     */
    protected final void setBlock(Block b) {
        this.block = b;
    }
    
    /**
     * <p>Getter for the field <code>block</code>.</p>
     *
     * @return a {@link org.redfx.strange.Block} object
     */
    public final Block getBlock() {
        return this.block;
    }
    
    /**
     * <p>setIndex.</p>
     *
     * @param idx a int
     */
    protected final void setIndex(int idx) {
        this.idx = idx;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setMainQubitIndex(int idx) {
        this.idx = idx;
    }

    /** {@inheritDoc} */
    @Override
    public int getMainQubitIndex() {
        return idx;
    }

    /** {@inheritDoc} */
    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /** {@inheritDoc} */
    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return IntStream.range(idx, idx+block.getNQubits()).boxed().collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public int getHighestAffectedQubitIndex() {
        int answer = block.getNQubits()+idx-1;
        return answer;
    }

    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return "B";
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "BlockGate";
    }

    /** {@inheritDoc} */
    @Override
    public String getGroup() {
        return "BlockGroup";
    }

    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix() {
        return getMatrix(null);
    }
    
    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix(QuantumExecutionEnvironment qee) {
        Complex[][] answer = block.getMatrix(qee);
        if (inverse) {
            answer = Complex.conjugateTranspose(answer);
        }
        return answer;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setInverse(boolean inv) {
        this.inverse = inv;
    }
    
    /**
     * <p>inverse.</p>
     *
     * @return a T object
     */
    public T inverse() {
        setInverse(!this.inverse);
        return (T) this;
    }
    
    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return block.getNQubits();
    }
        
    /** {@inheritDoc} */
    @Override
    public boolean hasOptimization() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Complex[] applyOptimize(Complex[] v) {
        return block.applyOptimize(v, inverse);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return "Gate for block "+block+", size = "+getSize()+", inv = "+inverse;
    }

    
}
