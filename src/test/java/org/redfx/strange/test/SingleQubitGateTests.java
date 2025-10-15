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

import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.redfx.strange.Complex;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.gate.Identity;
import org.redfx.strange.gate.Measurement;
import org.redfx.strange.gate.R;
import org.redfx.strange.gate.X;
import org.redfx.strange.gate.Y;
import org.redfx.strange.gate.Z;

public class SingleQubitGateTests extends BaseGateTests {

    @Test
    public void empty() {
    }

    @Test
    public void namedQubit() {
        Qubit q0 = new Qubit();
        Program p = new Program(q0);
        X x = new X(q0);
        p.addStep(new Step(x));

        Result res = runProgram(p);
        Complex[] probs = res.getProbability();
        System.err.println("probs = "+Arrays.toString(probs));
    }

    @Test
    public void namedQubit2() {
        Qubit q0 = new Qubit();
        Qubit q1 = new Qubit();
        Program p = new Program(q0, q1);
        X x = new X(q0);
        p.addSteps(new Step(x), new Step(new X(q1)));

        Result res = runProgram(p);
        Complex[] probs = res.getProbability();
        System.err.println("probs = "+Arrays.toString(probs));
    }
    @Test
    public void simpleIGate() {
        Program p = new Program(1, new Step(new Identity(0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(0, qubits[0].measure());
    }

    @Test
    public void simpleXGate() {
        Program p = new Program(1, new Step(new X(0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(1, qubits[0].measure());
    }    
        
    @Test
    public void simpleIXGate() {
        Program p = new Program(2, new Step(new X(0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }    
            
    @Test
    public void simpleXIGate() {
        Program p = new Program(2, new Step(new X(1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[1].measure());
    }    

    @Test
    public void simpleIXIGate() {
        Program p = new Program(3, new Step(new X(1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(0, qubits[2].measure());
    }

    @Test
    public void simpleXIIGate() {
        Program p = new Program(3, new Step(new X(2)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }    
    @Test
    public void simpleYGate() {
        Program p = new Program(1, new Step(new Y(0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(1, qubits[0].measure());
    }
    
    @Test
    public void simpleZGate() {
        Program p = new Program(1, new Step(new Z(0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(0, qubits[0].measure());
    }   
        
    @Test
    public void simpleHGate() {
        int[] results = new int[2];
        for (int i = 0; i < 100; i++) {
            Program p = new Program(1, new Step(new Hadamard(0)));
            Result res = runProgram(p);
            Qubit[] qubits = res.getQubits();
            results[qubits[0].measure()]++;
        }
        assertTrue(results[0] > 10);
        assertTrue(results[1] > 10);
    }   
    
    @Test
    public void simpleTogetherGate() {
        Program p = new Program(4,
            new Step(new X(0),
            new Y(1),
            new Z(2),
            new Identity(3))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(0, qubits[2].measure());
        assertEquals(0, qubits[3].measure());
    }

    @Test
    public void simpleIM() {
        Program p = new Program(1,
            new Step(new Identity(0)),
            new Step(new Measurement(0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(0, qubits[0].measure());
    }

    @Test
    public void simpleXM() {
        Program p = new Program(1,
            new Step(new X(0)),
            new Step(new Measurement(0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(1, qubits[0].measure());
    }

    @Test
    public void simpleXMH() {
        Program p = new Program(1,
            new Step(new X(0)),
            new Step(new Measurement(0))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> p.addStep(new Step(new Hadamard(0))));
    }

    @Test
    public void RMatrix() {
        R r = new R(2,1,0);
        Complex[][] matrix = r.getMatrix();
         assertEquals(-1,matrix[1][1].r, DELTA);
    }

    @Test
    public void simpleRGate() {
        Program p = new Program(1, new Step(new R(2, 1, 0)));
        Result res = runProgram(p);
        Complex[] probs = res.getProbability();
        assertEquals(1, probs[0].r);
        assertEquals(0, probs[0].i);
        assertEquals(0, probs[1].r);
        assertEquals(0, probs[1].i);
    }

    @Test
    public void XR1Gate() {
        Program p = new Program(1, new Step(new X(0)), new Step(new R(2, 1, 0)));
        Result res = runProgram(p);
        Complex[] probs = res.getProbability();
        assertEquals(0, probs[0].r, DELTA);
        assertEquals(0, probs[0].i, DELTA);
        assertEquals(-1, probs[1].r, DELTA);
        assertEquals(0, probs[1].i, DELTA);
    }

    @Test
    public void XR2Gate() {
        Program p = new Program(1, new Step(new X(0)), new Step(new R(2, 2, 0)));
        Result res = runProgram(p);
        Complex[] probs = res.getProbability();
        assertEquals(0, probs[0].r, DELTA);
        assertEquals(0, probs[0].i, DELTA);
        assertEquals(0, probs[1].r, DELTA);
        assertEquals(1, probs[1].i, DELTA);
    }

    @Test
    public void singleR() {
        double exp = Math.PI*-1;
        Complex c = new Complex(Math.cos(exp), Math.sin(exp));
        assertEquals(-1, c.r, DELTA);
    }

}
