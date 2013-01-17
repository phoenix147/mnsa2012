package at.ac.tuwien.mnsa.sms.com;

//#endif

import java.util.Random;

/**
* Class that creates and sends an SMS
*
* @author Nikita Kapitonov <nikita.kapitonov@gmail.com> / www.teplomonitor.ru
*/
public class PDUencode {

//#if DebugLevel=="debug"
  private static final boolean DEBUG = true;
//#elif DebugLevel=="warn" || DebugLevel=="fatal"
//# private static final boolean DEBUG = false;
//#endif
  private static final int WAIT_TIME = 200;
  private static final int MAXIMUM_DATA_SMS_LENGTH = 140;
  private static final int MAXIMUM_UCS2_SMS_LENGTH = 70;
  private static final int MAXIMUM_GSM_SMS_LENGTH = 160;
  // <Singleton pattern>
//  private static SMSFactory instance;
//
//  public static synchronized SMSFactory getInstance(ATCommand atCommand) {
//      if (instance == null) {
//          instance = new SMSFactory();
//          instance.atCommand = atCommand;
//      }
//      return instance;
//  }
//  // </Singleton pattern>
  private int smsSequenceNumber;
//  private ATCommand atCommand;
//
//  private String atCommandSend(String ATCmd) {
//      try {
//          return atCommand.send(ATCmd + '\r');
//      } catch (Exception ex) {
//          ex.printStackTrace();
//          return null;
//      }
//  }
//
//  private SMSFactory() {
//      if (DEBUG) {
//          System.out.println("\n\n\n<Constructing SMS>");
//      }
//      Random rnd = new Random();
//      smsSequenceNumber = rnd.nextInt(256);
//
//      boolean ok = false;
//
//      do {
//          try {
//              //set mode to PDU Mode
//              int t = 0;
//              do {
//                  Thread.sleep(t * 1000);
//                  ok = atCommandSend("AT+CMGF=0").indexOf("OK") >= 0;
//                  if (DEBUG) {
//                      System.out.println("set mode to PDU Mode... " + ok);
//                  }
//              } while (!ok);
//
//              //setting SMS mode
//              ok = atCommandSend("AT+CSMS=1").indexOf("OK") >= 0;
//              if (DEBUG) {
//                  System.out.println("setting SMS mode to new standart... " + ok);
//              }
//              //prefer CSD bearer when sending SMSs
//              ok &= atCommandSend("AT+CGSMS=3").indexOf("OK") >= 0;
//              if (DEBUG) {
//                  System.out.println("prefer CSD bearer when sending SMSs... " + ok);
//              }
//              //return error when sending sms fail
//              ok &= atCommandSend("AT^SM20=1,0").indexOf("OK") >= 0;
//              if (DEBUG) {
//                  System.out.println("enable error when sending sms fail... " + ok);
//              }
//              if (DEBUG) {
//                  // if initialization was successful
//                  if (ok) {
//                      System.out.println("SMS Init done");
//                      // if initialization was not successful
//                  } else {
//                      System.out.println("Error");
//                  }
//              }
//
//              String smsFULL = atCommandSend("AT^SMGO?");
//              int a = smsFULL.indexOf("^SMGO:");
//              if (a >= 0) {
//                  a = smsFULL.indexOf(',', a);
//                  a++;
//                  smsFULL = smsFULL.substring(a, a + 1);
//                  if (smsFULL.equals("1") || smsFULL.equals("2")) {
//                      if (DEBUG) {
//                          System.out.println("SMS storage is full - time to clean up!");
//                      }
//                      int i = 1;
//                      while (atCommandSend("AT+CMGD=" + i).indexOf("OK") >= 0) {
//                          if (DEBUG) {
//                              System.out.println("Deleting SMS at index " + i);
//                          }
//                          i++;
//                      }
//                  }
//              } else {
//                  if (DEBUG) {
//                      System.out.println("CANNOT CHECK IF SMS STORAGE IS FULL");
//                  }
//              }
//          } catch (Exception ex) {
//              ok = false;
//              ex.printStackTrace();
//          }
//      } while (!ok);
//      if (DEBUG) {
//          System.out.println("</Constructing SMS>\n\n\n");
//      }
//  }

//  private void setCodePage(boolean isUCS2) throws ATCommandFailedException {
//      if (isUCS2) {
//          atCommandSend("AT+CSCS=\"UCS2\"").indexOf("OK");
//      } else {
//          atCommandSend("AT+CSCS=\"GSM\"").indexOf("OK");
//      }
//  }
//
//  private synchronized String sendPDU(String PDU, boolean isUCS2) throws ATCommandFailedException {
//      String response;
//      try {
//          Thread.sleep(WAIT_TIME);
//      } catch (InterruptedException ex) {
//          if (DEBUG) {
//              ex.printStackTrace();
//          }
//      }
//      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//      //////////////////////////////////////////THIS IS CRITICAL AREA//////////////////////////////////////////////
//      //!!!!!!!!!WARNING!!!!!!!!!!WARNING!!!!!!!!!WARNING!!!!!!!!!WARNING!!!!!!!!!WARNING!!!!!!!!!WARNING!!!!!!!!!!
//      //All other calls anywhere else in the project to this atCommand should also be synchronized for this to work
//      ///////////////Otherwise, there is probability of SMS not being sent or stuck in the process/////////////////
//      synchronized (atCommand) {
//          setCodePage(isUCS2);
//          response = atCommandSend("AT+CMGS=" + (PDU.length() / 2 - 1));
//          if (DEBUG) {
//              System.out.println(response);
//          }
//          if (response.indexOf('>') >= 0) {
//              try {
//                  Thread.sleep(WAIT_TIME);
//              } catch (InterruptedException ex) {
//                  if (DEBUG) {
//                      ex.printStackTrace();
//                  }
//              }
//              response = atCommandSend(PDU + "\032");
//          }
//      }
//      /////////////////////////////////////////CRITICAL AREA ENDS//////////////////////////////////////////////////
//      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//      return response;
//  }

