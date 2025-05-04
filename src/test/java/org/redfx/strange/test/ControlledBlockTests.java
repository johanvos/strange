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
import org.redfx.strange.Block;
import org.redfx.strange.BlockGate;
import org.redfx.strange.Complex;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.AddInteger;
import org.redfx.strange.gate.Cr;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.gate.X;


/**
 *
 * @author johan
 */
public class ControlledBlockTests extends BaseGateTests {
    
    @Test
    public void cnotblock00() {
        Program p = new Program(2);
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 0,1 );
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
    }
             
    @Test
    public void cnotblock10() { //10 -> 11
        // q0 -----X-- 1
        //         |
        // q1 --X--o-- 1
        Program p = new Program(2);
        Step prep = new Step(new X(1));
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 0, 1);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[3].abssqr(), DELTA);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
    }
    
             
    @Test
    public void cnotblock01() { //01 -> 11
        // q0 --X--o-- 1
        //         |
        // q1 -----X-- 1
        Program p = new Program(2);
        Step prep = new Step(new X(0));
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 1, 0);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
    }
                     
    @Test
    public void cnotblock100a() { //100 -> 110
        Program p = new Program(3);
        Step prep = new Step(new X(2));
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 1, 2);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
          
    @Test
    public void cnotblock100b2() { //100 -> 111
        Program p = new Program(3);
        Step prep = new Step(new X(2));
        Block block = new Block(2);
        block.addStep(new Step(new X(0), new X(1)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 0, 2);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
    
    @Test
    public void cnotblock100b2inv() { //100 -> 111
        Program p = new Program(3);
        Step prep = new Step(new X(0));
        Block block = new Block(2);
        block.addStep(new Step(new X(0), new X(1)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 1, 0);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
    
    
    @Test
    public void cnotblock101() { //101 -> 001
        Program p = new Program(3);
        Step prep = new Step(new X(0), new X(2));
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 2, 0);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Complex[] probability = result.getProbability();
        Complex.printArray(probability);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
    }
    
    @Test
    public void cnotblock1010() { //1010 -> 0010
        Program p = new Program(4);
        Step prep = new Step(new X(1), new X(3));
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 3, 1);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Complex[] probability = result.getProbability();
        Complex.printArray(probability);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
    }
    
    @Test
    public void cnotblock1010inv() { //1010 -> 0010
        Program p = new Program(4);
        Step prep = new Step(new X(1), new X(3));
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 1, 3);
        Complex[][] m = cbg.getMatrix();

        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Complex[] probability = result.getProbability();
        Complex.printArray(probability);
        Qubit[] q = result.getQubits();

        assertEquals(4, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(1, q[3].measure());
    }
     
    @Test
    public void CCR2() { //101 -> 001
        Program p = new Program(3);
        Step prep = new Step(new Hadamard(0), new Hadamard(1), new Hadamard(2));
        Block block = new Block(2);
        block.addStep(new Step(new Cr(0,1,2,2)));
        BlockGate gate = new BlockGate(block, 0);
        ControlledBlockGate cbg = new ControlledBlockGate(gate, 0, 2);
        p.addStep(prep);
        p.addStep(new Step(cbg));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(0.3535, probs[7].i, 0.01);
    }
      
    @Test
    public void ccx() {
        Program p = new Program(3);
        Step prep = new Step(new Hadamard(0), new X(1),new Hadamard(2));
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        
        ControlledBlockGate cbg = new ControlledBlockGate(block, 0, 1);
        ControlledBlockGate cbg2 = new ControlledBlockGate(cbg,1,0);
        p.addStep(prep);
        p.addStep(new Step(cbg2));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(.5, probs[2].r, 0.01);
        assertEquals(.5, probs[3].r, 0.01);
        assertEquals(.5, probs[5].r, 0.01);
        assertEquals(.5, probs[6].r, 0.01);
    }    
    @Test
    public void ai2_11() {
        Program p = new Program(5);
        Step prep = new Step(new X(0), new X(1), new X(4));
        AddInteger ai = new AddInteger(0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(ai, 0, 3);
        ControlledBlockGate cbg2 = new ControlledBlockGate(cbg,1,0);
        p.addStep(prep);
        p.addStep(new Step(cbg2));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[21].r, 0.01);
    }   
        
    @Test
    public void ai2_01() {
        Program p = new Program(5);
        Step prep = new Step(new X(1), new X(4));
        AddInteger ai = new AddInteger(0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(ai, 0, 3);
        ControlledBlockGate cbg2 = new ControlledBlockGate(cbg,1,0);
        p.addStep(prep);
        p.addStep(new Step(cbg2));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[18].r, 0.01);
    }

    @Test
    public void ai2_10() {
        Program p = new Program(5);
        Step prep = new Step(new X(0), new X(1));
        AddInteger ai = new AddInteger(0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(ai, 0, 3);
        ControlledBlockGate cbg2 = new ControlledBlockGate(cbg,1,0);
        p.addStep(prep);
        p.addStep(new Step(cbg2));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[3].r, 0.01);
    }
    @Test
    public void ai2() {
        Program p = new Program(5);
        Step prep = new Step(new Hadamard(0), new X(1), new Hadamard(4));
        AddInteger ai = new AddInteger(0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(ai, 0, 3);
        ControlledBlockGate cbg2 = new ControlledBlockGate(cbg,1,0);
        p.addStep(prep);
        p.addStep(new Step(cbg2));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(0.5, probs[2].r, 0.01);
        assertEquals(0.5, probs[3].r, 0.01);
        assertEquals(0.5, probs[18].r, 0.01);
        assertEquals(0.5, probs[21].r, 0.01);
    }
}
