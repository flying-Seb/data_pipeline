package pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import cc.mallet.util.*;
import helpers.JSONIOHelper;

public class B4TopicModelling {

	// create two objects of the classes needed in this class

	static B4TopicModelling loader = new B4TopicModelling();
	static JSONIOHelper helper = new JSONIOHelper();

	public static void main(String[] args) {

		// start the modelling process

		loader.StartTopicModellingProcess("JSONDataStore.json");

	}

	private void StartTopicModellingProcess(String filePath) {
		// load the needed data from the JSON file and store it in a HashMap

		helper.LoadJSON(filePath);

		ConcurrentHashMap<String, String> lemmas = helper.GetLemmasFromJSONStructure();

		// call the method to save lemmas to a file
		loader.SaveLemmaDataToFile("topicdata.txt", lemmas);

		// run the topic modelling
		loader.RunTopicModelling("topicdata.txt", 10, 3, 500);
	}

	private void SaveLemmaDataToFile(String TMFlatFile, ConcurrentHashMap<String, String> lemmas) {
		// method to transform the JSON data into the format MALLET expects

		// save every line in lemmas to the text file
		for (Entry<String, String> entry : lemmas.entrySet()) {
			try (FileWriter writer = new FileWriter(TMFlatFile)) {
				// write the format MALLET expects
				writer.write(entry.getKey() + "\ten\t" + entry.getValue() + "\r\n");
			} catch (Exception e) {
				System.out.println("Writing lines to txt-file failed...");
				System.out.println("\n");
			}
		}

	}

	private void RunTopicModelling(String TMFlatFile, int numTopics, int numThreads, int numIterations) {
		// method to run the topic modelling

		// build aPipe array list
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: tokenise, map to features
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequence2FeatureSequence());

		// add a new instance list which takes the pipeList as an argument
		InstanceList instances = new InstanceList(new SerialPipes());

		InputStreamReader fileReader = null;
		
		// create a File and a FileInputStream Object
		File file = new File(TMFlatFile);
		FileInputStream stream = new FileInputStream(TMFlatFile);
		

		// try-catch-block for opening a FileReader and load the data from the flat text
		// file
		try (FileReader reader = new FileReader(TMFlatFile)) {
			// load the data
			reader.read();

		} catch (Exception e) {
			System.out.println("An error has occured.");
			e.printStackTrace();
			
			// stop the program
			System.exit(1);
		}
		
		// link the data into the processing pipeline
		instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1));
		
		// create a model that runs the topic modelling
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
		
		// add the variables to the model
		model.addInstances(instances);
		model.setNumThreads(numThreads);
		model.setNumIterations(numIterations);
		
		// start the process within a try-catch block
		try {
			model.estimate();
		} catch (Exception e) {
			System.out.println("An error occured.");
		}
	}

}
