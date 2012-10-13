import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class setup_cv {
	public static void main(String args[]) {
	    // init arraylist that contains 10 bins
	    ArrayList<ArrayList<String>> bins = new ArrayList<ArrayList<String>>();
	    for (int i = 0; i < 10; i++) {
	        bins.add(new ArrayList<String>());
	    }
	     
	    Random randomGenerator = new Random();
	    try {
            FileReader trainReader = new FileReader("sents.train");
            BufferedReader trainBr = new BufferedReader(trainReader);
            String currLine;
            while ((currLine = trainBr.readLine()) != null) {
	            int binIndex = randomGenerator.nextInt(10);
	            bins.get(binIndex).add(currLine);
            }
            // output cross validation train/test/answer dataset
            for (int i = 0; i < 10; i++) {
                // use ith bin as test
                FileWriter testWriter = new FileWriter("bin-" + i + ".test");
                BufferedWriter testBw = new BufferedWriter(testWriter);
                FileWriter ansWriter = new FileWriter("bin-" + i + ".ans");
                BufferedWriter ansBw = new BufferedWriter(ansWriter);
                FileWriter trainWriter = new FileWriter("bin-" + i + ".train");
                BufferedWriter trainBw = new BufferedWriter(trainWriter);

                for (int j = 0; j < 10; j ++) {
                    if (j == i) {
                        for (String line : bins.get(j))
                        {
                            String testLine = "";
                            // output test dataset
                            // remove POS labels
                            // break each line into an array of "word/tage" tokens
                            String[] tokens = line.trim().split("\\s+");
                            for (int m = 0; m < tokens.length; m++) {
                                // for each "word/tag" token, break it by "/"
                                // the last entity will be the tag
                                // everything before the last "/" will be the word
                                String[] wordAndTag = tokens[m].split("/");
                                String word = wordAndTag[0];
                                String tag = wordAndTag[wordAndTag.length - 1];
                                for (int n = 0; n < wordAndTag.length; n++) {
                                    if (n != 0 && n < wordAndTag.length - 1) {
                                        word += "/" + wordAndTag[n];
                                    }
                                }
                                testLine += word + " ";
                            }
                            testBw.write(testLine.trim());
                            testBw.newLine();
                            // output correct answers dataset
                            ansBw.write(line);
                            ansBw.newLine();
                        }
                    }
                    // output train dataset
                    else {
                        for (String line : bins.get(j))
                        {
                            trainBw.write(line);
                            trainBw.newLine();
                        }
                    }
                }

                testBw.close();
                ansBw.close();
                trainBw.close();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
	}
}