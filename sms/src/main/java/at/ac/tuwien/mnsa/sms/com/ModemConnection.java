package at.ac.tuwien.mnsa.sms.com;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import at.ac.tuwien.mnsa.sms.exceptions.AlreadyOpenException;
import at.ac.tuwien.mnsa.sms.exceptions.ConnectionException;
import at.ac.tuwien.mnsa.sms.exceptions.DataException;
import at.ac.tuwien.mnsa.sms.exceptions.SerialPortException;


public class ModemConnection {

	private static Logger LOG = Logger.getLogger(ModemConnection.class);

	private LinkedList<String> availablePorts;

	private CommPortIdentifier serialPortId;
	private Enumeration enumComm;
	private SerialPort serialPort;
	private OutputStream outputStream;
	private InputStream inputStream;
	private Boolean serialPortOpen = false;
	
	private String portName;

    private static int ASCII_NEW_LINE = 10;
    private static int ASCII_STRG_Z = 26;

	
	private static int baudrate = 460800;
	private static int dataBits = SerialPort.DATABITS_8;
	private static int stopBits = SerialPort.STOPBITS_1;
	private static int parity = SerialPort.PARITY_NONE;
	
	private static int serialTimeout = 5000;
	private static int serialThreshold = 1;
	
	private static int sleeptime = 30;

	public ModemConnection(String portName) {
		try {
			this.refreshPortList();
		} catch (AlreadyOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.portName = portName;
	}


	public void connect() throws ConnectionException {
		try {
			this.openSerialPort(this.portName);
			LOG.debug("Connection established");
		} catch (SerialPortException e) {
			throw new ConnectionException(e.getMessage());
		}
	}

	private boolean openSerialPort(String portName) throws SerialPortException {
		Boolean foundPort = false;

		if (serialPortOpen != false) {
			throw new SerialPortException("SerialPort already open");
		}

		LOG.info("Opening Serial Port " + portName);
		enumComm = CommPortIdentifier.getPortIdentifiers();

		while (enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}

		if (foundPort != true) {
			throw new SerialPortException("SerialPort not found");
		}

		try {
			serialPort = (SerialPort) serialPortId.open("Öffnen und Senden",
					500);
		} catch (PortInUseException e) {
			throw new SerialPortException("Port already used");
		}
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			throw new SerialPortException("Cannot access OutputStream");
		}
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			throw new SerialPortException("Cannot access InputStream");
		}

		//serialPort.notifyOnDataAvailable(true);
		
		
		try {
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.enableReceiveThreshold(serialThreshold);
			LOG.info("Setting timeout to " + serialTimeout);
			serialPort.enableReceiveTimeout(serialTimeout);

		} catch (UnsupportedCommOperationException e) {
			throw new SerialPortException("Couldn't set interface parameters");
		}
		
		if(checkPort()) {
			serialPortOpen = true;
		} else {
			serialPort.close();
			throw new SerialPortException("Couldn't open serial port");
		}
		
		return true;
		

	}

	public void disconnect() throws ConnectionException {
		this.closeSerialPort();
	}
	
	private boolean checkPort() {

		System.out.println("Checking port");
		try {
			this.sendData("AT");
			if (this.getData().equals("OK")) return true;;
		} catch (DataException e) {
			return false;
		}

		return false;
	}

	private void closeSerialPort() {
		if (serialPortOpen == true) {
			LOG.debug("Closing Serialport");
			serialPort.close();
			serialPortOpen = false;
		} else {
			LOG.debug("Serialport already closed");
		}
	}


	public LinkedList<String> refreshPortList() throws AlreadyOpenException {
		LOG.debug("refreshing serial port list");

		if (serialPortOpen != false) {
			LOG.debug("SerialPort is open");
			throw new AlreadyOpenException("SerialPort open");
		}

		availablePorts = new LinkedList<String>();
		LOG.debug("Getting Port identifiers");
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			LOG.debug("Checking serialPort " + serialPortId.getName());
			if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				LOG.debug("Found:" + serialPortId.getName());
				availablePorts.add(serialPortId.getName());
			}
		}

		return availablePorts;
	}

	private void sendData(String message) throws DataException {
		message = message + "\r\n";

		try {
			this.clearStream();
			this.outputStream.write(message.getBytes());
			Thread.sleep(50);
		} catch (IOException e) {
			throw new DataException("Couldn't send data: " + e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	private void sendRawData(int message) throws DataException {

		try {
			this.clearStream();
			this.outputStream.write(message);
			Thread.sleep(50);
		} catch (IOException e) {
			throw new DataException("Couldn't send data: " + e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private String getData() throws DataException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String output = "";
		
		try {
			reader.readLine();
			output = reader.readLine();
			reader.close();
		} catch (IOException e) {
			throw new DataException("Couldn't receive data: " + e.getMessage());
		}

		LOG.debug("Recieved: " + output);
		this.clearStream();

		return output;
	}
	
	private void clearStream() {

		try {
			Thread.sleep(sleeptime);
			int skip = inputStream.available();
			inputStream.skip(skip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private boolean checkResponse(String assertedAnswer) throws DataException {
		
		String returnValue = this.getData();
		LOG.debug("Return value: "+ returnValue);
		
		if(returnValue.equals(assertedAnswer)) {
			return true;
		}
		
		return false;
		
	}
	
	public void sendSMS(String number, String message) {
		
		String initString = "AT+CMGS=\"" + number + "\"";
		
		try {
			this.sendData("AT");
			LOG.debug("sent AT");
			checkResponse("OK");
			this.sendData("AT+CMGF=1");
			LOG.debug("sent AT+CMGF=1");
			checkResponse("OK");
			this.sendData(initString);
			LOG.debug("sent " + initString);
			this.sendData(message);
			LOG.debug("sent " + message);
			this.sendRawData(ASCII_STRG_Z);
			LOG.debug("sent 26 ASCII");

			Thread.sleep(5000);
			checkResponse("OK");
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendSmsPDU(String number, String message) {
		
		PDUencode pdu = new PDUencode();
		String sms = pdu.MakePDUTextGSM(number, message);
		int length = sms.length()-1;
		
		String initString = "AT+CMGS=" + length;
		
		
		try {
			this.sendData("AT");
			LOG.debug("sent AT");
			checkResponse("OK");
			this.sendData("AT+CMGF=0");
			LOG.debug("sent AT+CMGF=0");
			checkResponse("OK");
			this.sendData(initString);
			LOG.debug("sent " + initString);
			this.sendData(sms);
			LOG.debug("sent " + sms);
			this.sendRawData(ASCII_STRG_Z);
			LOG.debug("sent 26 ASCII");

			Thread.sleep(5000);
			checkResponse("OK");
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}
