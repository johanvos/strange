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

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.redfx.strange.Block;
import org.redfx.strange.BlockGate;
import org.redfx.strange.Complex;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Gate;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.AddInteger;
import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.Fourier;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.gate.R;
import org.redfx.strange.gate.Toffoli;
import org.redfx.strange.gate.X;

/**
 *
 * @author johan
 */
public class BlockTests extends BaseGateTests {

    @Test
    public void empty() {
    }

    @Test
    public void createBlock() {
        Block block = new Block(1);
        block.addStep(new Step(Gate.identity(0)));
        BlockGate gate = new BlockGate(block, 0);
    }

//    @Test
//    public void addWrongIndex() {
//        Block block = new Block(2);
//        block.addStep(new Step(Gate.x(0)));
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> block.addGate(Gate.x(2)));
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> block.addGate(Gate.cnot(2, 1)));
//        block.addGate(Gate.x(1));
//    }
    @Test
    public void createBlockInProgram() {
        Block block = new Block(1);
        block.addStep (new Step(Gate.identity(0)));
        BlockGate gate = new BlockGate(block, 0);
        Program p = new Program(1);
        p.addStep(new Step(gate));
        Result result = runProgram(p);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
    }

    @Test
    public void createXBlockInProgram() {
        Block block = new Block(1);
        Step bs = new Step(Gate.x(0));
        block.addStep(bs);
        BlockGate gate = new BlockGate(block, 0);
        Program p = new Program(1);
        p.addStep(new Step(gate));
        Result result = runProgram(p);
        Qubit[] qubits = result.getQubits();
        assertEquals(1, qubits[0].measure());
    }

    @Test
    public void createXBlockInProgramAddPos2() {
        Block block = new Block(1);
        block.addStep( new Step(Gate.x(0)));
        BlockGate gate = new BlockGate(block, 1);
        Program p = new Program(2);
        p.addStep(new Step(gate));
        Result result = runProgram(p);
        Qubit[] qubits = result.getQubits();
        assertEquals(1, qubits[1].measure());
        assertEquals(0, qubits[0].measure());
    }

    @Test
    public void createManyXBlockInProgramAddPos2() {
        Block block1 = new Block(1);
        block1.addStep(new Step(Gate.x(0)));
        Block block2 = new Block(1);
        block2.addStep(new Step(Gate.x(0)));
        Block block3 = new Block(1);
        block3.addStep(new Step(Gate.x(0)));
        BlockGate gate = new BlockGate(block1, 1);
        BlockGate gate2 = new BlockGate(block2, 0);
        BlockGate gate3 = new BlockGate(block3, 1);
        Program p = new Program(2);
        p.addSteps(new Step(gate), new Step(gate2), new Step(gate3));
        Result result = runProgram(p);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[0].measure());
    }

    @Test
    public void createXXBlockInProgram() {
        Block block = new Block(1);
        block.addStep(new Step(Gate.x(0)));
        block.addStep(new Step(Gate.x(0)));
        BlockGate gate = new BlockGate(block, 0);
        Program p = new Program(1);
        p.addSteps(new Step(gate));
        Result result = runProgram(p);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
    }

    @Test
    public void createXXBlockInProgramAddPos2() {
        Block block = new Block(1);
        block.addStep(new Step(Gate.x(0)));
        block.addStep(new Step(Gate.x(0)));
        BlockGate gate = new BlockGate(block, 1);
        Program p = new Program(2);
        p.addSteps(new Step(gate));
        Result result = runProgram(p);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[1].measure());
        assertEquals(0, qubits[0].measure());
    }

    @Test
    public void compareBlock() {
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                Step prep = new Step();
                if (a % 2 == 1) {
                    prep.addGate(new X(1));
                }
                if (b % 2 == 1) {
                    prep.addGate(new X(2));
                }
                if (a / 2 == 1) {
                    prep.addGate(new X(4));
                }
                if (b / 2 == 1) {
                    prep.addGate(new X(5));
                }
                Program p = new Program(7);
                Step s0 = new Step(new Toffoli(1, 2, 3));
                Step s1 = new Step(new Cnot(1, 2));
                Step s2 = new Step(new Toffoli(0, 2, 3));
                p.addSteps(prep, s0, s1, s2);
                Result normal = runProgram(p);
                Qubit[] normalQ = normal.getQubits();
                Block carry = new Block("carry", 4);
                carry.addStep(new Step(new Toffoli(1, 2, 3)));
                carry.addStep(new Step(new Cnot(1, 2)));
                carry.addStep(new Step(new Toffoli(0, 2, 3)));

                Program bp = new Program(7);
                Step bs0 = new Step(new BlockGate(carry, 0));
                bp.addSteps(prep, bs0);
                Result blockResult = runProgram(bp);
                Qubit[] blockQ = blockResult.getQubits();

                assertEquals(normalQ.length, blockQ.length);
                for (int i = 0; i < normalQ.length; i++) {
                    assertEquals(normalQ[i].measure(), blockQ[i].measure());
                }
            }
        }
    }

    @Test
    public void compareBlock2() {
        for (int a = 1; a < 2; a++) {
            for (int b = 0; b < 1; b++) {
                Step prep = new Step();
                if (a % 2 == 1) {
                    prep.addGate(new X(1));
                }
                if (b % 2 == 1) {
                    prep.addGate(new X(2));
                }

                Program p = new Program(4);

                Step s0 = new Step(new Toffoli(1,2,3));
                Step s1 = new Step(new Cnot(1, 2));
                p.addSteps(prep, s0, s1);
                Result normal = runProgram(p);
                Qubit[] normalQ = normal.getQubits();

                Block carry = new Block("carry", 4);
                carry.addStep(new Step(new Toffoli(1,2,3)));
                carry.addStep(new Step(new Cnot(1, 2)));

                Program bp = new Program(4);
                Step bs0 = new Step(new BlockGate(carry, 0));
                bp.addSteps(prep, bs0);
                Result blockResult = runProgram(bp);
                Qubit[] blockQ = blockResult.getQubits();

                assertEquals(normalQ.length, blockQ.length);
                for (int i = 0; i < normalQ.length; i++) {
                    assertEquals(normalQ[i].measure(), blockQ[i].measure());
                }
            }
        }
    }

    @Test
    public void testDummyBlockGate() {
        DummyBlockGate dbg = new DummyBlockGate(0);
        Program bp = new Program(2);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }

    @Test
    public void testDummyBlockGate2() {
        DummyBlockGate dbg = new DummyBlockGate(1);
        Program bp = new Program(3);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }
    
    @Test
    public void testDummyBlockGateInv() {
        DummyBlockGate dbg = new DummyBlockGate(0).inverse();
        Program bp = new Program(2);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }
    
    @Test
    public void testDummyBlockGateInv2() {
        DummyBlockGate dbg = new DummyBlockGate(1).inverse();
        Program bp = new Program(3);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }
        
    @Test
    public void testDummyBlockGateR() {
        DummyBlockGate dbg = new DummyBlockGate(0, new Step(new R(2,0)));
        Program bp = new Program(2);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }
    
    @Test
    public void testDummyBlockGateR2() {
        DummyBlockGate dbg = new DummyBlockGate(1, new Step(new R(2,0)));
        Program bp = new Program(3);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }
                       
    @Test
    public void testDummyBlockGateRinv() {
        DummyBlockGate dbg = new DummyBlockGate(0, new Step(new R(2,0))).inverse();
        Program bp = new Program(2);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
    }
    
    @Test
    public void testDummyBlockGateRinv2() {
        DummyBlockGate dbg = new DummyBlockGate(1, new Step(new R(2,0))).inverse();
        Program bp = new Program(3);
        Step bs0 = new Step(dbg);
        bp.addSteps(bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        assertEquals(0, qubits[0].measure());
        assertEquals(0, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }
    
    @Test
    public void testGenericBlockGateHHF() {
        Step prep = new Step(new X(0));
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(new Hadamard(0), new Hadamard(1)));
        steps.add(new Step(new Fourier(2,0)));
        GenericBlockGate dbg = new GenericBlockGate(0, 2, steps).inverse();
        Program bp = new Program(2);
        Step bs0 = new Step(dbg);
        bp.addSteps(prep, bs0);
        Result result = runProgram(bp);
        Complex[] probability = result.getProbability();
        float EPS = (float)1e-4;
        assertEquals(0, probability[0].r, EPS);
        assertEquals(0, probability[0].i, EPS);
        assertEquals(0, probability[1].r, EPS);
        assertEquals(0, probability[1].i, EPS);
        assertEquals(0.5, probability[2].r, EPS);
        assertEquals(-0.5, probability[2].i, EPS);
        assertEquals(0.5, probability[3].r, EPS);
        assertEquals(0.5, probability[3].i, EPS);
    
    }
    
    @Test
    public void testGenericBlockGateAMF() {
        List<Step> steps = new ArrayList<>();
        Step prep = new Step(new X(2));
        int x0 = 0; int x1 = 1; int N = 1; int dim = 3; int y0 = 2; int y1 = 3;
        
        AddInteger addN = new AddInteger(x0,x1,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, x0,2);
        steps.add(new Step(cbg));

        GenericBlockGate dbg = new GenericBlockGate(0, dim, steps).inverse();
        Program bp = new Program(3);
        Step bs0 = new Step(dbg);
        bp.addSteps(prep, bs0);
        Result result = runProgram(bp);
        Qubit[] qubits = result.getQubits();
        for (int i = 0; i < dim; i++) {
         //   System.err.println("m["+i+"]: "+qubits[i].measure());
        }
        assertEquals(1, qubits[0].measure());
        assertEquals(1, qubits[1].measure());
        assertEquals(1, qubits[2].measure());
    }
            
    class DummyBlockGate extends BlockGate<DummyBlockGate> {

        Block block;
        Step addedStep = null;

        public DummyBlockGate(int idx) {
            this (idx, null);
        }
        
        public DummyBlockGate(int idx, Step s) {
            super();
            setIndex(idx);
            this.block = createBlock();
            this.addedStep = s;
            setBlock(block);
        }

        public Block createBlock() {
            Block answer = new Block("Dummy", 2);
            answer.addStep(new Step(new X(1)));
            if (this.addedStep != null) {
                answer.addStep(addedStep);
            }
            answer.addStep(new Step(new Cnot(1, 0)));
            return answer;
        }

        @Override
        public boolean hasOptimization() {
            return true;
            // return !inverse;
        }
    }
    
    
    class GenericBlockGate extends BlockGate<GenericBlockGate> {

        Block block;
        List<Step> steps = null;
        private int dim;

        public GenericBlockGate(int idx, int dim) {
            this (idx, dim, null);
        }
        
        public GenericBlockGate(int idx, int dim, List<Step> s) {
            super();
            setIndex(idx);
            this.steps = s;
            this.dim = dim;
            this.block = createBlock();
            setBlock(block);
        }

        public Block createBlock() {
            Block answer = new Block("Generic", dim);
            for (Step step : steps) {
                answer.addStep(step);
            }
            return answer;
        }

        @Override
        public boolean hasOptimization() {
            return true;
        }
    }
}
