package pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import helpers.JSONIOHelper;

public class B1TextLoader {

	ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<String, String>();

	private static String user_in;

	public static void main(String[] args) {
		// main method for program logic

		// create new instance of this class
		B1TextLoader loader = new B1TextLoader();

		// create while loop to ask the user for the correct output name of the file
		boolean user_act = true;

		while (user_act == true) {

			// create variable for console input to ask which file to load and lemmatise
			// from the user
			// new Scanner object for System.in input
			Scanner scanner = new Scanner(System.in); // will be closed in the B2Lemmatiser class
			System.out.println("Please enter the name of the file you want to read: ");

			// read the next line and save it to the variable user_in
			user_in = scanner.nextLine();

			// double check with the user before loading the file
			System.out.println("The file you entered is: " + user_in);
			System.out.println("If that is correct please type [c]ontinue or [n]ew.");

			// read the double check as a new variable for the if-statement
			String user_ans = scanner.nextLine();
			user_ans = user_ans.toLowerCase();

			// check if the user entered the file name correct or spotted a typo or other
			// mistake
			// something and wants to re-enter it
			if (user_ans.startsWith("c")) {
				// load the given Text File which is stored in the same workspace
				loader.loadTextFile(user_in);
				// save the text file to a JSON file
				loader.SaveDocumentsToJSON("JSONDataStore.json");
				user_act = false;
				break;
			} else if (user_ans.startsWith("n")) {
				System.out.println("Please enter a new file to load.");
			} else {
				System.out.println("Something went wrong.");
			}
		}

		// call the B2Lemmatiser to have a consistent process and let the user just
		// start one program/class
		B2Lemmatiser.main(args);

	}

	public void loadTextFile(String filePath) {

		// create a try/catch block for errors
		try {
			// load the file passed as an argument
			System.out.println("Loading text file...");

			// create new File object
			File f = new File(filePath);

			// create new BufferReader object
			BufferedReader br = new BufferedReader(new FileReader(f));

			// use the BufferedReader and readLine() to read every line of the file
			// and put it into the hashMap with a counter
			String line = br.readLine();
			int counter = 0;
			while (line != null) {
				if (line.trim().length() > 0) {
					documents.put("doc" + counter, line);
					counter++;
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("File loading failed.");
		} finally {
			System.out.println("\n");
			System.out.println("Loading complete. Lines loaded: " + documents.size());
			System.out.println("\n");
		}
		CountWordsInDocuments(documents);
	}

	public void CountWordsInDocuments(ConcurrentHashMap<String, String> documents) {
		// create a for-each loop to iterate over all documents in the file
		documents.forEach(this::CountWordsInDocument);
	}

	public void CountWordsInDocument(String key, String value) {
		// create an array of Strings which contains all words of a document
		String[] words = value.split(" ");

		/*
		 * I commented that out for the assessment because it could confuse the user
		 * while loading very large files and fill the screen unnecessary
		 */

		// count the array and print the number of words
		// System.out.println(key + " has " + words.length + " words!");

	}

	public void SaveDocumentsToJSON(String filename) {
		// method to load a text file and save it into a JSON file by using the
		// JSONIOHelper class

		// create an instance of the helper class and call all three methods
		JSONIOHelper JSONIO = new JSONIOHelper();

		JSONIO.CreateBasicJSONStructure();
		JSONIO.AddDocumentsToJSONStructure(documents);
		JSONIO.SaveJSON(filename);

	}
}
