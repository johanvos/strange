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
package org.redfx.strange.gate;

import org.redfx.strange.Complex;
import org.redfx.strange.Gate;

import java.util.Collections;
import java.util.List;
import org.redfx.strange.Qubit;

/**
 *
 * This class describe a Gate that operates on a single qubit only.
 *
 * @author johan
 * @version $Id: $Id
 */
public abstract class SingleQubitGate implements Gate {
    
    protected int idx;
    Qubit qubit;
    private boolean inverse;
    
    /**
     * <p>Constructor for SingleQubitGate.</p>
     */
    public SingleQubitGate() {}

    public SingleQubitGate(Qubit q) {
        this.qubit = q;
    }
    /**
     * <p>Constructor for SingleQubitGate.</p>
     *
     * @param idx a int
     */
    public SingleQubitGate (int idx) {
        this.idx = idx;
    }

    public Qubit getQubit() {
        return qubit;
    }

    /** {@inheritDoc} */
    @Override
    public int getMainQubitIndex() {
        return this.idx;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setMainQubitIndex(int idx) {
        this.idx = idx;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        throw new RuntimeException("A SingleQubitGate can not have additional qubits");
    }

    /** {@inheritDoc} */
    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return Collections.singletonList(idx);
    }

    /** {@inheritDoc} */
    @Override
    public int getHighestAffectedQubitIndex() {
        return idx;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return getName();    
    }
    
    /** {@inheritDoc} */
    @Override
    public String getGroup() {
        return "SingleQubit";
    }
    
    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return 1;
    }
    
    /** {@inheritDoc} */
    @Override
    public abstract Complex[][] getMatrix();
    
    /** {@inheritDoc} */
    @Override
    public void setInverse(boolean v) {
        this.inverse = v;
    }
    
    /** {@inheritDoc} */
    @Override public String toString() {
        return "Gate with index "+idx+" and caption "+getCaption();
    }
    
}