  /**
   * Sends text via one or more SMS
   * @param address recepient's phone number
   * @param text of the message, can be in any language
   * @param allowLong true if allow concatenated SMS's, if false will cut excess text
   * @param isUCS2 true if allow international characters, and one SMS will contain only 70 characters maximum, if false will convert to latin characters and encode to GSM 7-bit encoding resulting in 160 characters maximum
   * @return String containing all warning and errors that occured while trying to send SMS
   */
//  public String SendText(String address, String text, boolean allowLong, boolean isUCS2) {
//
//      if (address.indexOf('#') >= 0) {
//          return "Try to send SMS to incorrect address " + address;
//      }
//      String result = "";
//      String text_remains = "";
//      boolean onesms = true;
//
//      if (DEBUG) {
//          System.out.println("Trying to send SMS: ");
//      }
//
//      if (!isUCS2) {
//          text = javaToGSM(text);
//      }
//
//      if ((text.length() > MAXIMUM_GSM_SMS_LENGTH && !isUCS2) || (text.length() > MAXIMUM_UCS2_SMS_LENGTH && isUCS2)) {
//          if (allowLong) {
//              onesms = false;
//          } else {
//              result += "Too long text!";
//              if (DEBUG) {
//                  System.out.println(result);
//              }
//              if (isUCS2) {
//                  text_remains = text.substring(MAXIMUM_UCS2_SMS_LENGTH);
//                  text = text.substring(0, MAXIMUM_UCS2_SMS_LENGTH);
//              } else {
//                  text_remains = text.substring(MAXIMUM_GSM_SMS_LENGTH);
//                  text = text.substring(0, MAXIMUM_GSM_SMS_LENGTH);
//              }
//          }
//      }
//
//      try {
//          if (onesms) {
//              String PDUText;
//              String response;
//              if (isUCS2) {
//                  PDUText = MakePDUTextUCS2(address, text);
//              } else {
//                  PDUText = MakePDUTextGSM(address, text);
//              }
//              response = sendPDU(PDUText, isUCS2);
//              if (response.indexOf("OK") < 0) {
//                  result += "Error during sending SMS: " + response;
//                  if (DEBUG) {
//                      System.out.println(result);
//                  }
//                  return result;
//              }
//          } else {
//              String[] PDUText;
//              String response;
//              if (isUCS2) {
//                  PDUText = MakeMultiPDUTextUCS2(address, text);
//              } else {
//                  PDUText = MakeMultiPDUTextGSM(address, text);
//              }
//              for (int i = 0; i < PDUText.length; i++) {
//                  response = sendPDU(PDUText[i], isUCS2);
//                  if (response.indexOf("OK") <= 0) {
//                      result += "<FAIL>Error during sending SMS: " + response + "</FAIL>";
//                      if (DEBUG) {
//                          System.out.println(result);
//                      }
//                      return result;
//                  }
//              }
//          }
//          result += "SMS with text:\n\"" + text + (text_remains.length() > 0 ? '(' + text_remains + ')' : "") + "\"\nwas sent to the address " + address;
//      } catch (Exception e) {
//          e.printStackTrace();
//          result += "Exception while sending SMS! " + e.getMessage();
//          if (DEBUG) {
//              System.out.println(result);
//          }
//          return result;
//      }
//      return result;
//  }
//
//  public void SendData(String num, byte[] data) {
//
//      if (DEBUG) {
//          System.out.println("Trying to send SMS: ");
//      }
//
//      if (data.length > MAXIMUM_DATA_SMS_LENGTH) {
//          if (DEBUG) {
//              System.out.println("ERROR: too long data array");
//          }
//          return;
//      }
//
//      boolean ok = true;
//      do {
//          try {
//              String PDUData = MakePDUData(num, data);
//              atCommandSend("AT+CMGS=" + (PDUData.length() / 2 - 1));
//              Thread.sleep(WAIT_TIME);
//              atCommandSend(PDUData + "\032");
//              Thread.sleep(WAIT_TIME);
//          } catch (Exception e) {
//              ok = false;
//              e.printStackTrace();
//          }
//      } while (!ok);
//  }

//  private String MakePDUTextUCS2(String num, String msg) {
//      return "000100" + Util.intToHexFixedWidth(num.length() - 1, 2) + "91" + AddrToPDU(num) + "00" + "08" + Util.intToHexFixedWidth(msg.length() * 2, 2) + ATStringConverter.Java2UCS2Hex(msg);
//  }
//
//  private String[] MakeMultiPDUTextUCS2(String num, String msg) {
//      int parts = msg.length() / 67 + (msg.length() % 67 == 0 ? 0 : 1);
//      String[] result = new String[parts];
//      int seqNum = getSequenceNumber();
//      for (int i = 0; i < parts; i++) {
//          String msgpart;
//          if (i == parts - 1) {
//              msgpart = msg.substring(i * 67);
//          } else {
//              msgpart = msg.substring(i * 67, (i + 1) * 67);
//          }
//          result[i] = "004100" + Util.intToHexFixedWidth(num.length() - 1, 2) + "91" + AddrToPDU(num) + "00" + "08" + Util.intToHexFixedWidth(msgpart.length() * 2 + 6, 2) + "050003" + Util.intToHexFixedWidth(seqNum, 2) + Util.intToHexFixedWidth(parts, 2) + Util.intToHexFixedWidth(i + 1, 2) + ATStringConverter.Java2UCS2Hex(msgpart);
//      }
//      return result;
//  }

