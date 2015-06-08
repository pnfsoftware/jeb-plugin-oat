package com.pnf.OAT;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;


public class OATFile extends StreamReader {
    private boolean checksumMatches;

    private byte[] magic = new byte[] {'o', 'a', 't', '\n'};
    private int version;
    private int checksum;
    private int instructionSet;
    private int instructionSetFeatures;
    private int dexFileCount;
    private int executableOffset;
    private int interpreterToInterpreterBridgeOffset;
    private int interpreterToCompiledCodeBridgeOffset;
    private int jniDlsymLookupOffset;
    private int portableImtConflictTrampolineOffset;
    private int portableResolutionTrampolineOffset;
    private int portableToInterpreterBridgeOffset;
    private int quickGenericJniTrampolineOffset;
    private int quickImtConflictTrampolineOffset;
    private int quickResolutionTrampolineOffset;
    private int quickToInterpreterBridgeOffset;
    private int imagePatchDelta;
    private int imageFileLocationOatChecksum;
    private int imageFileLocationOatDataBegin;
    private int keyValueStoreSize;
    private byte[] keyValueStore;


    private List<DexFile> dexFiles = new ArrayList<>();

    public OATFile(byte[] data) {

        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        int offset = 0;

        stream.read(magic, 0, 4);

        if(!checkBytes(data, 0, magic)) {
            throw new IllegalArgumentException("Magic number does not match");
        }

        version = Integer.parseInt(new String(readString(stream)).replaceFirst("^0+(?!$)", "").trim());

        checksum = readInt(stream);

        if(version == 39) {

            // OAT HEADER FORMAT -- version 39
            // 0-3  : magic num (oat\n)
            // 4-7  : OAT version
            // 8-11 : checksum of header
            // 12-15: ISA
            // 16-19: ISA features bitmask
            // 20-23: Dex file count
            // 24-27: offset of executable code section
            // 28-31: don't need
            // 32-35: don't need
            // 36-39: don't need
            // 40-43: don't need
            // 44-47: don't need
            // 48-51: don't need
            // 52-55: don't need
            // 56-59: don't need
            // 60-63: don't need
            // 64-67: don't need
            // 68-71: don't need
            // 72-75: don't need
            // 76-79: don't need
            // 80-83: key value store length
            // 80-~~: key value store
            // Start dex headers
            // 0-3  : dex file location size
            // 4-~~ : dex file location path string
            // 0-3  : dex file location checksum
            // 4-7  : dex file pointer from start of oatdata
            instructionSet = readInt(stream);
            instructionSetFeatures = readInt(stream);
            dexFileCount = readInt(stream);
            executableOffset = readInt(stream);
            interpreterToInterpreterBridgeOffset = readInt(stream);
            interpreterToCompiledCodeBridgeOffset = readInt(stream);
            jniDlsymLookupOffset = readInt(stream);
            portableImtConflictTrampolineOffset = readInt(stream);
            portableResolutionTrampolineOffset = readInt(stream);
            portableToInterpreterBridgeOffset = readInt(stream);
            quickGenericJniTrampolineOffset = readInt(stream);
            quickImtConflictTrampolineOffset = readInt(stream);
            quickResolutionTrampolineOffset = readInt(stream);
            quickToInterpreterBridgeOffset = readInt(stream);
            imagePatchDelta = readInt(stream);
            imageFileLocationOatChecksum = readInt(stream);
            imageFileLocationOatDataBegin = readInt(stream);
            keyValueStoreSize = readInt(stream);
            keyValueStore = new byte[keyValueStoreSize];
            for(int index=0; index < keyValueStoreSize; index++) {
                stream.read(keyValueStore, index, 1);
            }

            byte[] headerBytes = new byte[data.length - stream.available()];
            System.arraycopy(data, 0, headerBytes, 0, headerBytes.length);
            Adler32 actualChecksum = new Adler32();
            actualChecksum.update(headerBytes);
            checksumMatches = (actualChecksum.getValue() == (long)checksum);
            int dexFileLocationSize;
            String dexFileLocation;
            int dexFileLocaionChecksum;
            int dexFilePointer;
            List<Integer> classOffsets;
            for(int index=0; index < dexFileCount; index++) {
                // Loop through dex files
                dexFileLocationSize = readInt(stream);
                dexFileLocation = readString(stream, dexFileLocationSize);
                dexFileLocaionChecksum = readInt(stream);
                dexFilePointer = readInt(stream);
                dexFiles.add(new DexFile(data, dexFilePointer, data.length - dexFilePointer, dexFileLocation));

            }
        }
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
        switch(instructionSet) {
            case OAT.kArm:
                return "kArm";
            case OAT.kArm64:
                return "kArm64";
            case OAT.kThumb2:
                return "kThumb2";
            case OAT.kX86:
                return "kX86";
            case OAT.X86_64:
                return "X86_64";
            case OAT.kMips:
                return "kMips";
            case OAT.kMips64:
                return "kMips64";
            default:
                return "kNone";
        }
    }
    public String getKeyValueStore() {
        return new String(keyValueStore);
    }

}
