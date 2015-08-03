package com.pnf.OAT;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class OATFile extends StreamReader {
	private byte[] magic = new byte[4];
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

		// Compare the magic numbers at start of oat
		stream.read(magic, 0, 4);
		if (!checkBytes(data, 0, OAT.magic)) {
			throw new IllegalArgumentException("Magic number does not match");
		}

		// Get oat format version. Parser designed on version 39, which is
		// compatible
		// at least until 45 (current at time of writing)
		version = Integer.parseInt(new String(readString(stream)).replaceFirst(
				"^0+(?!$)", "").trim());

		// Not useful. Does not represent the header's data alone
		checksum = readInt(stream);

		if (version >= 39 && version <= 45) {

			// OAT HEADER FORMAT -- version 39
			// Bytes: name
			// 0-3 : magic num (oat\n)
			// 4-7 : OAT version
			// 8-11 : checksum of header
			// 12-15: ISA
			// 16-19: ISA features bitmask
			// 20-23: Dex file count
			// 24-27: offset of executable code section
			// 28-31: interpreter to interpreter bridge offset
			// 32-35: interpreter to compile code bridge offset
			// 36-39: jni dlsym lookup offset
			// 40-43: portable imt conflict trampoline offset
			// 44-47: portable resolution trampoline offset
			// 48-51: portable to interpreter bridge offset
			// 52-55: quick generic jni trampoline offset
			// 56-59: quick imt conflict trampoline offset
			// 60-63: quick resolution trampoline offset
			// 64-67: quick to interpreter bridge offset
			// 68-71: image patch delta
			// 72-75: image file location oat checksum
			// 76-79: image file location oat data begin
			// 80-83: key value store length
			// 80-~~: key value store - hold some info about compilation
			// Start dex headers
			// 0-3 : dex file location size
			// 4-~~ : dex file location path string
			// 0-3 : dex file location checksum
			// 4-7 : dex file pointer from start of oatdata

			// ISA - see OAT.java
			instructionSet = readInt(stream);
			instructionSetFeatures = readInt(stream);
			dexFileCount = readInt(stream);
			// Get all the less useful information
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
			// KeyValueStoreSize needs to be read to give some compilation
			// information
			keyValueStoreSize = readInt(stream);
			keyValueStore = new byte[keyValueStoreSize];
			for (int index = 0; index < keyValueStoreSize; index++) {
				stream.read(keyValueStore, index, 1);
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
			OutputStream file;
			// Loop through the dex file headers. Will be dexFileCount of them
			for (int index = 0; index < dexFileCount; index++) {
				// Loop through dex file headers
				// Number of characters in dex files location data
				dexFileLocationSize = readInt(stream);
				// Location of dex file to compile from on disk
				dexFileLocation = readString(stream, dexFileLocationSize);
				// Checksum of the location string
				dexFileLocationChecksum = readInt(stream);
				// Pointer to location of dex file within the oat file
				dexFilePointer = readInt(stream);
				// Create a dex file out of the bytes starting from
				// dexFilePointer ->
				// end of the oatfile. (Can't trust dex files size numbers)
				dexFiles.add(new DexFile(data, dexFilePointer, data.length
						- dexFilePointer, dexFileLocation));

				// Calculate the location of the information about number of
				// classes in the dex file
				// I don't believe that this can be obfuscated successfully, but
				// it is a point of
				// failure if it can be changed
				current = data.length - stream.available();
				classes_offsets_size = readInt(stream, dexFilePointer - current
						+ 96);
				stream.skip(classes_offsets_size * 4);
			}
		} else {
			throw new IllegalArgumentException("Unsupported OAT version "
					+ version);
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
		// Might use this in a description
		switch (instructionSet) {
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
		// Get the info from key value store
		// The returned string alternates
		// between key and value separated by
		// the null character '\0'
		return new String(keyValueStore);
	}

}
