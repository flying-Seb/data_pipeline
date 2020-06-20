package pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

import helpers.JSONIOHelper;

public class B1TextLoader {

	ConcurrentHashMap<String, String> documents = new ConcurrentHashMap();

	public static void main(String[] args) {
		// main method for program logic

		// create new instance of this class
		B1TextLoader loader = new B1TextLoader();

		// load the given Text File which is stored in the same workspace
		loader.loadTextFile("BasicTextFile.txt");

		// save the text file to a JSON file
		loader.SaveDocumentsToJSON("JSONDataStore.json");
	}

	public void loadTextFile(String filePath) {

		// create a try/catch block for errors
		try {
			// load the file passed as an argument
			System.out.println("Loading file...");

			// create new File object
			File f = new File(filePath);

			// create new BufferReader object
			BufferedReader br = new BufferedReader(new FileReader(f));

			// use the BufferedReader and readLine() to read every line of the file
			// and put it into the hashMap with a counter
			System.out.println("Processing file...");

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

		// count the array and print the number of words
		System.out.println(key + " has " + words.length + " words!");

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
