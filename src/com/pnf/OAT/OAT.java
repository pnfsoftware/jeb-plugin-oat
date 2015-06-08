package com.pnf.OAT;
public class OAT {
    public static final int kNone = 0;
    public static final int kArm = 1;
    public static final int kArm64 = 2;
    public static final int kThumb2 = 3;
    public static final int kX86 = 4;
    public static final int X86_64 = 5;
    public static final int kMips = 6;
    public static final int kMips64 = 7;

    public static final int kOatClassAllCompiled = 0;
    public static final int kOatClassSomeCompiled = 1;
    public static final int kOatClassNoneCompiled = 2;


    public static final byte[] magic = new byte[] { 'o', 'a', 't', '\n' };
}
