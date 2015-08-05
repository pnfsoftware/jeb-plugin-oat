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

package com.pnf.plugin.oat.internal;

public class DexFile extends StreamReader {

    // Wrapper for the bytes in a dexfile pulled from the oatfile

    private byte[] data;
    private int offset;
    private int size;
    private String location;

    public DexFile(byte[] data, int offset, int size, String location) {
        this.data = data;
        this.offset = offset;
        this.size = size;
        this.location = location;
    }

    // Returns all of the bytes within its bounds
    public byte[] getBytes() {
        byte[] output = new byte[size];
        System.arraycopy(data, offset, output, 0, size);
        return output;
    }

    // Returns the string location pulled from the oat file
    public String getLocation() {
        return location;
    }

}
