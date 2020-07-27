package pipeline;

import java.io.FileWriter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cc.mallet.util.*;
import helpers.JSONIOHelper;

public class B4TopicModelling {

	// create two objects of the classes needed in this class

	static B4TopicModelling loader = new B4TopicModelling();
	static JSONIOHelper helper = new JSONIOHelper();

	public static void main(String[] args) {

		// start the modelling process

		loader.StartTopicModellingProcess("JSONData.json");

	}

	private void StartTopicModellingProcess(String filePath) {
		// load the needed data from the JSON file and store it in a HashMap

		helper.LoadJSON(filePath);

		ConcurrentHashMap<String, String> lemmas = helper.GetLemmasFromJSONStructure();
		
		// call the method to save lemmas to a file
		loader.SaveLemmaDataToFile("topicdata.txt", lemmas);

	}
	
	private void SaveLemmaDataToFile(String TMFlatFile, ConcurrentHashMap<String, String> lemmas) {
		// method to transform the JSON data into the format MALLET expects
		
		// save every line in lemmas to the text file
		for (Entry<String, String> entry : lemmas.entrySet()) {
			try (FileWriter writer = new FileWriter(TMFlatFile)){
				// write the format MALLET expects
				writer.write(entry.getKey() + "\ten\t" + entry.getValue() + "\r\n");
			} catch (Exception e) {
				System.out.println("Writing lines to txt-file failed...");
				System.out.println("\n");
			}
		}
		
		
	}

}
