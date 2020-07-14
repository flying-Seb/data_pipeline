package pipeline;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import helpers.JSONIOHelper;
import pipeline.B2Lemmatiser;



public class B3DescriptiveStatistics {
	
	static JSONIOHelper helper = new JSONIOHelper();
	
	public static void main(String[] args) {
		
		StartCreatingStatistics("ExampleOutput.txt");

	}
	
	public static void StartCreatingStatistics(String filePath) {
		System.out.println("Loading file...!");
		
		// load the file with the helper class
		helper.LoadJSON(filePath);
		
		// put it into a concurrent hash-map and fill it with the lemmas from the file
		ConcurrentHashMap<String, String> lemmas = helper.GetLemmasFromJSONStructure();;
		
		// print every entry to double-check the loading
		for (Entry<String, String> entry : lemmas.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		
		
		System.out.println("Loaded succesfully!");
		
		
	}

}
