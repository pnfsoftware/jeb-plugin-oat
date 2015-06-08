package com.pnf.OAT;
public class OAT {
    public static final int kNone = 0;
    public static final int kArm = 0;
    public static final int kArm64 = 0;
    public static final int kThumb2 = 0;
    public static final int kX86 = 0;
    public static final int k86_64 = 0;
    public static final int kMips = 0;
    public static final int kMips64 = 0;

    public static final int kOatClassAllCompiled = 0;
    public static final int kOatClassSomeCompiled = 1;
    public static final int kOatClassNoneCompiled = 2;


    public static final byte[] magic = new byte[] { 'o', 'a', 't', '\n' };
}
