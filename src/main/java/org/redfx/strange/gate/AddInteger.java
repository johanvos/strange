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
package org.redfx.strange.gate;

import org.redfx.strange.Block;
import org.redfx.strange.BlockGate;
import org.redfx.strange.Complex;
import org.redfx.strange.Step;

import java.util.HashMap;
import org.redfx.strange.Gate;

/**
 * <p>AddInteger class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class AddInteger extends BlockGate<AddInteger> {

    final int x0;
    final int x1;
    final int num;
    static HashMap<Integer, Block> cache = new HashMap<>();

    
    /**
     * Add the qubit in the x register and the y register, result is in x
     *
     * @param x0 start idx x register
     * @param x1 end idx x register
     * @param num the integer to be added, (y_m.. y_0)
     * x_0 ----- y_0 + x_0
     * x_1 ----- y+1 + x_1
     */
    public AddInteger(int x0, int x1, int num) {
        super();
        setIndex(x0);
        this.x0 = x0;
        this.x1 = x1;
        this.num = num;
//        x1 = x1 - x0;
//        x0 = 0;
//        int hash = 1000000 * x0 + 10000*x1+ num;
//        this.block = cache.get(hash);
//        if (this.block == null) {
//            this.block = createBlock(x0, x1, num);
//        }
//        setBlock(block);
       
    }
    
    public Block createBlock(boolean inverse) {
        return createBlock(x0, x1, num, inverse);
    }
    /**
     * <p>createBlock.</p>
     *
     * @param x0 a int
     * @param x1 a int
     * @param num a int
     * @return a {@link org.redfx.strange.Block} object
     */
    public Block createBlock(int x0, int x1, int num, boolean inverse) {
        System.err.println("CREATE addint with num = "+num+" and inv = "+inverse);
        boolean old = true;
        int m = x1-x0+1;
        Block answer = new Block("AddInteger ", m);
        answer.addStep(new Step(new Fourier(m, 0)));
        Step pstep = new Step();
        for (int i = 0; i < m; i++) {
            Complex[][] mat = Complex.identityMatrix(2);
            for (int j = 0; j < m-i ; j++) {
                int cr0 = m-j-i-1;
//                if (inverse) {
//                    System.err.println("ADDINT inv");
//                    cr0 = -1*cr0;
//                }
                if ((num >> cr0 & 1) == 1) {
                    mat = Complex.mmul(mat, new R(2, 1 + j, i).getMatrix());
                    if (old) {
                       int pow = j+1;// (inverse ? (-1 -j) : (1 + j));
                       Gate g = new R(2, pow, i);
                        System.err.println("CREATED addint subgate (pow = "+pow+"): "+g);
                       if (inverse) {
                           g.setInverse(true);
                       }
                        Step s = new Step(g);
                        answer.addStep(s);
                    }
                }
            }
            if (!old) {
                pstep.addGate(new SingleQubitMatrixGate(i, mat));
            }
        }
        if (!old) {
            answer.addStep(pstep);
        }
        answer.addStep(new Step(new InvFourier(m, 0)));
        return answer;
    }

    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return "A\nD\nD\nI";
    }

}
