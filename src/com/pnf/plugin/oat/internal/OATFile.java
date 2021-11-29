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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.pnfsoftware.jeb.util.serialization.annotations.Ser;
import com.pnfsoftware.jeb.util.serialization.annotations.SerConstructor;
import com.pnfsoftware.jeb.util.serialization.annotations.SerId;

/**
 * Description of an OAT file.
 * <p>
 * Description of the OAT header: refer to [art]/runtime/oat.h
 * </p>
 * Valid until version 214 (2021/11/22).
 * 
 */
@SuppressWarnings("unused")
@Ser
public class OATFile extends StreamReader {
    @SerId(1)
    private byte[] magic = new byte[4];
    @SerId(2)
    private int version;
    @SerId(3)
    private int checksum;
    @SerId(4)
    private int instructionSet;
    @SerId(5)
    private int instructionSetFeatures;
    @SerId(6)
    private int dexFileCount;
    @SerId(7)
    private int executableOffset;
    @SerId(8)
    private int interpreterToInterpreterBridgeOffset;
    @SerId(9)
    private int interpreterToCompiledCodeBridgeOffset;
    @SerId(10)
    private int jniDlsymLookupOffset;
    @SerId(11)
    private int portableImtConflictTrampolineOffset;
    @SerId(12)
    private int portableResolutionTrampolineOffset;
    @SerId(13)
    private int portableToInterpreterBridgeOffset;
    @SerId(14)
    private int quickGenericJniTrampolineOffset;
    @SerId(15)
    private int quickImtConflictTrampolineOffset;
    @SerId(16)
    private int quickResolutionTrampolineOffset;
    @SerId(17)
    private int quickToInterpreterBridgeOffset;
    @SerId(18)
    private int imagePatchDelta;
    @SerId(19)
    private int imageFileLocationOatChecksum;
    @SerId(20)
    private int imageFileLocationOatDataBegin;
    @SerId(21)
    private int keyValueStoreSize;
    @SerId(22)
    private byte[] keyValueStore;
    @SerId(23)
    private List<DexFile> dexFiles = new ArrayList<>();
    @SerId(24)
    private int oatDexFilesOffset;
    @SerId(25)
    private int jniDlsymLookupCriticalTrampolineOffset;
    @SerId(26)
    private int nterpTrampolineOffset;

    @SerConstructor
    protected OATFile() {
    }

    @SuppressWarnings("resource")
    public OATFile(byte[] data) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        int offset = 0;

        // Compare the magic numbers at start of oat
        stream.read(magic, 0, 4);
        if(!checkBytes(data, 0, OAT.magic)) {
            throw new IllegalArgumentException("Magic number does not match");
        }

        // Get oat format version. Parser designed on version 39, which is
        // compatible
        // at least until 45 (current at time of writing)
        version = Integer.parseInt(new String(readString(stream)).replaceFirst("^0+(?!$)", "").trim());

        // Not useful. Does not represent the header's data alone
        checksum = readInt(stream);

        if(version < 39) {
            throw new IllegalArgumentException("Unsupported OAT version " + version);
        }

        if(version > 214) {
            logger.warn("OAT version %d not officially unsupported, unexpected behavior may happen", version);
        }

        // OAT HEADER FORMAT: base is version 39, exceptions start in version 45+
        //
        // (all entries are 32-bit words)
        //        magic number ('oat\n')
        //        OAT version ('NNN\0')
        //        checksum of header
        //        ISA
        //        ISA features bitmask
        //        Dex file count
        //        OAT Dex Files Offset                          // ADDED in v127+
        //        offset of executable code section
        //        interpreter to interpreter bridge offset      // REMOVED in v170+
        //        interpreter to compile code bridge offset     // REMOVED in v170+
        //        jni dlsym lookup (trampoline) offset
        //        jni dlsym lookup critical trampoline offset   // ADDED in v180+
        //        portable imt conflict trampoline offset       // REMOVED in v45+
        //        portable resolution trampoline offset         // REMOVED in v45+
        //        portable to interpreter bridge offset         // REMOVED in v45+
        //        quick generic jni trampoline offset
        //        quick imt conflict trampoline offset
        //        quick resolution trampoline offset
        //        quick to interpreter bridge offset
        //        nterp trampoline offset                       // ADDED in v190+
        //        image patch delta                             // REMOVED in v162+
        //        image file location oat checksum              // BECOMES "boot image checksum" in v164+ / REMOVED in v166+
        //        image file location oat data begin            // REMOVED in v162+
        //        key value store length
        // *      key value store - hold some info about compilation
        // (start of dex headers)
        //        dex file location size
        // *      dex file location path string
        //        dex file location checksum
        //        dex file pointer from start of oatdata

