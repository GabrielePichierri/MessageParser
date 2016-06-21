package it.csttech;
import it.csttech.messageparser.*;
import java.util.*;

public class SimpleTester {

  private static final String REGEX = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}";
  private static final String FILE_NAME = "input/inputSingolo.log"; // "input/phoenix-develop-2016-06-09-1.log";
  private static MessageParser messageParser;

  public static void main(String[] args) {
    String testingMode;
    if (args.length == 0) {
      testingMode = "2";
    } else {
      testingMode = args[0];
    }
    messageParser = new Log4jMessageParser(FILE_NAME,REGEX);
    switch (testingMode) {
      case "0": MethodZero( args[1], args[2] );
      break;
      case "1": MethodOne();
      break;
      case "2": MethodTwo();
      break;
      default: System.out.println("Method number not recognized.");
      break;
    }
    return;
  }

  private static String basicOperation(String direction){
    switch(direction){
      case "-1": return messageParser.prevMessage();
      case "1": return messageParser.nextMessage();
      default: return null;
    }
  }

  private static String offSet(int lineNumber){
    String result = null;
    for (int i = 0 ; i < lineNumber; i++) {
      result = messageParser.nextMessage();
    }
    return result;
  }

  private static void MethodZero(String offsetString, String sizeString){
    int offset = Integer.parseInt(offsetString);
    if (offSet(offset) == null) {
      return;
    }
    int size = Integer.parseInt(sizeString);
    Random random = new Random(System.nanoTime());
    String message = "";
    for (int i = 0 ; i < size && message != null; i++) {
      System.out.println(message);
      boolean direction = random.nextBoolean();
      message = basicOperation(direction?"1":"-1");
      System.out.println(direction?"Avanti":"Indietro");
    }
    return;
  }

  private static void MethodOne(){
    Scanner scanner = new Scanner(System.in);
    String newInput;
    String output;
    for (;;) {
      System.out.print("\nInsert -1 to get the previous message, 1 to get the next one, or 0 to exit the program: ");
      newInput = new String(scanner.next());
      while( ! (newInput.equals("-1") || newInput.equals("0") || newInput.equals("1")) ){
        System.out.print("\nInsert -1 to get the previous message, 1 to get the next one, or 0 to exit the program: ");
        newInput = scanner.next();
      }
      if (newInput.equals("0")){
        return;
      }
      output = basicOperation(newInput);
      if (output == null) {
        System.out.println("No message available.");
        return;
      }
      System.out.println(output);
    }
  }

  private static void MethodTwo(){
    System.out.println(messageParser.nextMessage());
    System.out.println(messageParser.nextMessage());
    System.out.println(messageParser.prevMessage());
  }

}
