package pipeline;

import java.util.List;
import java.util.Map.Entry;
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
		
		ConcurrentHashMap<String,String> lemmatised = JSONIO.GetLemmasFromJSONStructure();
		
		// call the lemmatise method for every row of text in the documents object
		for (Entry<String, String> entry : documents.entrySet()) {
			lemmatised.put(entry.getKey(), LemmatiseSingleDoc(entry.getValue()));
		}
		
		JSONIO.AddLemmasToJSONStructure(lemmatised);
		JSONIO.SaveJSON("doc_and_lemm.json");

	}
	
	private String LemmatiseSingleDoc(String text) {
		// a method to lemmatise a single document which is passed as an argument
		
		// use regex to clean the data (remove most punctuation and replace several whitespace with a single one
		text = text.replaceAll("\\p{Punct}", " ");
		text = text.replaceAll("\\s+", " ");
		
		// remove all leading and trailing whitespace and set all to lowercase
		text = text.replaceAll("^[ \\t]+|[ \\t]+$", "");
		text = text.toLowerCase();
		
		// use the stanford nlp library to lemmatise the text
		//first: create a sentence object and pass the text to it
		Sentence sent = new Sentence(text);
		// create a list of strings and call the method lemmas on it
		List<String> lemmas = sent.lemmas();
		// finally convert the List<String> back to a simple string and pass it to text with a whitespace as a delimiter
		text = String.join(" ", lemmas);
		
		System.out.println(text);
		
		return text;
	}

}
