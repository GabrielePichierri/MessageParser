package it.csttech.messageparser;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.regex.*;

public class Log4jMessageParser implements MessageParser {

	private String regex;
	private Path file;
	private long position;

	public Log4jMessageParser(String filename, String regex) {
		this(filename, regex, 0L);
	}

	public Log4jMessageParser(String filename, String regex, long position) {
		this.regex = regex;
		file = Paths.get(filename);
		this.position = position;
	}

	public String nextMessage(){
		StringBuffer line = new StringBuffer();
		StringBuffer message = new StringBuffer();
		long positionSaver = position;
		do {
			line = readLine();
			if ( line == null) {
				// Then it's EOF.
				return null;
			} else { }
		} while ( ! isStartOfMessage(line) ); // Start of message found!
		message = message.append(line);
		while (true) {
			line = readLine();
			if ( line == null ) {
				break;
			} else if ( ! isStartOfMessage(line) ) {
				message = message.append(line);
				positionSaver = position;
			} else {
				setPosition( positionSaver );
				break;
			}
		}
		return message.toString();
	}

	public String prevMessage() {
		for (int i = 0; i < 2; i++) {
			if (position == 0) {
				//then we already are at the BOF; return null
				return null;
			}

			retrieveLineBeginPosition();
			long positionSaver = position; //stores the position of the first character of a line

			while( ! isStartOfMessage(readLine())) {
				//then this line is not a start of a message
				position = positionSaver--; //position yourself to the previous character, belonging to the previous line
				retrieveLineBeginPosition(); //move to the beginning of such line
				positionSaver = position; //re-store the position of the first character of such line
			}
			//we found the start of a message? TODO SOLVE THIS!
			setPosition(positionSaver);
		}
		return nextMessage();
	}

	private StringBuffer readLine() {

		ByteBuffer byteBuffer;
		StringBuffer line;

		try (FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ)) {
			fileChannel.position(position);
			byteBuffer = ByteBuffer.allocate(1);
			line = new StringBuffer();
			int currentChar = -1;

			while(fileChannel.read(byteBuffer) != -1) {
				byteBuffer.rewind(); 	//The position is set to zero and the mark is discarded.
				//                      Invoke this method before a sequence of get operations.
				currentChar = byteBuffer.get(byteBuffer.position());

				if(currentChar == '\n' || currentChar == '\r') {
					line.append((char) currentChar);
					break;
				} else {
					line.append((char) currentChar);
				}
			}

			if(currentChar == -1) {
				//Then it's the end of file; return null
				return null;
			}

			//If it's not the end of file:
			if(currentChar == '\r') {
				byteBuffer.rewind();
				char nextChar = (char) fileChannel.read(byteBuffer);
				if(nextChar == '\n') {
					//Then we found a '\r' followed by a '\n'
					//we ignore it and increase the position by one
				} else {
					//Then we had only found a '\r' and the next character is not '\n';
					//Return to the previous position for the next read
					fileChannel.position(fileChannel.position() - 1);
				}
			}

			setPosition( fileChannel.position() );  //update this object's position with the fileChannel position
		} catch (IOException e) {
			//Never(!) happens: main method should avoid any errors
			System.out.println("I/O Exception: " + e);
			return null;
		}
		return line;
	}

	private boolean isStartOfMessage(StringBuffer line) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);
		if (!matcher.find()) {
			return false;
		} else {
			return line == null ? false :	matcher.start() == 0;
		}
	}

	private void setPosition(long newPosition) {
		position = newPosition;
	}

	//This method is not called if position = 0;
	private void retrieveLineBeginPosition(){

		int c = getPreviousCharacter(1);

		while ( c == '\n' || c == '\r') {
			position--;
			c = getPreviousCharacter(1);
		}
		while ( c != '\n' && c != '\r' ) {
			position--;
			if ( position == 0 ) {
				break;
			}
			c = getPreviousCharacter(1);
		}
		return;

}
/*		long lineBeginPosition = position;
if ( position <= 0 ) {
setPosition(-1);
return;
// } else {
ByteBuffer byteBuffer;
try (FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ)) {
fileChannel.position(lineBeginPosition - 1);
byteBuffer = ByteBuffer.allocate(1);
int currentChar;

while( true ) {
System.out.println("position: " + fileChannel.position());
fileChannel.read(byteBuffer);
byteBuffer.rewind(); 	//The position is set to zero and the mark is discarded.
//                      Invoke this method before a sequence of get operations.
currentChar = byteBuffer.get(byteBuffer.position());
if(currentChar == '\n' || currentChar == '\r' ) {
lineBeginPosition = fileChannel.position();
break;
// } else {
lineBeginPosition = lineBeginPosition - 2;
fileChannel.position(lineBeginPosition);
System.out.println("else: " + fileChannel.position()); } }
position = lineBeginPosition;
return; } catch (IOException e) {
//Never(!) happens: main method should avoid any errors
System.out.println("I/O Exception: " + e);
position = -1;
return; } } }
*/

private int getPreviousCharacter( int offset ){
	ByteBuffer byteBuffer;
	if (position - offset < 0) {
		return -1;
	}
	try (FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ)) {
		byteBuffer = ByteBuffer.allocate(1);
		int currentChar;

		fileChannel.position(position - offset);
		fileChannel.read(byteBuffer);
		byteBuffer.rewind(); 	//The position is set to zero and the mark is discarded.
		//                      Invoke this method before a sequence of get operations.
		return byteBuffer.get(byteBuffer.position());

	} catch (IOException e) {
		//Never(!) happens: main method should avoid any errors
		System.out.println("I/O Exception: " + e);
		return -1;
	}
}


}