  public String MakePDUTextGSM(String num, String msg) {
      String hexSeptets = gsmToSeptetsToHex(msg, null);

      return "001100" + Util.intToHexFixedWidth(num.length(), 2)  + "91" + semiOctetToString(num) + "0000AA" + Util.intToHexFixedWidth(msg.length(), 2) + hexSeptets;
  }
  
  public String semiOctetToString(String octet) {
		String out = "";	
		for(int i=0;i<octet.length();i=i+2)
		{
		  	String temp = octet.substring(i,i+2);	
			out = out + temp.charAt(1) + temp.charAt(0);
		}

		return out;
	}

  public String[] MakeMultiPDUTextGSM(String num, String msg) {
      final int UDH_LENGTH_IN_SEPTETS = 7;

      int parts = (msg.length() / (160 - UDH_LENGTH_IN_SEPTETS)) + (msg.length() % (160 - UDH_LENGTH_IN_SEPTETS) == 0 ? 0 : 1);
      String[] result = new String[parts];
      
      int seqNum = getSequenceNumber();
      for (int i = 0; i < parts; i++) {
          String msgpart;
          if (i == parts - 1) {
              msgpart = msg.substring(i * (160 - UDH_LENGTH_IN_SEPTETS));
          } else {
              msgpart = msg.substring(i * (160 - UDH_LENGTH_IN_SEPTETS), (i + 1) * (160 - UDH_LENGTH_IN_SEPTETS));
          }
          String UDH = "050003" + Util.intToHexFixedWidth(seqNum, 2) + Util.intToHexFixedWidth(parts, 2) + Util.intToHexFixedWidth(i + 1, 2);
          String hexSeptets = gsmToSeptetsToHex(msgpart, UDH);
          result[i] = "004100" + Util.intToHexFixedWidth(num.length(), 2) + "91" + semiOctetToString(num) + "00" + "00" + Util.intToHexFixedWidth(UDH_LENGTH_IN_SEPTETS + msgpart.length(), 2) + hexSeptets;
          //return "001100" + Util.intToHexFixedWidth(num.length(), 2)  + "91" + semiOctetToString(num) + "0000AA" + Util.intToHexFixedWidth(msg.length(), 2) + hexSeptets;

      }
      return result;
  }

//  private String javaToGSM(String text) {
//      if (DEBUG) {
//          System.out.println("Trying to convert: " + text);
//      }
//      String gsmString = "";
//      for (int i = 0; i < text.length(); i++) {
//          try {
//              gsmString += ATStringConverter.Java2GSM(text.substring(i, i + 1));
//          } catch (IllegalArgumentException illegalArgumentException) {
//              String character = text.substring(i, i + 1);
//              if (DEBUG) {
//                  System.out.println("Cannot convert character: " + character);
//              }
//              gsmString += character;
//          }
//      }
//      if (DEBUG) {
//          System.out.println("text.length=" + text.length() + ", gsmString.length=" + gsmString.length());
//      }
//      return gsmString;
//  }

