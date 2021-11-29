# Android OAT Plugin for JEB

This plugin extracts DEX files from compiled OAT files (ELF) that are used by the Android Runtime.

Supports OAT versions 39 to 214 (Nov 2021).

Building from source: adjust the version number and run the `build-xxx` script.

## File Format
```
OAT HEADER FORMAT: base is version 39, exceptions start in version 45+

(all entries are 32-bit words)
       magic number ('oat\n')
       OAT version ('NNN\0')
       checksum of header
       ISA
       ISA features bitmask
       Dex file count
       OAT Dex Files Offset                          // ADDED in v127+
       offset of executable code section
       interpreter to interpreter bridge offset      // REMOVED in v170+
       interpreter to compile code bridge offset     // REMOVED in v170+
       jni dlsym lookup (trampoline) offset
       jni dlsym lookup critical trampoline offset   // ADDED in v180+
       portable imt conflict trampoline offset       // REMOVED in v45+
       portable resolution trampoline offset         // REMOVED in v45+
       portable to interpreter bridge offset         // REMOVED in v45+
       quick generic jni trampoline offset
       quick imt conflict trampoline offset
       quick resolution trampoline offset
       quick to interpreter bridge offset
       nterp trampoline offset                       // ADDED in v190+
       image patch delta                             // REMOVED in v162+
       image file location oat checksum              // BECOMES "boot image checksum" in v164+ / REMOVED in v166+
       image file location oat data begin            // REMOVED in v162+
       key value store length
*      key value store - hold some info about compilation
(start of dex headers)
       dex file location size
*      dex file location path string
       dex file location checksum
       dex file pointer from start of oatdata
```