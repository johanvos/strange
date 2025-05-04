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
package org.redfx.strange.gate;

import org.redfx.strange.Block;
import org.redfx.strange.BlockGate;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Step;

/**
 * <p>AddIntegerModulus class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class AddIntegerModulus extends BlockGate<AddIntegerModulus> {

    Block block;

    
    /**
     * Add integer a to the qubit in the x register mod N, result is in x
     *
     * @param x0 start idx x register
     * @param a a int
     * @param N a int
     */
    public AddIntegerModulus(int x0, int x1, int a, int N) {
        super();
        int n = x1 - x0 + 1;
        if (N >= (1 << n)) {
            throw new IllegalArgumentException ("AddIntegerModules with n = "+n+" but modulus is bigger than max: "+N);
        }
        setIndex(x0);
        x1 = x1 - x0;
        x0 = 0;
        this.block = createBlock(x0, x1, a, N);
        setBlock(block);
    }

    /**
     * <p>createBlock.</p>
     *
     * @param x0 a int
     * @param x1 a int
     * @param a a int
     * @param N a int
     * @return a {@link org.redfx.strange.Block} object
     */
    public Block createBlock(int x0, int x1, int a, int N) {
        Block answer = new Block("AddIntegerModulus", x1-x0+2);
        int nn = x1-x0;
        int dim = nn+1+x0;
        AddInteger add = new AddInteger(x0, x1, a);
        answer.addStep(new Step(add));
        AddInteger min = new AddInteger(x0,x1,N).inverse();
        answer.addStep(new Step(min));
        answer.addStep(new Step(new Cnot(x1,dim)));
        AddInteger addN = new AddInteger(x0,x1,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, x0,dim);
        answer.addStep(new Step(cbg));

        AddInteger add2 = new AddInteger(x0,x1,a).inverse();
        answer.addStep(new Step(add2));
        
        answer.addStep(new Step(new X(dim -1)));
        answer.addStep(new Step(new Cnot(x1,dim)));
        answer.addStep(new Step(new X(dim -1)));

        AddInteger add3 = new AddInteger(x0,x1,a);
        answer.addStep (new Step(add3));
        return answer;
    }

}
