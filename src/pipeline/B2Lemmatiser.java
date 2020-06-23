package pipeline;

import java.util.concurrent.ConcurrentHashMap;

import edu.stanford.nlp.simple.*;
import helpers.JSONIOHelper;


public class B2Lemmatiser {

	public static void main(String[] args) {
		
		B2Lemmatiser loader = new B2Lemmatiser();
		
		loader.StartLemmatisation("JSONDataStore.json");
		
	}
	
	private void StartLemmatisation(String filePath) {
		System.out.println("Loading JSON file...");
		
		// create a JSONIOHelper Object and call the LoadJSON method
		JSONIOHelper JSONIO = new JSONIOHelper();
		JSONIO.LoadJSON("JSONDataStore.json");
		
		// call the concurrent hash map method to put the JSON into the outgoing data structure
		ConcurrentHashMap<String,String> documents = JSONIO.GetDocumentsFromJSONStructure();

	}

}
