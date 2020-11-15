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


import org.redfx.strange.Complex;
import org.redfx.strange.Gate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Oracle implements Gate {

    private int mainQubit = 0;
    private List<Integer> affected = new LinkedList<>();
    private Complex[][] matrix;
    private String caption = "Oracle";
    private int span = 1;

    public Oracle (int i) {
        this.mainQubit = i;
    }
    public Oracle(Complex[][] matrix) {
        this.matrix = matrix;
        sanitizeMatrix();
        span = (int)(Math.log(matrix.length)/Math.log(2));
        for (int i = 0; i < span;i++) {
            setAdditionalQubit(i,i);
        }
    }
 
    @Override
    public int getSize() {
        return span;
    }
    
    public void setCaption(String c) {
        this.caption = c;
    }

    @Override
    public void setMainQubitIndex(int idx) {
        this.mainQubit = 0;
    }

    @Override
    public int getMainQubitIndex() {
        return mainQubit;
    }

    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        this.affected.add(idx);
    }

    public int getQubits() {
        return span;
    }

    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return this.affected;
    }

    @Override
    public int getHighestAffectedQubitIndex() {
        return Collections.max(getAffectedQubitIndexes());
    }

    @Override
    public String getCaption() {
        return this.caption;
    }

    @Override
    public String getName() {
        return "Oracle";
    }

    @Override
    public String getGroup() {
        return "Oracle";
    }

    @Override
    public Complex[][] getMatrix() {
        return matrix;
    }

    // replace null with Complex.ZERO
    private void sanitizeMatrix() {
        int rows = matrix.length;
        for (int i = 0;i < rows; i++) {
            Complex[] row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                if (matrix[i][j] == null) {
                    matrix[i][j] = Complex.ZERO;
                }
            }
        }
    }
}
