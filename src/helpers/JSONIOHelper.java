package helpers;

import java.io.FileWriter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;

public class JSONIOHelper {
	
	// create class variables for two JSON Objects
	JSONObject rootObject;
	JSONObject documentsObject;
	
	// create three methods to structure, update and save JSON data
	public void CreateBasicJSONStructure() {
		// create new instances of the Objects (Step 1)
		rootObject = new JSONObject();
		documentsObject = new JSONObject();
		
		rootObject.put("documents", documentsObject);
	}
	
	public void AddDocumentsToJSONStructure(ConcurrentHashMap<String, String> documents) {
		//adding file data (from B1TextLoader) to JSON data structure (Step 2)
		
		// creating a for each loop to iterate over 
		for (Entry<String, String> entry : documents.entrySet()) {
			documentsObject.put(entry.getKey(), entry.getValue());
		}
		
	}
	
	public void SaveJSON(String filename) {
		// saving the data into a .json file
		
		// creating a String out of the rootObject
		String JSONString = rootObject.toJSONString();
		
		// creating a new instance of the FileWriter class
		try(FileWriter writer = new FileWriter(filename)){
			// writing the String to a file
			writer.write(JSONString);
			// print completion message to the user
			System.out.println("Saving JSON to file successfully completed.");
		}
		catch(Exception e) {
			System.out.println("Saving JSON to file failed...");
		};
	}
	
}
