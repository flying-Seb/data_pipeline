package pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import com.opencsv.CSVParser;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import cc.mallet.types.InstanceList;
import helpers.JSONIOHelper;

public class B4TopicModelling {

	// create two objects of the classes needed in this class

	static B4TopicModelling loader = new B4TopicModelling();
	static JSONIOHelper helper = new JSONIOHelper();

	public static void main(String[] args) {

		// start the modeling process

		loader.StartTopicModellingProcess("JSONDataStore.json");

	}

	private void StartTopicModellingProcess(String filePath) {
		// load the needed data from the JSON file and store it in a HashMap

		helper.LoadJSON(filePath);

		ConcurrentHashMap<String, String> lemmas = helper.GetLemmasFromJSONStructure();

		// call the method to save lemmas to a file
		loader.SaveLemmaDataToFile("topicdata.txt", lemmas);

		// run the topic modeling
		loader.RunTopicModelling("topicdata.txt", 10, 3, 500);
	}

	private void SaveLemmaDataToFile(String TMFlatFile, ConcurrentHashMap<String, String> lemmas) {
		// method to transform the JSON data into the format MALLET expects

		// save every line in lemmas to the text file
		// initiate and open the file once outside of the loop to be more efficient!
		FileWriter writer;
		try {
			writer = new FileWriter(TMFlatFile, true); // 'true' is to append text to the file
			// write the format MALLET expects
			for (Entry<String, String> entry : lemmas.entrySet()) {
				try {
					writer.write(entry.getKey() + "\ten\t" + entry.getValue() + "\r\n");
				} catch (IOException e) {
					System.out.println("Writing lines to txt-file failed...");
					System.out.println("\n");
				}
			}
		} catch (IOException e) {
			System.out.println("Writing lines to txt-file failed...");
			System.out.println("\n");
		}

	}

	private void RunTopicModelling(String TMFlatFile, int numTopics, int numThreads, int numIterations) {
		// method to run the topic modeling

		// build aPipe array list
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: tokenise, map to features
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequence2FeatureSequence());

		// add a new instance list which takes the pipeList as an argument
		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		InputStreamReader fileReader = null;
		CSVReader csvReader = null;

		// create a File and a FileInputStream Object
		File file = new File(TMFlatFile);

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			csvReader = new CSVReader(br);
			// FileInputStream stream = new FileInputStream(file);
			csvReader.readAll();
			// how to pass csvReader to fileReader??
			// fileReader.read();
			System.out.println(csvReader);
		} catch (Exception e) {
			System.out.println(e);
		}

		// link the data into the processing pipeline
		instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1));

		// create a model that runs the topic modeling in parallelization
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
