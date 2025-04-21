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
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.Cz;
import org.redfx.strange.gate.Identity;
import org.redfx.strange.gate.Measurement;
import org.redfx.strange.gate.Swap;
import org.redfx.strange.gate.X;

/**
 *
 * @author johan
 */
public class TwoQubitGateTests extends BaseGateTests {
    
    @Test
    public void empty() {
    }

    @Test
    public void doubleIGate() {
        Program p = new Program(2, new Step(new Identity(0),new Identity(1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }

    @Test
    public void oneXGate1() {
        Program p = new Program(2, new Step(new X(0)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }

    @Test
    public void oneXGate2() {
        Program p = new Program(2, new Step(new X(1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }

    @Test
    public void oneXGate3() {
        Program p = new Program(2, new Step(new X(0), new X(1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }
    @Test
    public void swapGate00() {
        Program p = new Program(2,
            new Step(new Identity(0),new Identity(1)),
            new Step(new Swap(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }

    @Test
    public void swapGate01() {
        Program p = new Program(2,
           new Step(new Identity(0),new X(1)),
           new Step(new Swap(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }

    @Test
    public void swapGate10() {
        Program p = new Program(2,
            new Step(new Identity(1),new X(0)),
            new Step(new Swap(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[1].measure());
        assertEquals(0, qubits[0].measure());
    }

    @Test
    public void swapGate11() {
        Program p = new Program(2,
            new Step(new X(0),new X(1)),
            new Step(new Swap(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }

    @Test
    public void swapGate012() {
        Program p = new Program(3,
            new Step(new X(0)),
            new Step(new Swap(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(0, qubits[2].measure());
    }

    @Test
    public void swapGate122() {
        Program p = new Program(3,
            new Step(new X(1)),
            new Step(new Swap(1,2))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void swapGate022() {
        Program p = new Program(3,
            new Step(new X(0)),
            new Step(new Swap(0,2))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void swapGate202() {
        Program p = new Program(3,
            new Step(new X(0)),
            new Step(new Swap(2,0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void cnot01() {
        Program p = new Program(2, new Step(new Cnot(0,1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }


    @Test
    public void cnotx01() {
        Program p = new Program(2,
            new Step(new X(0)),
            new Step(new Cnot(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }

    @Test
    public void cnotx10() {
        Program p = new Program(2,
            new Step(new X(1)),
            new Step(new Cnot(1,0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }

    @Test
    public void cnotx02() {
        Program p = new Program(3,
            new Step(new X(0)),
            new Step(new Cnot(0,2))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void cnotx20() {
        Program p = new Program(3,
            new Step(new X(2)),
            new Step(new Cnot(2,0))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void cnotx21() {
        Program p = new Program(3,
            new Step(new X(2)),
            new Step(new Cnot(2,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(3, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }

    @Test
    public void cz01() {
        Program p = new Program(2, new Step(new Cz(0,1)));
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }

    @Test
    public void czx01() {
        Program p = new Program(2,
            new Step(new X(0)),
            new Step(new Cz(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
    }

    @Test
    public void czxx01() {
        Program p = new Program(2,
            new Step(new X(0), new X(1)),
            new Step(new Cz(0,1))
        );
        Result res = runProgram(p);
        Qubit[] qubits = res.getQubits();
        assertEquals(2, qubits.length);
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }

    @Test
    public void IMcnot() {
        Program p = new Program(2,
            new Step(new Identity(0)),
            new Step(new Measurement(0)),
            new Step(new Cnot(0,1))
        );
    }

    @Test
    public void IMcnot10() {
        Program p = new Program(2,
            new Step(new Identity(0)),
            new Step(new Measurement(0))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> p.addStep(new Step(new Cnot(1,0))));
    }
}
