package pipeline;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import helpers.JSONIOHelper;

public class B3DescriptiveStatistics {

	static JSONIOHelper helper = new JSONIOHelper();
	static B3DescriptiveStatistics loader = new B3DescriptiveStatistics();

	public static void main(String[] args) {

		loader.StartCreatingStatistics(B2Lemmatiser.getUser_out());

		// call the next class in the process for a smooth workflow
		B4TopicModelling.main(args);

	}

	public void StartCreatingStatistics(String filePath) {
		System.out.println("Loading file...!");

		// load the file with the helper class
		helper.LoadJSON(filePath);

		// put it into a concurrent hash-map and fill it with the lemmas from the file
		ConcurrentHashMap<String, String> lemmas = helper.GetLemmasFromJSONStructure();

		System.out.println("Loaded succesfully!");

		loader.CountWordsInCorpus(lemmas);

		loader.CountWordsInDocument(lemmas);

	}

	private void CountWordsInCorpus(ConcurrentHashMap<String, String> lemmas) {
		// This is a method to count the occurrence of words in the lemmatised text

		// 1. create an arrayList called corpus
		ArrayList<String> corpus = new ArrayList<String>();

		// 2. create a ConcurrentHashMap called counts
		ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<String, Integer>();

		// 3. for each entry in lemmas
		for (Entry<String, String> entry : lemmas.entrySet()) {
			// I. get the string in value and split it at the whitespace
			String[] value = entry.getValue().split(" ");

			// transform the String Array to a list of Strings
			List<String> bridge = Arrays.asList(value);

			// II. add it to the arrayList corpus
			for (String text : bridge) {
				corpus.add(text);
			}

		}

		// 4. for each word in corpus
		for (String item : corpus) {
			// I. if word in counts -> add 1 to the value of word
			if (counts.containsKey(item) == true) {
				// get the value of the specific item and increment the value by 1
				Integer count = counts.get(item) + 1;
				// put the key, value pair back into counts
				counts.put(item, count);
			}
			// II. if word not in counts -> add a value with 1
			else if (counts.containsKey(item) == false) {
				counts.put(item, 1);
			}
		}

		// call the method to output the counts
		loader.OutpuCountAsCSV(counts, "CorpusWordCounts.csv");

	}

	private void CountWordsInDocument(ConcurrentHashMap<String, String> lemmas) {
		// a method to count the words in each document

		// create a new concurrent HashMap called counts
		ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<String, Integer>();

		// for each document in lemmas
		for (Entry<String, String> entry : lemmas.entrySet()) {
			// get the string in value and split it at the whitespace
			String[] value = entry.getValue().split(" ");

			// count the number of words
			Integer numOfWords = value.length;

			// and save that number into counts with the same key as in lemmas
			counts.put(entry.getKey(), numOfWords);

		}

		// call the method to output the counts
		loader.OutpuCountAsCSV(counts, "DocumentCounts.csv");

	}

	private void OutpuCountAsCSV(ConcurrentHashMap<String, Integer> counts, String fileName) {
		// method to output the counted words to a CSV file

		String CSVOutput = "";

		for (Entry<String, Integer> entry : counts.entrySet()) {
			// append a single line with key, value and new line to the result String
			CSVOutput = CSVOutput + (entry.getKey() + "," + entry.getValue() + System.lineSeparator());
		}

		// try-catch block to write the string to a file
		try (FileWriter writer = new FileWriter(fileName)) {
			// writing the String to a file
			writer.write(CSVOutput);
			System.out.println("Saving counts to CSV-file succeeded.");
			System.out.println("\n");
		} catch (Exception e) {
			System.out.println("Saving counts to CSV-file failed...");
			System.out.println("\n");
		}

	}

}
