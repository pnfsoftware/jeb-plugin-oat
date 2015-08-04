/*
 * JEB Copyright PNF Software, Inc.
 * 
 *     https://www.pnfsoftware.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pnf.OAT;

public class OAT {

    // Oat magic numbers
    public static final byte[] magic = new byte[] { 'o', 'a', 't', '\n' };

    // ISA constants - incomplete list but still useful
    public static final int kNone = 0;
    public static final int kArm = 1;
    public static final int kArm64 = 2;
    public static final int kThumb2 = 3;
    public static final int kX86 = 4;
    public static final int X86_64 = 5;
    public static final int kMips = 6;
    public static final int kMips64 = 7;

    // For the class headers - not used
    public static final int kOatClassAllCompiled = 0;
    public static final int kOatClassSomeCompiled = 1;
    public static final int kOatClassNoneCompiled = 2;

}
