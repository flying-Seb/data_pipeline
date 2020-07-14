package pipeline;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import helpers.JSONIOHelper;



public class B3DescriptiveStatistics {
	
	static JSONIOHelper loader = new JSONIOHelper();
	
	public static void main(String[] args) {
		
		StartCreatingStatistics("JSONDataStore.json");

	}
	
	public static void StartCreatingStatistics(String filePath) {
		System.out.println("Loading file...!");
		
		loader.LoadJSON(filePath);
		
		ConcurrentHashMap<String, String> lemmas = loader.GetLemmasFromJSONStructure();;
		
		// loader.LoadJSON(filePath);
		// loader.GetLemmasFromJSONStructure();
		
		for (Entry<String, String> entry : lemmas.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		
		
		System.out.println("Loaded succesfully!");
		
		
	}

}
