package pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import com.opencsv.CSVParser;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import cc.mallet.util.*;
import cc.mallet.topics.*;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
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
		loader.RunTopicModelling("topicdata.txt", 10, 3, 2000);
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

		// build a Pipe array list
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: tokenise, map to features
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequence2FeatureSequence());

		// add a new instance list which takes the pipeList as an argument
		InstanceList instances = new InstanceList(new SerialPipes(pipeList));
		
		// create a File and a InputStreamReader Object
		File file = new File(TMFlatFile);
		InputStreamReader fileReader = null;

		try {
			// create a FileReader with the file as an argument and pass it to the InputStreamReader
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.out.println("The file "+TMFlatFile+" has not been found.");
			System.out.println(e);
		}

		// link the data into the processing InstanceList
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
		
		// get a CSV file output from the topic modelling
		String CSVOutput = "";
		// The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        Formatter out = new Formatter(new StringBuilder(), Locale.UK);      
        
        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        
        // Show top 10 words in topics with proportions for the first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            
            out = new Formatter(new StringBuilder(), Locale.UK);
            out.format("%d, ", topic);
            int rank = 0;
            // append every word to the string until the 10th word is reached
            while (iterator.hasNext() && rank < 10) {
                IDSorter idCountPair = iterator.next();
                out.format("%s ", dataAlphabet.lookupObject(idCountPair.getID()));
                rank++;
            }
            //System.out.println(out);
            
            // append 'out' for every topic to CSVOutput with a line separator 
            CSVOutput = CSVOutput + out + System.lineSeparator();
            
        }
        
        // try-catch block to write the string to a file
		try (FileWriter writer = new FileWriter("TopTenTopicData.csv")) {
			// writing the CSVOutput to a CSV file
			writer.write(CSVOutput);
			System.out.println("Saving topics to CSV-file succeeded.");
			System.out.println("\n");
		} catch (Exception e) {
			System.out.println("Saving topics to CSV-file failed...");
			System.out.println("\n");
		}
		
	}

}
