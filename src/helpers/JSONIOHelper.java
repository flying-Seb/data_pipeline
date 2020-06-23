package helpers;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
	
	public void LoadJSON(String filename) {
		// A method to load the JSON data from the .json file
		
		// call the method to create the basic JSON structure
		CreateBasicJSONStructure();
		
		// try-catch-block for opening a FileReader and load the data
		// because of this design "file.close()" is not necessary
		try(FileReader file = new FileReader(filename)){
			// load the data
			JSONParser parser = new JSONParser();
			
			rootObject = (JSONObject)parser.parse(file);
			
			if(rootObject.get("documents") != null) {
				documentsObject = (JSONObject)rootObject.get("documents");
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("JSON Loading complete. Lines: " + documentsObject.size());
		}
		
		
	}
	
	public ConcurrentHashMap<String,String> GetDocumentsFromJSONStructure(){
		// create a concurrent hash map
		ConcurrentHashMap<String,String> documents = new ConcurrentHashMap<String,String>();
		
		// iterate over the hash map and put every key into documents key
		for(String key : (Iterable<String>)documentsObject.keySet()) {
			documents.put(key, (String)documentsObject.get(key));
		}
		
		return documents;
	}
	
}
