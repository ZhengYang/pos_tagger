import java.io.*;
import java.util.ArrayList;
import java.util.Random;

class eval_tagger {
	public static void main(String args[]) {
	    // init arraylist that contains 10 bins
	    ArrayList<ArrayList<String>> bins = new ArrayList<ArrayList<String>>();
	    for (int i = 0; i < 10; i++) {
	        bins.add(new ArrayList<String>());
	    }
	     
	    Random randomGenerator = new Random();
	    try {
            FileInputStream fstream = new FileInputStream("sents.train");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String currLine;
            while ((currLine = br.readLine()) != null) {
	            int binIndex = randomGenerator.nextInt(10);
	            bins.get(binIndex).add(currLine);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // output cross validation train/test dataset
        for (int i = 0; i < 10; i++) {
            // use ith bin as test
            try {
                FileWriter testStream = new FileWriter("bin-" + i + ".test");
                BufferedWriter testWriter = new BufferedWriter(testStream);
                FileWriter trainStream = new FileWriter("bin-" + i + ".train");
                BufferedWriter trainWriter = new BufferedWriter(trainStream);
                
                for (int j = 0; j < 10; j ++) {
                    // output test set
                    if (j == i) {
                        for (String line : bins.get(j))
                        {
                            testWriter.write(line + "\n");
                        }
                    }
                    // output train set
                    else {
                        for (String line : bins.get(j))
                        {
                            trainWriter.write(line + "\n");
                        }
                    }
                }
                
                testWriter.close();
                trainWriter.close();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        
	}
}