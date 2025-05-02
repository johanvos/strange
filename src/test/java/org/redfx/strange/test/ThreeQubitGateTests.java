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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.redfx.strange.Complex;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Toffoli;
import org.redfx.strange.gate.X;

/**
 *
 * @author johan
 */
public class ThreeQubitGateTests extends BaseGateTests {
    
    @Test
    public void empty() {
    }
      
    @Test
    public void ToffoliGate0() {
        // |000> -> |000>
        Program p = new Program(3, new Step(new Toffoli(2,1,0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(0, qubits[2].measure());
    }
                   
    @Test
    public void ToffoliGate1() {
        // |100> -> |100>
        Program p = new Program(3,
            new Step(new X(2)),
            new Step(new Toffoli(2,1,0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        Complex[] probs = res.getProbability();
        Complex.printArray(probs);
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }
    
    @Test
    public void ToffoliGate2() {
        // |110> -> |111>
        Program p = new Program(3,
           new Step(new X(2),new X(1)),
           new Step(new Toffoli(2,1,0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }
    
    @Test
    public void ToffoliGate3() {
        // |111> -> |110>
        Program p = new Program(3,
            new Step(new X(2),new X(1),new X(0)),
            new Step(new Toffoli(2,1,0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void ToffoliGate4() {
        // |001> -> |001>
        Program p = new Program(3,
            new Step(new X(0)),
            new Step(new Toffoli(2,1,0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(0, qubits[2].measure());
    }

    @Test
    public void ToffoliGate5() {
        // |010> -> |010>
        Program p = new Program(3,
            new Step(new X(1)),
            new Step(new Toffoli(2,1,0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(0, qubits[2].measure());
    }

    @Test
    public void ToffoliGateR0() {
        // |000> -> |000>
        Program p = new Program(3, new Step(new Toffoli(0,1,2)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(0, qubits[2].measure());
    }

    @Test
    public void ToffoliGateR1() {
        // |100> -> |100>
        Program p = new Program(3,
            new Step(new X(2)),
            new Step(new Toffoli(0,1,2))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void ToffoliGateR2() {
        // |011> -> |111>
        Program p = new Program(3,
           new Step(new X(0),new X(1)),
           new Step(new Toffoli(0, 1, 2)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void ToffoliGateR3() {
        // |110> -> |111>
        Program p = new Program(4,
           new Step(new X(2),new X(3)),
           new Step(new Toffoli(3, 2, 1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(4, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
        assertEquals(1, qubits[3].measure());
    }

    @Test
    public void ToffoliGateR4() {
        // |1100> -> |1101>
        Program p = new Program(4,
           new Step(new X(2),new X(3)),
           new Step(new Toffoli(3, 2, 0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(4, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
        assertEquals(1, qubits[3].measure());
    }

    @Test
    public void ToffoliGateS2() {
        // |0101> -> |1101>
        Program p = new Program(4,
           new Step(new X(0),new X(2)),
           new Step(new Toffoli(0, 2, 3)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(4, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
        assertEquals(1, qubits[3].measure());
    }
}
