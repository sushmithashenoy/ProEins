package ProEins.ProEins;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import com.google.gson.stream.JsonReader;

public class App {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("You have entered nothing !!\n Please enter a valid string on command line.");
			System.exit(0);
		}
		String location = getFilePathFromUser();
		populateCsv(connectToUrl(args[0]), createCsvFile(location));
		if (location.equals(null) || location.equals("")) // CSV file has been
															// created at the
															// default location
			System.out.println("Please find the file at E:\\LocationDetails.csv");
		else
			// CSV file has been created at the user defined location
			System.out.println("Please find the file at " + location);
	}

	/* The following method connects to the API endpoint and returns the URL */
	private static URL connectToUrl(String inputString) throws MalformedURLException, IOException {
		URL locationApiUrl = new URL("http://api.goeuro.com/api/v2/position/suggest/en/" + inputString);
		URLConnection myURLConnection = locationApiUrl.openConnection();
		myURLConnection.connect();
		return locationApiUrl;
	}

	/*
	 * The following method recieves the user input for the path and filename
	 * and returns it as a String Object
	 */
	private static String getFilePathFromUser() {
		System.out.println("Please enter a filename  in the following format \n E:\\LocationDetails.csv .\nPlease press Enter if you want to use the default i.e E:\\LocationDetails.csv");
		Scanner scanner = new Scanner(System.in);
		String location = scanner.nextLine();
		scanner.close();
		if (location.equals(""))
			return "E:\\LocationDetails.csv";
		return location;
	}

	/*
	 * The following method creates a CSV file at the user specified path .The
	 * user can also choose to stick to the default path by entering 1
	 */
	private static FileWriter createCsvFile(String location) throws IOException {
		FileWriter csvWriter;
		if (location.equals("")) {
			csvWriter = new FileWriter("E:\\LocationDetails.csv");
		} else {
			csvWriter = new FileWriter(location);
		}
		csvWriter.append("id,name,type,latitute,longitude,\n");
		return csvWriter;
	}

	/*
	 * The following method populates the CSV file with the elements from the
	 * JSON and closes the file
	 */
	private static void populateCsv(URL locationApiUrl, FileWriter csvWriter) throws IOException {
		JsonReader jsonReader = getJsonReader(locationApiUrl);
		extractFromJsonToCsv(csvWriter, jsonReader);
		jsonReader.endArray();
		jsonReader.close();
		csvWriter.flush();
		csvWriter.close();
	}

	/*
	 * The following method returns a JSON reader which points to the specified
	 * url stream
	 */
	private static JsonReader getJsonReader(URL locationApiUrl) throws IOException {
		InputStream is = locationApiUrl.openStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		JsonReader jsonReader = new JsonReader(rd);
		jsonReader.beginArray(); // parsing the JSON array
		if (!jsonReader.hasNext()) { // if the JSON array is empty ,then exit
										// the application
			System.out.println("Your input doesnot correspond to a location.\n Please enter a valid input string on the command line");
			System.exit(0);
		}
		return jsonReader;
	}

	/*
	 * The following method parses the JSON object array appending each JSON
	 * object in each row of the CSV file
	 */
	private static void extractFromJsonToCsv(FileWriter csvWriter, JsonReader jsonReader) throws IOException {
		while (jsonReader.hasNext()) {
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName().toString()) {
				case "_id":
					csvWriter.append(((Integer) jsonReader.nextInt()).toString());
					csvWriter.append(',');
					break;
				case "name":
					csvWriter.append(jsonReader.nextString());
					csvWriter.append(',');
					break;
				case "type":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						csvWriter.append(jsonReader.nextString().toString());
					csvWriter.append(',');
					break;
				case "key":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextString().toString();
					break;
				case "iata_airport_code":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextString().toString();
					break;
				case "fullName":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextString().toString();
					break;
				case "country":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextString().toString();
					break;
				case "geo_position":
					jsonReader.beginObject();
					jsonReader.nextName().toString();
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						csvWriter.append(jsonReader.nextString());
					csvWriter.append(',');
					jsonReader.nextName().toString();
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						csvWriter.append(jsonReader.nextString());
					csvWriter.append(',');
					jsonReader.endObject();
					break;
				case "locationId":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextInt();
					break;
				case "inEurope":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextBoolean();
					break;
				case "coreCountry":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextBoolean();
					break;
				case "countryCode":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextString().toString();
					break;
				case "distance":
					if (jsonReader.peek().toString().equals("NULL"))
						jsonReader.nextNull();
					else
						jsonReader.nextLong();
					break;
				}
			}
			csvWriter.append('\n');
			jsonReader.endObject();
		}
	}
}
