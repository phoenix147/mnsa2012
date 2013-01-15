package at.ac.tuwien.mnsa.sms;

import java.io.IOException;

import at.ac.tuwien.mnsa.sms.com.ModemConnection;
import at.ac.tuwien.mnsa.sms.exceptions.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	ModemConnection mc = new ModemConnection("/dev/ttyS66");
    	
    	
        System.out.println( "SMS Reader/Writer" );
        try {
        	mc.connect();
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
}
