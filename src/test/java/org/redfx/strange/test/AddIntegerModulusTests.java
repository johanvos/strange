/*-
 * #%L
 * Strange
 * %%
 * Copyright (C) 2021 Johan Vos
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
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.AddIntegerModulus;
import org.redfx.strange.gate.X;
import static org.redfx.strange.test.BaseGateTests.DELTA;


/**
 *
 * @author johan
 */
public class AddIntegerModulusTests extends BaseGateTests {

    static final double D = 0.000000001d;

    @Test
    public void testModLimit() {
        // modulus should be smaller than 2^n
        AddIntegerModulus a0 = new AddIntegerModulus(0,0,1,1);
        AddIntegerModulus a1 = new AddIntegerModulus(0,1,1,1);
        AddIntegerModulus a2 = new AddIntegerModulus(0,1,1,2);
        AddIntegerModulus a3 = new AddIntegerModulus(0,1,1,3);
        boolean err = false;
        try {
            AddIntegerModulus a4 = new AddIntegerModulus(0,0,1,2);
        } catch (IllegalArgumentException e) {
            err = true;
        }
        assertTrue(err);
        err = false;
        try {
            AddIntegerModulus a5 = new AddIntegerModulus(0,1,1,4);
        } catch (IllegalArgumentException e) {
            err = true;
        }
        assertTrue(err);
    }

    @Test
    public void add1p1m3() {
        // 1 + 1 mod 3 = 2
        int add = 1;
        int mod = 3;
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(0)); //1
        p.addStep(prep);
        AddIntegerModulus aim = new AddIntegerModulus(0,1,add,mod);
        p.addStep(new Step(aim));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
    }
      
    @Test
    public void add2p1m3() {
        // 2 + 1 mod 3 = 0
        int add = 1;
        int mod = 3;
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(1)); // 2
        p.addStep(prep);
        AddIntegerModulus aim = new AddIntegerModulus(0,1,add,mod);
        p.addStep(new Step(aim));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[0].abssqr(), DELTA);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
    }
           
    @Test
    public void min2m1m3() {
        // 2 - 1 mod 3 = 1
        int add = 1;
        int mod = 3;
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(1)); // 2
        p.addStep(prep);
        AddIntegerModulus aim = new AddIntegerModulus(0,1,add,mod).inverse();
        p.addStep(new Step(aim));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
    }
               
    @Test
    public void min1m1m3() {
        // 1 - 1 mod 3 = 0
        int add = 1;
        int mod = 3;
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(0)); // 1
        p.addStep(prep);
        AddIntegerModulus aim = new AddIntegerModulus(0,1,add,mod).inverse();
        p.addStep(new Step(aim));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
    }
        
}
