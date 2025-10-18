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
import java.util.Collections;
import java.util.List;
import org.redfx.strange.Qubit;

/**
 *
 * This class describe a Gate that operates on three qubits. In a single
 * <code>Step</code>, there should not be two Gates that act on the same qubit.
 *
 * @author johan
 * @version $Id: $Id
 */
public abstract class ThreeQubitGate implements Gate {
    
    private int first;
    private int second;
    private int third;

    private Qubit mainQubit;
    private Qubit secondQubit;
    private Qubit thirdQubit;

    /**
     * <p>Constructor for ThreeQubitGate.</p>
     */
    public ThreeQubitGate() {}
    
    /**
     * <p>Constructor for ThreeQubitGate.</p>
     *
     * @param first a int
     * @param second a int
     * @param third a int
     */
    public ThreeQubitGate (int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    @Override
    public Qubit getQubit() {
        return mainQubit;
    }

    @Override
    public Qubit getSecondQubit() {
        return secondQubit;
    }

    @Override
    public Qubit getThirdQubit() {
        return thirdQubit;
    }

    public ThreeQubitGate (Qubit mainQubit, Qubit secondQubit, Qubit thirdQubit) {
        this.mainQubit = mainQubit;
        this.secondQubit = secondQubit;
        this.thirdQubit = thirdQubit;
    }

    
    /** {@inheritDoc} */
    @Override
    public void setMainQubitIndex(int idx) {
        this.first = idx;
    }
    
    /** {@inheritDoc} */
    @Override
    public int getMainQubitIndex() {
        return this.first;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        if ((cnt < 1) || (cnt > 2)) { 
            System.err.println("Can't set qubit "+cnt+" as additional on a three qubit gate");
        }
        if (cnt == 1) this.second = idx;
        if (cnt == 2) this.third = idx;
    }

    /**
     * <p>getMainQubit.</p>
     *
     * @return a int
     */
    public int getMainQubit() {
        return this.first;
    }
    
    /**
     * <p>getSecondQubit.</p>
     *
     * @return a int
     */
    public int getSecondQubitIndex() {
        return this.second;
    }
    
    /**
     * <p>getThirdQubit.</p>
     *
     * @return a int
     */
    public int getThirdQubitIndex() {
        return this.third;
    }
        
    /** {@inheritDoc} */
    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return Arrays.asList(first, second, third);
    }

    /** {@inheritDoc} */
    @Override
    public int getHighestAffectedQubitIndex() {
        return Collections.max(getAffectedQubitIndexes());
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
        return "ThreeQubit";
    }
    
    
    /** {@inheritDoc} */
    @Override public String toString() {
        return "Gate acting on qubits "+first+", "+second+" and "+third+" and caption "+getCaption();
    }
    
}
