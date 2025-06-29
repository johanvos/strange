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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.redfx.strange.Complex;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.MulModulus;
import org.redfx.strange.gate.X;

/**
 *
 * @author johan
 */
public class MulModulusTests extends BaseGateTests {

    static final double D = 0.000000001d;

    @Test
    public void mul2x5mod3() { // 2 x 5 mod 3 = 1
        Program p = new Program(6);
        int mul = 5;
        int N = 3;
        Step prep = new Step();
        prep.addGates(new X(1)); // 2
        Step s = new Step(new MulModulus(0, 1, mul, N));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();

        assertEquals(6, q.length);
        assertEquals(1, q[0].measure()); // 1
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure()); // clean from here on
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());

    }

    @Test
    public void mul3x5mod6() { // 3 x 5 mod 6 = 3
        Program p = new Program(8);
        int mul = 5;
        int N = 6;
        Step prep = new Step();
        prep.addGates(new X(0), new X(1)); // 3 in high register
        Step s = new Step(new MulModulus(0, 2, mul, N));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();

        assertEquals(8, q.length);
        assertEquals(1, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
        assertEquals(0, q[7].measure());
    }

    @Tag("performance")
    @Test
    public void mul3x11mod31() { // 3 x 11 mod 31 = 2
        Program p = new Program(12);
        int mul = 11;
        int N = 31;
        Step prep = new Step();
        prep.addGates(new X(0), new X(1)); // 3 in high register
        Step s = new Step(new MulModulus(0, 4, mul, N));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[2].abssqr(), 0.0001);
        assertEquals(12, q.length);
    }

    @Tag("performance")
    @Test
    public void mul11x13mod97() { // 11 x 13 mod 97 = 46
        Program p = new Program(16);
        int mul = 13;
        int N = 97;
        Step prep = new Step();
        prep.addGates(new X(0), new X(1), new X(3)); // 11 in high register
        Step s = new Step(new MulModulus(0, 6, mul, N));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[46].abssqr(), 0.0001);
        assertEquals(16, q.length);
    }

}
