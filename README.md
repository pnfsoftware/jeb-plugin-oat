# Android OAT Plugin for JEB

This plugin extracts dex files from compiled OAT files that are used by the Android Runtime.

OAT Files pulled directly from phones will most likely be in ELF files.
This plugin does not extract directly from ELF files, but there is an
ELF plugin available. Otherwise, extracting the .rodata section of the
ELF file will yield a parseable OAT file.

Includes multidex support

Supports OAT Versions 39 - 45

Reference:

[An (older) OAT Format Link](https://www.blackhat.com/docs/asia-15/materials/asia-15-Sabanal-Hiding-Behind-ART-wp.pdf)

For the most up-to-date information about OAT, see the [android source files](https://android.googlesource.com/platform/art/)

Building from source: `ant -Dversion=1.0.5`