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
package org.redfx.strange.test;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.gate.MulModulus;
import org.redfx.strange.gate.X;


/**
 *
 * @author johan
 */
public class MulModulusTest extends BaseGateTests {

    static final double D = 0.000000001d;

  //  @Test
    public void m1x2() {
        int n = 2;
        int dim = 2 * (n+1) + 1;
        int mul = 2;
        int mod = 3;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(3));
        p.addStep(prep);
        
        Step modstep = new Step(new MulModulus(0,2,mul, mod));
        p.addStep(modstep);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();

        assertEquals(dim, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
    }
    
 //   @Test
    public void c0m1x2() {
        int n = 2;
        int dim = 2 * (n+1) + 1 + 1;
        int mul = 2;
        int mod = 3;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(4));
        p.addStep(prep);
        
        MulModulus mulModulus = new MulModulus(0,2,mul, mod);
        
        ControlledBlockGate cbg = new ControlledBlockGate(mulModulus, 1, 0);

        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(dim, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
    }
    
 //   @Test
    public void cx0m1x2() {
        int n = 2;
        int dim = 2 * (n+1) + 1 + 1;
        int mul = 2;
        int mod = 3;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0), new X(4));
        p.addStep(prep);
        
        MulModulus mulModulus = new MulModulus(0,2,mul, mod);
        
        ControlledBlockGate cbg = new ControlledBlockGate(mulModulus, 1, 0);

        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(dim, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(1, q[5].measure());
    }
    
  //  @Test
    public void ch0m1x2() {
        int n = 2;
        int dim = 2 * (n+1) + 1 + 1;
        int mul = 2;
        int mod = 3;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new Hadamard(0), new X(4));
        p.addStep(prep);
        
        MulModulus mulModulus = new MulModulus(0,2,mul, mod);
        
        ControlledBlockGate cbg = new ControlledBlockGate(mulModulus, 1, 0);

        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(dim, q.length);
        assertEquals(0, q[1].getProbability(),D);
        assertEquals(0, q[2].getProbability(),D);
        assertEquals(0, q[3].getProbability(),D);
        int a = q[0].measure();
        assertEquals(a, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals((a==0) ? 1 : 0, q[4].measure());
        assertEquals(a, q[5].measure());
    }
    
    @Test
    public void chhh0m1x2() {
        int n = 3;
        int dim = 3 * n + 3;
        int mul = 2;
        int mod = 3;
        Program p = new Program(dim);
        Step prep = new Step();
        for (int i = 0; i < n; i++) {
            prep.addGate(new Hadamard(i));
        }
        prep.addGates( new X(2*n+1));
        p.addStep(prep);
        
        MulModulus mulModulus = new MulModulus(0,n-1,mul, mod);
        
        ControlledBlockGate cbg = new ControlledBlockGate(mulModulus, n, 0);

        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(dim, q.length);
        for (int i = n; i < 2*n; i++) {
            assertEquals(0, q[i].getProbability(),D, "wrong value for qubit "+i);
        }
       
    }
    
//    @Test
    public void chhhh0m1x2() {
        int n = 4;
        int dim = 3 * n + 3;
        int mul = 2;
        int mod = 3;
        Program p = new Program(dim);
        Step prep = new Step();
        for (int i = 0; i < n; i++) {
            prep.addGate(new Hadamard(i));
        }
        prep.addGates( new X(2*n+1));
        p.addStep(prep);
        
        MulModulus mulModulus = new MulModulus(0,n-1,mul, mod);
        
        ControlledBlockGate cbg = new ControlledBlockGate(mulModulus, n, 0);

        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(dim, q.length);
        assertEquals(0, q[4].getProbability(),D);
        assertEquals(0, q[5].getProbability(),D);
        assertEquals(0, q[6].getProbability(),D);
        assertEquals(0, q[7].getProbability(),D);
        assertEquals(0, q[8].getProbability(),D);
       
    }
}
