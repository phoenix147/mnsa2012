package at.ac.tuwien.mnsa.sms;

import at.ac.tuwien.mnsa.sms.com.PDUencode;

/**
 * Hello world!
 *
 */
public class App 
{
	static String number = "";
	static String shortText = "blubb";
	static String longText = "Das ist eine sehr sehr lange Textnachricht zum Testen von " +
			"MultiSMSnachrichten und deshalb schicke ich sie dir gerade weiter um das zu " +
			"testen alkjfdöalsfjöaleifajsölfaksdflasdlfkaösdfkjuhiugkhjzgkjhlkjhlkhljhdtdgtfrs";
    public static void main( String[] args )
    {    	
    	System.out.println(longText.length());
    	
        System.out.println( "SMS Reader/Writer" );
        PDUencode psu = new PDUencode();
		String encoded = psu.MakePDUTextGSM(number, shortText);
		String[] encoded2 = psu.MakeMultiPDUTextGSM(number, shortText);
		
	
		
		System.out.println(encoded);
		System.out.println(encoded.length()/2-1);
		
		int lengthSum=0;
		for (String string : encoded2) {
			System.out.println(string);
			System.out.println(string.length()/2-1);
			
		}
		
		System.out.println(lengthSum/2-1);
		
//        	mc.connect();
//			System.in.read();
//			mc.sendSMS("+436802121580", "test sms");
    
        
        
    }
    
    
}
