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

	private boolean loggedIn = false;

	private CommPortIdentifier serialPortId;
	private Enumeration enumComm;
	private SerialPort serialPort;
	private OutputStream outputStream;
	private InputStream inputStream;
	private Boolean serialPortOpen = false;
	
	private String portName;

	private int baudrate = 460800;
	private int dataBits = SerialPort.DATABITS_8;
	private int stopBits = SerialPort.STOPBITS_1;
	private int parity = SerialPort.PARITY_NONE;
	
	private int serialTimeout = 5000;
	private int serialThreshold = 1;
	
	private int sleeptime = 30;

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
		} catch (SerialPortException e) {
			throw new ConnectionException(e.getMessage());
		}
	}

	private void openSerialPort(String portName) throws SerialPortException {
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
		

	}


	public void disconnect() throws ConnectionException {
		this.closeSerialPort();
	}
	
	private boolean checkPort() {

		try {
			this.sendData("");
			this.getData();
		} catch (DataException e) {
			return false;
		}

		return true;
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
		message = message + "\n";

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

	private String getData() throws DataException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
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

	private String getValuefromWebDL(String value) throws DataException {


		String command = "cat /etc/det.cfg | grep " + value;
		String data = "";
		try {

			this.sendData(command);
			data = this.getData();
			int pos = data.indexOf("=");
			data = data.substring(pos + 1);

		} catch (DataException de ) {
			throw new DataException("COM getValue: " + value + " : " + de.getMessage());
		}

		return data;

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








}
