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
import org.redfx.strange.Complex;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.gate.InvFourier;
import org.redfx.strange.gate.Mul;
import org.redfx.strange.gate.MulModulus;
import org.redfx.strange.gate.X;

/**
 *
 * @author johan
 */
public class ExpMulTests extends BaseGateTests {

    static final double D = 0.000000001d;
        
    
    @Test // 
    public void expmul3p3() { // 3^3 = 27 -> mod 8 = 3
        int length = 3;
        int N = 8;
        // q0 -> q2: x (3)
        // q3 -> q5: ancilla (0 before, 0 after)
        // q6 -> q8: result
        int a = 3;
        Program p = new Program(3 * length);
        Step prep = new Step(new X(0), new X(1));
        Step prepAnc = new Step(new X(2 * length));
        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = length - 1; i > -1; i--) {
//            int m = (int) Math.pow(a, 1 << i);
            int m = 1;
            for (int j = 0; j < 1 << i; j++) {
                m = m*a %N;
            }
            Mul mul = new Mul(length, 2 * length - 1, m);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, length, i);
            p.addStep(new Step(cbg));
        }
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[195].abssqr(), DELTA);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(1, q[6].measure());
        assertEquals(1, q[7].measure());
        assertEquals(0, q[8].measure());
    }
       
   @Test // 
    public void mul3x3() { // 3^3 = 27 -> mod 8 = 3
        int length = 3;
        // q0 -> q2: x (3)
        // q3 -> q5: ancilla (0 before, 0 after)
        // q6 -> q8: result
        int a = 3;
        Program p = new Program(3 * length);
        Step prep = new Step(new X(0), new X(1));
        Step prepAnc = new Step(new X(2 * length));
        p.addStep(prep);
        p.addStep(prepAnc);
        int i = 2;
        int m = 1;
        Mul mul = new Mul(length, 2 * length - 1, m);
        p.addStep(new Step(mul));

        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[67].abssqr(), DELTA);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(1, q[6].measure());
        assertEquals(0, q[7].measure());
        assertEquals(0, q[8].measure());
    }

    @Test // 
    public void mul1() { // test Mul that doesn't start at 0
        int length = 2;
        Program p = new Program(3 * length);
        Step prep = new Step(new X(0), new X(1));
        Step prepAnc = new Step(new X(2 * length));
        p.addStep(prep);
        p.addStep(prepAnc);
        int i = 2;
        int m = 1;
        Mul mul = new Mul(length, 2 * length - 1, m);
        p.addStep(new Step(mul));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
    }
    
    @Test // 
    public void expmul3p4() { // 3^4 = 81 -> mod 8 = 1
        int length = 3; 
        // q0 -> q2: x (4)
        // q3 -> q5: ancilla (0 before, 0 after)
        // q6 -> q8: result
        int a = 3;
        Program p = new Program(3 * length);
        Step prep = new Step(new X(2));
        Step prepAnc = new Step(new X(2 * length));
        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = length - 1; i > -1; i--) {
            int m = (int) Math.pow(a, 1 << i);
            Mul mul = new Mul(length, 2 * length - 1, m);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, length, i);
            p.addStep(new Step(cbg));
        }
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[68].abssqr(), DELTA);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(1, q[6].measure());
        assertEquals(0, q[7].measure());
        assertEquals(0, q[8].measure());
    }
    
    // @Test // 
    public void expmul3p4mod7() { // 3^4 = 81 -> mod 7 = 4
        int length = 3; 
        // q0 -> q2: 4
        // q3 -> q6: ancilla (0 before, 0 after)
        // q7 -> q10: result, start with 1
        int a = 3;
        int mod = 7;
                Program p = new Program(3 * length+3);
        Step prep = new Step(new X(2)); // exp = 4
        Step prepAnc = new Step(new X(7)); 

        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = length - 1; i > -1; i--) {
            int m = 1;
            for (int j = 0; j < 1 << i; j++) {
                m = m*a %mod;
            }
//            int m = (int) Math.pow(a, 1 << i);
            System.err.println("M = "+m);
            MulModulus mul = new MulModulus(length, 2 * length, m, mod);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, length, i);
            p.addStep(new Step(cbg));
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
      assertEquals(12, q.length);
        System.err.println("results: ");
        for (int i = 0; i < 12; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
        }
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
        assertEquals(0, q[7].measure());
        assertEquals(0, q[8].measure());
        assertEquals(1, q[9].measure());
        assertEquals(0, q[10].measure());
        assertEquals(0, q[11].measure());
    }
    
    @Test // 
    public void expmul3p4mod7gen() { // 3^4 = 81 -> mod 7 = 4
        Qubit[] q = expmod(3,7,3);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(1, q[5].measure());
        assertEquals(0, q[6].measure());
        assertEquals(0, q[7].measure());
        assertEquals(0, q[8].measure());
        assertEquals(0, q[9].measure());
        assertEquals(0, q[10].measure());
    }
    
    @Test // 
    public void expmul7p4mod15gen() { // 3^4 = 81 -> mod 7 = 4
        Qubit[] q = expmod(7,15,4);
    }
    
    private Qubit[] expmod(int a, int mod, int length) {
        // q0 -> q2: 4
        // q3 -> q5: ancilla (0 before, 0 after)
        // q6 -> q8: result
        Program p = new Program(3 * length+2);
        Step prep = new Step(new X(2)); // exp = 4
        Step prepAnc = new Step(new X(length)); 
        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = length - 1; i > -1; i--) {
            int m = 1;
            for (int j = 0; j < 1 << i; j++) {
                m = m*a %mod;
            }
            MulModulus mul = new MulModulus(length, 2 * length-1, m, mod);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, length, i);
            p.addStep(new Step(cbg));
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        return q;
    }
    
    
    @Test
    public void testexpmodH37() {
        //  H { 1 x 3 ^ A mod 7}
        int mod = 7;
        int a = 3;
        int length = (int) Math.ceil(Math.log(mod) / Math.log(2));
        int offset = 1 ;
        int dim0 = 1 << offset;
        assertEquals(dim0, 2);
        Program p = new Program(2 * length + 2 + offset);
        Step prep = new Step();

        prep.addGate(new X(3));
        
        Step prepAnc = new Step(new X(length + 1 + offset));
        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = offset - 1; i > - 1; i--) {
            int m = 1;
            for (int j = 0; j < 1 << i; j++) {
                m = m * a % mod;
            }
//            System.err.println("Create MulModulus, i = "+i+", m = "+m+", a = "+a+", MOD = "+mod);
            MulModulus mul = new MulModulus(length, 2 * length-1, m, mod);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, offset, i);
            p.addStep(new Step(cbg));
        }
        p.addStep(new Step(new InvFourier(offset, 0)));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        Qubit[] q = result.getQubits();
        int answer = 0;
        double[] amps = new double[dim0];
        for (int i = 0; i < probs.length; i++) {
            amps[i%dim0] = amps[i%dim0] + probs[i].abssqr();
        }
        for (int i = 0; i < dim0; i ++) {
//            System.err.println("AMP["+i+"] = "+amps[i]);
            assertEquals(amps[i], .5, 0.001);
        }
    }
    
    @Test
    public void testexpmodHH37() {
        //  H { 1 x 3 ^ A mod 7}
        int mod = 7;
        int a = 3;
        int length = (int) Math.ceil(Math.log(mod) / Math.log(2));
        int offset = 2 ;
        int dim0 = 1 << offset;
        assertEquals(dim0, 4);
        Program p = new Program(2 * length + 2 + offset);
        Step prep = new Step();
        for (int i = 0; i < offset; i++) {
            prep.addGate(new Hadamard(i));
        }
        
        Step prepAnc = new Step(new X(length + 1 + offset));
        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = offset - 1; i > - 1; i--) {
            int m = 1;
            for (int j = 0; j < 1 << i; j++) {
                m = m * a % mod;
            }
//            System.err.println("Create MulModulus, i = "+i+", m = "+m+", a = "+a+", MOD = "+mod);
            MulModulus mul = new MulModulus(length, 2 * length-1, m, mod);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, offset, i);
            p.addStep(new Step(cbg));
        }
        p.addStep(new Step(new InvFourier(offset, 0)));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        Qubit[] q = result.getQubits();
        int answer = 0;
        double[] amps = new double[dim0];
        for (int i = 0; i < probs.length; i++) {
            amps[i%dim0] = amps[i%dim0] + probs[i].abssqr();
        }
        for (int i = 0; i < dim0; i ++) {
         //   System.err.println("AMP["+i+"] = "+amps[i]);
            assertEquals(amps[i], .25, 0.001);
        }
    }
    
    @Test
    public void period7_15() {
        int p = measurePeriod(7, 15);
        System.err.println("P = "+p);
    }
    
    
    private int measurePeriod(int a, int mod) {         
        int length = (int) Math.ceil(Math.log(mod) / Math.log(2));
        int offset = length;
        Program p = new Program(2 * length + 2 + offset);
        Step prep = new Step();
        for (int i = 0; i < offset; i++) {
            prep.addGate(new Hadamard(i));
        }
        Step prepAnc = new Step(new X(offset));
        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = length - 1; i > length - 1 - offset; i--) {
            int m = 1;
            for (int j = 0; j < 1 << i; j++) {
                m = m * a % mod;
            }
            MulModulus mul = new MulModulus(length, 2 * length-1, m, mod);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, offset, i);
            p.addStep(new Step(cbg));
        }
        p.addStep(new Step(new InvFourier(offset, 0)));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        int answer = 0;
        for (int i = 0; i < offset; i++) {
            answer = answer + q[i].measure()*(1<< i);
        }
        return answer;
    }
        
}
