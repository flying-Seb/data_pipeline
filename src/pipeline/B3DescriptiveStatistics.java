package pipeline;

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

		loader.StartCreatingStatistics("ExampleOutput.txt");

	}

	public void StartCreatingStatistics(String filePath) {
		System.out.println("Loading file...!");

		// load the file with the helper class
		helper.LoadJSON(filePath);

		// put it into a concurrent hash-map and fill it with the lemmas from the file
		ConcurrentHashMap<String, String> lemmas = helper.GetLemmasFromJSONStructure();
		;

		// print every entry to double-check the loading
		for (Entry<String, String> entry : lemmas.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		System.out.println("Loaded succesfully!");

		loader.CountWordsInCorpus(lemmas);

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

		// 5. third loop to print counts
		for(Entry<String, Integer> entry : counts.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

	}

}
