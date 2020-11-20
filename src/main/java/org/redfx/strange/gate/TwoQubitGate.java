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

import org.redfx.strange.Gate;
import java.util.Arrays;
import java.util.List;

/**
 *
 * This class describe a Gate that operates on two qubits. In a single
 * <code>Step</code>, there should not be two Gates that act on the same qubit.
 * @author johan
 */
public abstract class TwoQubitGate implements Gate {
    
    private int first;
    private int second;
    private int highest = -1;
    private boolean inverse;
    
    public TwoQubitGate() {}
    
    public TwoQubitGate (int first, int second) {
        this.first = first;
        this.second = second;
        this.highest = Math.max(first, second);
    }

    @Override
    public void setMainQubitIndex(int idx) {
        this.first = idx;
    }
    
    @Override
    public int getMainQubitIndex() {
        return this.first;
    }
    
    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        this.second = idx;
    }


    public int getSecondQubitIndex() {
        return this.second;
    }

    public void setHighestAffectedQubitIndex(int v) {
        this.highest = v;
    }
        
    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return Arrays.asList(first, second);
    }

    @Override
    public int getHighestAffectedQubitIndex() {
        return highest;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
    @Override
    public String getCaption() {
        return getName();    
    }
    
    @Override
    public String getGroup() {
        return "TwoQubit";
    }
     
    @Override
    public int getSize() {
        return 2;
    }
    
    @Override
    public void setInverse(boolean v) {
        this.inverse = v;
    }
    
    @Override public String toString() {
        return "Gate acting on qubits "+first+" and "+second+" and caption "+getCaption();
    }
    
}
