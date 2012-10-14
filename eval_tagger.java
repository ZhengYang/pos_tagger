import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;

class eval_tagger {
	public static void main(String args[]) {
        // carry out the cross validation
        double totalAcc = 0;
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
                FileReader outReader = new FileReader(outFile);
                BufferedReader outBr = new BufferedReader(outReader);

                FileReader ansReader = new FileReader(ansFile);
                BufferedReader ansBr = new BufferedReader(ansReader);
                
                String outLine;
                String ansLine;
                int correctCount = 0;
                int totalCount = 0;
                while ((outLine = outBr.readLine()) != null) {
                    ansLine = ansBr.readLine();
    	            String[] outTokens = outLine.trim().split("\\s+");
    	            String[] ansTokens = ansLine.trim().split("\\s+");
                    for (int k = 0; k < outTokens.length; k++) {
                        totalCount += 1;
                        if (outTokens[k].equals(ansTokens[k])) {
                            correctCount += 1;
                        }
                    }
                }
                double acc = correctCount / (double) totalCount;
                totalAcc += acc;
                System.out.println("bin-" + i + " Acc = " + String.format("%.2f", acc * 100) + "%");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Average Acc = " + String.format("%.2f", totalAcc / 10 * 100) + "%");
	}
}