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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author johan
 */
public class PermutationGate implements Gate {

    private int a;
    private int b;
    private int n;
    
    private Complex[][] m;
    private List<Integer> affected = new LinkedList<>();
    
    @Override
    public int getMainQubitIndex() {
        return this.a;
    }
    
    public PermutationGate(int a, int b, int n) {
        assert(a < n);
        assert(b < n);
        this.a = a;
        this.b = b;
        this.n = n;
        for (int i =0 ; i < n; i++) {
            affected.add(i);
        }
    }
    
    public int getIndex1() {
        return a;
    }
    
    public int getIndex2() {
        return b;
    }
    
    @Override
    public void setMainQubitIndex(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return affected;
    }

    @Override
    public int getHighestAffectedQubitIndex() {
        return Collections.max(affected);
    }

    @Override
    public String getCaption() {
        return "P";
    }

    @Override
    public String getName() {
        return "permutation gate";
    }

    @Override
    public String getGroup() {
        return "permutation";
    }

    @Override
    public Complex[][] getMatrix() {
        throw new RuntimeException ("No matrix required for Permutation");
//        return m;
    }
    
    @Override
    public int getSize() {
        return 2;
    }
    
    @Override
    public String toString() {
        return "Perm "+a+", "+b;
    }
}