        // ISA - see OAT.java
        instructionSet = readInt(stream);
        instructionSetFeatures = readInt(stream);
        dexFileCount = readInt(stream);
        if(version >= 127) {
            oatDexFilesOffset = readInt(stream);
        }
        executableOffset = readInt(stream);
        if(version < 170) {
            interpreterToInterpreterBridgeOffset = readInt(stream);
            interpreterToCompiledCodeBridgeOffset = readInt(stream);
        }
        jniDlsymLookupOffset = readInt(stream);
        if(version >= 180) {
            jniDlsymLookupCriticalTrampolineOffset = readInt(stream);
        }
        if(version < 45) {
            portableImtConflictTrampolineOffset = readInt(stream);
            portableResolutionTrampolineOffset = readInt(stream);
            portableToInterpreterBridgeOffset = readInt(stream);
        }
        quickGenericJniTrampolineOffset = readInt(stream);
        quickImtConflictTrampolineOffset = readInt(stream);
        quickResolutionTrampolineOffset = readInt(stream);
        quickToInterpreterBridgeOffset = readInt(stream);
        if(version >= 190) {
            nterpTrampolineOffset = readInt(stream);
        }
        if(version < 162) {
            imagePatchDelta = readInt(stream);
        }
        if(version < 166) {
            imageFileLocationOatChecksum = readInt(stream);
        }
        if(version < 162) {
            imageFileLocationOatDataBegin = readInt(stream);
        }

        keyValueStoreSize = readInt(stream);
        if(keyValueStoreSize >= 200*1024*1024) {
            // safety, most likely would be the result of an unsupported oat version 
            throw new IllegalArgumentException("KeyValue store is too large, parsing failed!");
        }
        keyValueStore = new byte[keyValueStoreSize];
        try {
            if(stream.read(keyValueStore) != keyValueStoreSize) {
                logger.warn("The KeyValue store was not fully read, the input file may be truncated");
            }
        }
        catch(IOException ex) {
            throw new RuntimeException(ex);
        }

        // After the key value store, there is a list of dex file headers
        // that give information about each dex file
        byte[] headerBytes = new byte[data.length - stream.available()];
        int dexFileLocationSize;
        String dexFileLocation;
        int dexFileLocationChecksum;
        int dexFilePointer;
        int dexFileOffset;
        int classes_offsets_size;
        int current = 0;

        // Loop through the dex file headers. Will be dexFileCount of them
        for(int idex = 0; idex < dexFileCount; idex++) {
            // Loop through dex file headers

            // Number of characters in dex files location data
            dexFileLocationSize = readInt(stream);
            if(dexFileLocationSize > 0x10000) {
                logger.warning("OAT File entry format appears to be unsupported");
                break;
            }

            // Location of dex file to compile from on disk
            dexFileLocation = readString(stream, dexFileLocationSize);
            // Checksum of the location string
            dexFileLocationChecksum = readInt(stream);
            // Pointer to location of dex file within the oat file
            dexFilePointer = readInt(stream);

            // Create a dex file out of the bytes starting from
            // dexFilePointer -> end of the oatfile. (Can't trust dex files size numbers)
            dexFiles.add(new DexFile(data, dexFilePointer, data.length - dexFilePointer, dexFileLocation));
        }

        // heuristically search for 2+ entries, in order to handle OAT header format changes
        if(dexFiles.size() < dexFileCount) {
            logger.info("Searching for %d additional DEX files heuristically...", dexFileCount - dexFiles.size());
            int idex = 2;
            byte[] b = new byte[0x1000];
            int n = stream.read(b, 0, 0x1000);
            for(int i = 0; i <= n - 4; i++) {
                int v = littleEndianBytesToInt(b, i);

                if(v >= 0 && (v + 100) <= data.length) {
                    int val = littleEndianBytesToInt(data, v);
                    if(val == 0x0A786564) {  // 'dex\n'
                        String name = String.format("Unknown DEX #%d", dexFiles.size() + 1);
                        dexFiles.add(new DexFile(data, v, data.length - v, name));
                        if(dexFiles.size() >= dexFileCount) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static int littleEndianBytesToInt(byte[] array, int offset) {
        //@formatter:off
        return (array[offset] & 0xFF)
                | ((array[offset + 1] << 8) & 0xFF00)
                | ((array[offset + 2] << 16) & 0xFF0000)
                | ((array[offset + 3] << 24) & 0xFF000000);
        //@formatter:on
    }

    public int getVersion() {
        return version;
    }

    public int getDexFileCount() {
        return dexFileCount;
    }

    public List<DexFile> getDexFiles() {
        return dexFiles;
    }

    public String getISAString() {
        // Might use this in a description
        switch(instructionSet) {
        case OAT.kArm:
            return "ARM";
        case OAT.kArm64:
            return "ARM64";
        case OAT.kThumb2:
            return "ARM_Thumb2";
        case OAT.kX86:
            return "X86";
        case OAT.X86_64:
            return "X86_64";
        case OAT.kMips:
            return "MIPS";
        case OAT.kMips64:
            return "MIPS64";
        default:
            return "Unknown";
        }
    }

    public String getKeyValueStore() {
        // Get the info from key value store
        // The returned string alternates
        // between key and value separated by
        // the null character '\0'
        return new String(keyValueStore);
    }
}