  private String gsmToSeptetsToHex(String gsmString, String UDH) {
      if (DEBUG) {
          System.out.println("Trying to convert gsmToSeptetsToHex: gsmString=" + gsmString + ", UDH=" + UDH);
      }
      int skipBits = 0;
      if (UDH != null) {
          if (UDH.length() != 0) {
              int udhLength = UDH.length() / 2;
              skipBits = ((udhLength + 1) * 8) + ((7 - ((udhLength + 1) * 8) % 7) % 7); //Number of bits to skip from beginning
          }
      }
      if (DEBUG) {
          System.out.println("skipBits=" + skipBits);
      }
      boolean[] udBool = new boolean[skipBits + gsmString.length() * 7 + ((8 - ((skipBits + gsmString.length() * 7) % 8)) % 8)]; //Bits array of final data
      if (DEBUG) {
          System.out.println("gsmString.length()=" + gsmString.length() + "; udBool[].length=" + udBool.length);
      }
      if (UDH != null) {
          if (UDH.length() != 0) {
              for (int i = 0; i < UDH.length() / 2; i++) { //Fill the first bits of udBool with UDH bits
                  boolean[] t = Util.intToBinaryArrayFixedWidth(Integer.parseInt(UDH.substring(i * 2, (i + 1) * 2), 16), 8);
                  System.arraycopy(t, 0, udBool, i * 8, 8);
              }
          }
      }
      for (int i = 0; i < gsmString.length(); i++) { //Fill remaining bits of udBool with text septets bits
          boolean[] t = Util.intToBinaryArrayFixedWidth(gsmString.charAt(i), 7);
          //<DEBUG>
          String boolArrToString = "";
          for (int j = 0; j < t.length; j++) {
              boolArrToString += (t[j] == true ? '1' : '0');
          }
          if (DEBUG) {
              System.out.println("t=" + boolArrToString);
          }
          //</DEBUG>
          System.arraycopy(t, 0, udBool, skipBits + (i * 7), 7);
      }
      int length = udBool.length / 8; // length of UD in octets
      if (DEBUG) {
          System.out.println("length=" + length);
      }
      String hexResult = ""; //Hex string containing UD
      for (int i = 0; i < length; i++) {
          boolean[] t = new boolean[8];
          System.arraycopy(udBool, i * 8, t, 0, 8);
          hexResult += Util.intToHexFixedWidth(Util.binaryArrayToInt(t), 2);
      }
      if (DEBUG) {
          System.out.println("hexResult=" + hexResult);
      }
      return hexResult;
  }

  private String AddrToPDU(String s) {
      String r = new String();
      int i = 0;
      s = s.substring(1, s.length());
      if (s.length() % 2 > 0) {
          s += 'F';
      }
      while (i < s.length()) {
          r += s.charAt(i + 1) + s.charAt(i);
          i += 2;
      }
      return r;
  }                       //Converts phone number from +71234567890 format to PDU format 1732547698F0

  private int getSequenceNumber() {
	  Random rnd = new Random();
	  smsSequenceNumber = rnd.nextInt(256);
      smsSequenceNumber++;
      smsSequenceNumber = smsSequenceNumber % 256;
      return smsSequenceNumber;
  }
}
