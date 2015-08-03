package com.pnf.OAT;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class StreamReader {
	// Give a logger to all subclasses
	protected static final ILogger logger = GlobalLog
			.getLogger(StreamReader.class);

	// Read an int from the stream at an offset from the current position
	// Leaves a mark
	protected static int readInt(ByteArrayInputStream stream, int offset) {
		stream.mark(0);
		stream.skip(offset);
		int output = readInt(stream);
		stream.reset();
		return output;
	}

	// Read an short from the stream at an offset from the current position
	// Leaves a mark
	protected static short readShort(ByteArrayInputStream stream, int offset) {
		stream.mark(0);
		stream.skip(offset);
		short output = readShort(stream);
		stream.reset();
		return output;
	}

	// Read an int from the stream
	// No mark
	protected static int readInt(ByteArrayInputStream stream) {
		byte[] temp = new byte[4];
		stream.read(temp, 0, 4);
		return ByteBuffer.wrap(temp).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	// Read an short from the stream
	// No mark
	protected static short readShort(ByteArrayInputStream stream) {
		byte[] temp = new byte[2];
		stream.read(temp, 0, 2);
		return ByteBuffer.wrap(temp).order(ByteOrder.LITTLE_ENDIAN).getShort();
	}

	// Read a string from the stream. Goes until sees null character '\0'
	protected static String readString(ByteArrayInputStream stream) {
		String output = "";
		char character;
		while (stream.available() > 0) {
			character = (char) stream.read();
			if (character == 0)
				break;
			output = output + character;
		}
		return output;
	}

	// Reads a string of given length, ignoring null char
	protected static String readString(ByteArrayInputStream stream, int length) {
		String output = "";
		char character;
		for (int index = 0; index < length; index++) {
			character = (char) stream.read();
			output = output + character;
		}
		return output;
	}

	// Extra implementation of checkbytes. Does byte by byte comparison
	protected static boolean checkBytes(byte[] data, int offset,
			byte... checkBytes) {
		for (int index = 0; index < checkBytes.length; index++) {
			if (data[offset + index] != checkBytes[index])
				return false;
		}
		return true;
	}
}
