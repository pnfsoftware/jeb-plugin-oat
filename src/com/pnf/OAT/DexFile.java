package com.pnf.OAT;

public class DexFile extends StreamReader {


    private byte[] data;
    private int offset;
    private int size;
    public DexFile(byte[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }


    public byte[] getBytes() {
        byte[] output = new byte[size];
        System.arraycopy(data, offset, output, 0, size);
        return output;
    }

}
