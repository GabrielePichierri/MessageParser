package it.csttech.messageparser;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.regex.Pattern;

public class Log4jMessageParser implements MessageParser {

	//private String filename;
	private String regex;
	private Path file;
	private long position = 0;

	public Log4jMessageParser(String filename, String regex) {
		//this.filename = filename;
		this.regex = regex;
		file = Paths.get(filename);
	}
	
	public String nextMessage() {
		//TOCHANGE
		StringBuffer line = null;
		StringBuffer message = null; //TODO use the StringBuffer append(StringBuffer sb) method
		//to iteratively append the relevant lines to this message
		//
		 try {
			//TODO: a while cycle which scans through the lines until it finds on that is the
			//start of an error message (using the method isStartOfMessage()). Then I will start
			//appending lines to the StringBuffer variable called message, until I find another
			//message. Then I will return message.toString().
			//
			line = readLine();

			if(line == null) {
				return "EmptyLine";
			}
			if(isStartOfMessage(line)) {
				return "yes";
			} else {
				return "no";
			}

		} catch (IOException e) {
			System.out.println("I/O Exception: " + e);
			return null;
		}
	}

	public String prevMessage() {
		//TODO
		return "textPrevMessage";
	}

	private StringBuffer readLine() throws IOException {
		FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ);
		fileChannel.position(position);
		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
		StringBuffer line = new StringBuffer();
		char currentChar = ' ';

		while(fileChannel.read(byteBuffer) > 0) {
			byteBuffer.rewind(); 	//The position is set to zero and the mark is discarded.
									//Invoke this method before a sequence of get operations.
			currentChar = (char) byteBuffer.get(byteBuffer.position()); 

			if(currentChar == '\n' || currentChar == '\r') {				
				break;
			} else {
				line.append(currentChar);
			}
		}
		
		position = fileChannel.position();
		System.out.println(fileChannel.position()); //TOREMOVE
		
		if(currentChar == '\r') {
			byteBuffer.rewind();
			char nextChar = (char) fileChannel.read(byteBuffer);
			if(nextChar == '\n') {
				position -= 1;
			} else {
				
			}
		}
	
		return line.length()==0?null:line; //TOCHECK
	}

	private boolean isStartOfMessage(StringBuffer line) {
		//TODO
		return Pattern.matches(regex, line);
	}
}