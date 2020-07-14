package helpers;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONIOHelper {

	// create class variables for three JSON Objects
	JSONObject rootObject;
	JSONObject documentsObject;
	JSONObject lemmasObject;

	// create three methods to structure, update and save JSON data
	public void CreateBasicJSONStructure() {
		// create new instances of the Objects (Step 1)
		rootObject = new JSONObject();
		documentsObject = new JSONObject();
		// create new object (lemmasObject) and add it to the rootObject (2nd)
		lemmasObject = new JSONObject();

		// add both "child" objects to the root object
		rootObject.put("documents", documentsObject);
		rootObject.put("lemmas", lemmasObject);

	}

	public void AddDocumentsToJSONStructure(ConcurrentHashMap<String, String> documents) {
		// adding file data (from B1TextLoader) to JSON data structure (Step 2)

		// creating a for-each loop to iterate over the documents concurrent hash map
		for (Entry<String, String> entry : documents.entrySet()) {
			documentsObject.put(entry.getKey(), entry.getValue());
		}

	}
	
	public void AddLemmasToJSONStructure(ConcurrentHashMap<String, String> lemmas) {
		// a method to convert the lemmatised text into JSONStructure
		for (Entry<String, String> entry : lemmas.entrySet()) {
			lemmasObject.put(entry.getKey(), entry.getValue());
		}
	}

	public void SaveJSON(String filename) {
		// saving the data into a .json file

		// creating a String out of the rootObject
		String JSONString = rootObject.toJSONString();

		// creating a new instance of the FileWriter class
		try (FileWriter writer = new FileWriter(filename)) {
			// writing the String to a file
			writer.write(JSONString);
			// print completion message to the user
			System.out.println("Saving JSON to file successfully completed.");
			System.out.println("\n");
		} catch (Exception e) {
			System.out.println("Saving JSON to file failed...");
			System.out.println("\n");
		}
		;
	}

	public void LoadJSON(String filename) {
		// A method to load the JSON data from the .json file

		// call the method to create the basic JSON structure
		CreateBasicJSONStructure();

		// try-catch-block for opening a FileReader and load the data
		// because of this design "file.close()" is not necessary
		try (FileReader file = new FileReader(filename)) {
			// load the data
			JSONParser parser = new JSONParser();

			rootObject = (JSONObject) parser.parse(file);

			if (rootObject.get("documents") != null) {
				documentsObject = (JSONObject) rootObject.get("documents");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("JSON Loading complete. Lines: " + documentsObject.size());
		}
		
		// another try-catch block for the lemmatised text
		try (FileReader file = new FileReader(filename)){
			JSONParser parser = new JSONParser();
			
			rootObject = (JSONObject) parser.parse(file);
			
			if(rootObject.get("lemmas") != null) {
				lemmasObject = (JSONObject) rootObject.get("lemmas");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("JSON Loading complete. Lines: " + lemmasObject.size());
		}

	}

	public ConcurrentHashMap<String, String> GetDocumentsFromJSONStructure() {
		// a method to get the documents from the JSON structure
		
		// create a concurrent hash map object
		ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<String, String>();

		// iterate over the hash map and fill it with key of documents and text (casted to Strings)
		for (String key : (Iterable<String>) documentsObject.keySet()) {
			documents.put(key, (String) documentsObject.get(key));
		}

		return documents;
	}
	
	public ConcurrentHashMap<String, String> GetLemmasFromJSONStructure(){
		// a method to get the lemmatised text from the JSON structure
		
		// create a concurrent has map object
		ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<String, String>();
		
		// iterate over the hash map and fill it with key of lemmas and text (casted to Strings)
		for (String key : (Iterable<String>) lemmasObject.keySet()) {
			lemmas.put(key, (String) lemmasObject.get(key));
		}
		return lemmas;
	}

}
