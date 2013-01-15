package at.ac.tuwien.mnsa.sms.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReaderWriter {


	public List<SMS> readCSV() throws FileNotFoundException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader("SmsDispatchList.csv"));

		List<SMS> smsList = new ArrayList<SMS>();

		String line = null;
		try {
			while((line=bufferedReader.readLine()) != null){
				SMS sms = parseLine(line);
				smsList.add(sms);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bufferedReader.close();
			} catch (IOException e) {
			}
		}

		return smsList;
	}

	public void writeCSV(List<SMS> smsList) throws IOException {

		FileWriter fileWriter = new FileWriter("SmsReceivedList.csv");

		try {

			for (SMS sms : smsList) {
				StringBuilder outputLine = new StringBuilder();
				outputLine.append(sms.getNumber() + ",");
				outputLine.append(sms.getText());
				outputLine.append("\n");

				try {
					fileWriter.append(outputLine.toString());
					fileWriter.flush();
				} catch (IOException e) {
					fileWriter.close();
					throw new RuntimeException("Could not append to fileWriter", e);
				}
			}


			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not close fileWriter", e);
		}

	}

	private SMS parseLine(String line) {
		String[] list = line.split(",");


		return new SMS(list[0], list[1]);
	}

}
