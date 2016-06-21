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
		this.regex = regex; // + ".*";
		file = Paths.get(filename);
		this.position = position;
	}

	public String nextMessage(){
		StringBuffer line = new StringBuffer();
		StringBuffer message = new StringBuffer();
		do {
			line = readLine();
			if ( line == null) {
				// Then it's EOF.
				return null;
			}
		} while ( ! isStartOfMessage(line) ); // Start of message found!
		message = message.append(line);
		while (true) {
			line = readLine();
			if ( line == null ) {
				break;
			} else if ( isStartOfMessage(line) ) {
				break;
			} else {
				message = message.append(line);
			}
		}
		return message.toString();

	}

	public String prevMessage() {
		//TODO
		if(position == 0) return "BOF";
		return "textPrevMessage";
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

	private long retrievePrevious(){ // TODO: finire
		if ( position == 0 ) {
			return -1;
		} else {
			ByteBuffer byteBuffer;
			try (FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ)) {
				fileChannel.position(position);
				byteBuffer = ByteBuffer.allocate(1);
				int currentChar = -1;

				while(fileChannel.read(byteBuffer) != -1) {
					byteBuffer.rewind(); 	//The position is set to zero and the mark is discarded.
					//                      Invoke this method before a sequence of get operations.
					currentChar = byteBuffer.get(byteBuffer.position());

					if(currentChar == '\n' || currentChar == '\r') {
						break;
					} else { }
				}
			} catch (IOException e) {
				//Never(!) happens: main method should avoid any errors
				System.out.println("I/O Exception: " + e);
				return -1;
			}
			return position;
		}
	}

}


/* commento al nextMessage:
//TOCHANGE
StringBuffer line = null;
StringBuffer message = null; //TODO use the StringBuffer append(StringBuffer sb) method
//to iteratively append the relevant lines to this message
//
//TODO: a while cycle which scans through the lines until it finds on that is the
//start of an error message (using the method isStartOfMessage()). Then I will start
//appending lines to the StringBuffer variable called message, until I find another
//message. Then I will return message.toString().
//

do {
line = readLine();
} while (line != null || !isStartOfMessage(line));

if(line == null) {
return null; //TOCHECK
}

if(isStartOfMessage(line)) {
do {
message.append(line);
line=readLine();
}
while (line != null || !isStartOfMessage(line));
}

return message.toString();
/*		if(isStartOfMessage(line)) {
*     return "line: " + line.toString() + ". Start of Message";
*     } else {
*     return "line: " + line.toString() + ". Not start of Message";
*     }
*/
