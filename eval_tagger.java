import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
                FileWriter ansStream = new FileWriter("bin-" + i + ".ans");
                BufferedWriter ansWriter = new BufferedWriter(ansStream);
                FileWriter trainStream = new FileWriter("bin-" + i + ".train");
                BufferedWriter trainWriter = new BufferedWriter(trainStream);
                
                for (int j = 0; j < 10; j ++) {
                    if (j == i) {
                        for (String line : bins.get(j))
                        {
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
                                testWriter.write(word);
                            }
                            testWriter.write("\n");
                            // output correct answers dataset
                            ansWriter.write(line + "\n");
                        }
                    }
                    // output train dataset
                    else {
                        for (String line : bins.get(j))
                        {
                            trainWriter.write(line + "\n");
                        }
                    }
                }
                
                testWriter.close();
                ansWriter.close();
                trainWriter.close();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        
        
        // carry out the cross validation
        double avAcc = 0;
        for (int i = 0; i < 10; i ++) {
            String trainFile = "bin-" + i + ".train";
            String devtFile = "sents.devt";
            String modelFile = "bin-" + i + ".model";
            String testFile = "bin-" + i + ".test";
            String outFile = "bin-" + i + ".out";
            String ansFile = "bin-" + i + ".ans";
            
            String[] trainCommand = {"java", "build_tagger", trainFile, devtFile, modelFile};
            String[] testCommand = {"java", "run_tagger", testFile, modelFile, outFile};
            try {
                Process procTrain = Runtime.getRuntime().exec(trainCommand);
                Process procTest = Runtime.getRuntime().exec(testCommand);
                /*
                InputStream pStdOut = proc.getInputStream();
                Scanner sc = new Scanner(pStdOut);
                while(sc.hasNext())
                    System.out.println( sc.nextLine() );
                */
                
                // evaluate output against answers
                FileInputStream oStream = new FileInputStream(outFile);
                DataInputStream o = new DataInputStream(oStream);
                BufferedReader obr = new BufferedReader(new InputStreamReader(o));

                FileInputStream aStream = new FileInputStream(ansFile);
                DataInputStream a = new DataInputStream(aStream);
                BufferedReader abr = new BufferedReader(new InputStreamReader(a));
                String oline;
                String aline;
                int correctCount = 0;
                int totalCount = 0;
                while ((oline = obr.readLine()) != null) {
                    aline = abr.readLine();
    	            String[] otokens = oline.trim().split("\\s+");
    	            String[] atokens = aline.trim().split("\\s+");
                    for (int k = 0; k < otokens.length; k++) {
                        totalCount += 1;
                        if (otokens[k].equals(atokens)) {
                            correctCount += 1;
                        }
                    }
                }
                double acc = correctCount / (double) totalCount * 100;
                avAcc += acc;
                System.out.println("bin-" + i + " Acc = " + String.format("%.2f", acc) + "%");
                
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Average Acc = " + String.format("%.2f", avAcc) + "%");
	}
}