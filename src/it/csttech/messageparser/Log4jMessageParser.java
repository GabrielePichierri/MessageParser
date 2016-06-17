package it.csttech.messageparser;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.channels.*;

public class Log4jMessageParser implements MessageParser {

	//private String filename;
	private String regex;
	private Path file;
	private FileChannel fileChannel; 

	public Log4jMessageParser(String filename, String regex) {
		//this.filename = filename;
		this.regex = regex;

		file = Paths.get(filename);
		try(FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ)) {
			this.fileChannel = fileChannel;
			fileChannel.position(10);

			System.out.println(this.fileChannel.position());
		} catch (IOException e) {
			System.out.println("I/O Exception: " + e);
		}
	}
	
	public String nextMessage() {
		String message = null;
		try {
			message = readLine();
		} catch (IOException e) {
			System.out.println("I/O Exception: " + e);
		} 
	return message;
	}

	public String prevMessage() {
	//TODO
	return "textPrevMessage";
	}

	private String readLine() throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
		StringBuffer line = new StringBuffer();
		char prevChar, currentChar;

		while(fileChannel.read(byteBuffer) > 0) {
			byteBuffer.flip(); //Set the limit to the current position and then the position to 0.
			currentChar = (char) byteBuffer.get();
			if(currentChar == '\r' || currentChar == '\n') {
				return line.toString();
			} else {
				line.append(currentChar);
			}
		}

	return null; //TOCHECK
	}
}