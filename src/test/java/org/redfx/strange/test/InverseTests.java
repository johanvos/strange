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


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.redfx.strange.Block;
import org.redfx.strange.BlockGate;
import org.redfx.strange.Complex;
import org.redfx.strange.Program;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Fourier;
import org.redfx.strange.gate.X;


/**
 *
 * @author johan
 */
public class InverseTests extends BaseGateTests {

    static final double D = 0.000000001d;
    
   /*
    *          |  1   1   1   1  | 
    * F = 1/2  |  1   i  -1  -i  |
    *          |  1  -1   1  -1  |
    *          |  1  -i  -1   i  |
    */
    
    @Test
    public void fourier1000() { // 00
        Program p = new Program(2);
        Fourier f = new Fourier(2,0);
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, .5f,D);
        assertEquals(probs[2].r, .5f,D);
        assertEquals(probs[3].r, .5f,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, 0, D);
    }
        
    @Test
    public void fourier0100() { // 01
        Program p = new Program(2);
        Fourier f = new Fourier(2,0);
        p.addStep(new Step(new X(0)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, -.5f,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, .5, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, -.5, D);
    }
      
    @Test
    public void fourier0010() { // 10
        Program p = new Program(2);
        Fourier f = new Fourier(2,0);
        p.addStep(new Step(new X(1)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, -.5f,D);
        assertEquals(probs[2].r, .5f,D);
        assertEquals(probs[3].r, -.5f,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, 0, D);
    }
    
    @Test
    public void fourier0001() { // 11
        Program p = new Program(2);
        Fourier f = new Fourier(2,0);
        p.addStep(new Step(new X(0), new X(1)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, -.5f,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, -.5, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, .5, D);
    }
      
    
    @Test
    public void invfourier1000() { // 00
        Program p = new Program(2);
        Fourier f = new Fourier(2,0).inverse();
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, .5f,D);
        assertEquals(probs[2].r, .5f,D);
        assertEquals(probs[3].r, .5f,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, 0, D);
    }

    @Test
    public void invfourier0100() { // 01
        Program p = new Program(2);
        Fourier f = new Fourier(2,0).inverse();
        p.addStep(new Step(new X(0)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, -.5f,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, -.5, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, .5, D);
    }
      
    
    @Test
    public void invfourier0010() { // 10
        Program p = new Program(2);
        Fourier f = new Fourier(2,0).inverse();
        p.addStep(new Step(new X(1)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, -.5f,D);
        assertEquals(probs[2].r, .5f,D);
        assertEquals(probs[3].r, -.5f,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, 0, D);
    }
    
    @Test
    public void invfourier0001() { // 11
        Program p = new Program(2);
        Fourier f = new Fourier(2,0).inverse();
        p.addStep(new Step(new X(0), new X(1)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, -.5f,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, .5, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, -.5, D);
    }
    
    @Test
    public void invinvfourier0100() { // 01
        Program p = new Program(2);
        Fourier f = new Fourier(2,0).inverse().inverse();
        p.addStep(new Step(new X(0)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, -.5f,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, .5, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, -.5, D);
    }
        
    @Test
    public void fourier3qX1() { // 01
        Program p = new Program(3);
        Fourier f = new Fourier(2,1);
        p.addStep(new Step(new X(1)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, 0,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, -.5f,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, 0,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, .5, D);
        assertEquals(probs[3].i, 0, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, -.5, D);
        assertEquals(probs[7].i, 0, D);
    }  
            
    @Test
    public void fourierinv3qX1() { // 01
        Program p = new Program(3);
        Fourier f = new Fourier(2,1).inverse();
        p.addStep(new Step(new X(1)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, 0,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, -.5f,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, 0,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, -.5, D);
        assertEquals(probs[3].i, 0, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, .5, D);
        assertEquals(probs[7].i, 0, D);
    }  
    
    @Test
    public void block3Qx1() {
        Program p = new Program(3);
        Block block = new Block("myfourier", 2);
        block.addStep(new Step(new Fourier(2,0)));
        BlockGate bg = new BlockGate(block, 1);
        p.addStep(new Step(new X(1)));
        p.addStep(new Step(bg));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, 0,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, -.5f,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, 0,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, .5, D);
        assertEquals(probs[3].i, 0, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, -.5, D);
        assertEquals(probs[7].i, 0, D);
    }
    
    @Test
    public void blockinv3qX1() {
        Program p = new Program(3);
        Block block = new Block("myfourier", 2);
        block.addStep(new Step(new Fourier(2,0).inverse()));
        BlockGate bg = new BlockGate(block, 1);
        p.addStep(new Step(new X(1)));
        p.addStep(new Step(bg));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();   
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, 0,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, -.5f,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, 0,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, -.5, D);
        assertEquals(probs[3].i, 0, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, .5, D);
        assertEquals(probs[7].i, 0, D);
    }
    
    @Test
    public void blockinvgate3qX1() {
        Program p = new Program(3);
        Block block = new Block("myfourier", 2);
        block.addStep(new Step(new Fourier(2,0)));
        BlockGate bg = (BlockGate) new BlockGate(block, 1).inverse();
        p.addStep(new Step(new X(1)));
        p.addStep(new Step(bg));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();  
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, 0,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, -.5f,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, 0,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, -.5, D);
        assertEquals(probs[3].i, 0, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, .5, D);
        assertEquals(probs[7].i, 0, D);
    }
    
    
    @Test
    public void fourier3qX2() { // 01
        Program p = new Program(3);
        Fourier f = new Fourier(2,1);
        p.addStep(new Step(new X(2)));
        p.addStep(new Step(f));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, -.5f,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, .5f,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, -.5f,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, 0, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, 0, D);
        assertEquals(probs[7].i, 0, D);
    }  
    
    @Test
    public void fourierinv3qX2() { // 01
        Program p = new Program(3);
        Fourier f = new Fourier(2,0);
        p.addStep(new Step(new X(0), new X(2)));
        p.addStep(new Step(new X(2), f.inverse()));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, -.5f,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, 0,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, 0,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, -.5f, D);
        assertEquals(probs[2].i, 0, D);
        assertEquals(probs[3].i, .5f, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, 0, D);
        assertEquals(probs[7].i, 0, D);
    }  
    
    @Test
    public void blockinvgate3qX2() {
        Program p = new Program(3);
        Block block = new Block("myfourier", 2);
        block.addStep(new Step(new Fourier(2,0)));
        BlockGate bg = (BlockGate) new BlockGate(block, 1).inverse();
        p.addStep(new Step(new X(0), new X(1)));
        p.addStep(new Step(bg));
        p.addStep(new Step(new X(0)));
        Result result = runProgram(p);
        Complex[] probs = result.getProbability();
        assertEquals(probs[0].r, .5f,D);
        assertEquals(probs[1].r, 0,D);
        assertEquals(probs[2].r, 0,D);
        assertEquals(probs[3].r, 0,D);
        assertEquals(probs[4].r, -.5f,D);
        assertEquals(probs[5].r, 0,D);
        assertEquals(probs[6].r, 0,D);
        assertEquals(probs[7].r, 0,D);
        assertEquals(probs[0].i, 0, D);
        assertEquals(probs[1].i, 0, D);
        assertEquals(probs[2].i, -.5, D);
        assertEquals(probs[3].i, 0, D);
        assertEquals(probs[4].i, 0, D);
        assertEquals(probs[5].i, 0, D);
        assertEquals(probs[6].i, .5, D);
        assertEquals(probs[7].i, 0, D);
     }

}
