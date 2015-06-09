package com.pnf.OAT;

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
