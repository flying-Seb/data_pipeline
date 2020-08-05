package pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.nlp.simple.*;
import helpers.JSONIOHelper;

public class B2Lemmatiser {

	// create new variable user_out for the name of the lemmatised file
	private static String user_out;

	public static String getUser_out() {
		return user_out;
	}

	public static void main(String[] args) {

		B2Lemmatiser loader = new B2Lemmatiser();

		// new Scanner instance to read the output file name from the user
		Scanner scanner = new Scanner(System.in);

		// create while loop to ask the user for the correct output name of the file
		boolean user_act = true;

		while (user_act == true) {

			// ask user for the name of the output file and double check it
			System.out.println("Please enter the desired name of the lemmatised output file: ");
			user_out = scanner.nextLine();
			System.out.println("You entered the file name: " + user_out);
			System.out.println("Is that correct? Please type [y]es or [n]o");
			String user_ans = scanner.nextLine();
			user_ans = user_ans.toLowerCase();
			
			
			// check if the user entered the file name correct
			if (user_ans.startsWith("y")) {
				loader.StartLemmatisation("JSONDataStore.json");
				// get the start time to measure performance of processing in parallel
				long startTime = System.nanoTime();
				user_act = false;
				scanner.close();
				// get the result time to measure performance of processing in parallel
				long endTime = System.nanoTime() - startTime;
				System.out.println("The lemmatisation took: " + endTime + " nano seconds.");
				// call the next class in the process for a smooth workflow
				B3DescriptiveStatistics.main(args);
				break;
			} else if (user_ans.startsWith("n")) {
				System.out.println("Please enter a new file name.");
			} else {
				System.out.println("Something went wrong.");
			}
			
		}
	}

	private void StartLemmatisation(String filePath) {
		System.out.println("Loading JSON file...");

		// create a JSONIOHelper Object and call the LoadJSON method
		JSONIOHelper JSONIO = new JSONIOHelper();
		JSONIO.LoadJSON("JSONDataStore.json");

		// call the concurrent hash map method to put the JSON into the outgoing data
		// structure
		ConcurrentHashMap<String, String> documents = JSONIO.GetDocumentsFromJSONStructure();

		ConcurrentHashMap<String, String> lemmatised = JSONIO.GetLemmasFromJSONStructure();

		// call the lemmatise method for every row of text in the documents object and
		// put it in the lemmatisedObject
		
		// map.forEach((k, v) -> method(k, v))
		
		// parallelize here!
		documents.forEach(1, (k, v) -> lemmatised.put(k, LemmatiseSingleDoc(v)));
		
		/*
		for (Entry<String, String> entry : documents.entrySet()) {
			lemmatised.put(entry.getKey(), LemmatiseSingleDoc(entry.getValue()));
		}
		*/

		JSONIO.AddLemmasToJSONStructure(lemmatised);
		JSONIO.SaveJSON("JSONDataStore.json");
		JSONIO.SaveJSON(user_out);
	}

	private String LemmatiseSingleDoc(String text) {
		// a method to lemmatise a single document which is passed as an argument

		// use regular expressions to clean the data (remove most punctuation and
		// replace several whitespace with a single one
		text = text.replaceAll("\\p{Punct}", " ");
		text = text.replaceAll("\\s+", " ");

		// remove all leading and trailing whitespace and set all to lower case
		text = text.replaceAll("^[ \\t]+|[ \\t]+$", "");
		text = text.toLowerCase();

		// use the Stanford NLP library to lemmatise the text

		// first: create a sentence object and pass the text to it
		Sentence sent = new Sentence(text);
		// create a list of strings and call the method lemmas on it
		List<String> lemmas = sent.lemmas();
		// finally convert the List<String> back to a simple string and pass it to text
		// with a whitespace as a delimiter
		text = String.join(" ", lemmas);

		// remove the stop-words from the text in every single document

		// 1. load the stop-words
		List<String> stopwords = null;
		try {
			stopwords = Files.readAllLines(Paths.get("stopwords.txt"));
		} catch (Exception e) {
			System.out.println("Please put 'stopwords.txt' in the correct location.");
			e.printStackTrace();
		}

		// 2. transform the original text from the document to an ArrayList<String>
		// and to use the method removeAll()
		ArrayList<String> allWords = Stream.of(text.toLowerCase().split(" "))
				.collect(Collectors.toCollection(ArrayList<String>::new));

		// 3. use the method removeAll() to remove all stop-words
		allWords.removeAll(stopwords);

		// 4. convert the ArrayList<String> back to a simple String which is returned
		// from the method
		text = String.join(" ", allWords);

		return text;
	}

}
