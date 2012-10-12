import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;

class run_tagger {
	public static void main(String args[]) {
	    if (args.length != 3) {
	        System.out.println("error: Wrong number of arguments.");
	        System.out.println("usage: java run_tagger <sents.test> <model_file> <sents.out>");
	        System.exit(1);
        }
        // take in params
        String testFile = args[0];
        String modelFile = args[1];
        String outFile = args[2];
        
        // read input
        try {
            FileWriter fostream = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fostream);

            FileInputStream fstream = new FileInputStream(testFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String currLine;
            while ((currLine = br.readLine()) != null) {
                // break each line into an array of tokens
                String[] tokens = currLine.trim().split("\\s+");
                ArrayList<String> tags = Vite(tokens);
                
                // write to out file
                String outputLine = "";
                for (int i = 0; i < tokens.length; i++) {
                    line += tokens[i] + "/" + tags.get(i);
                }
                out.write(outputLine + "\n");
            }
            in.close();
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
	}
	
	public static ArrayList<String> Vite(String[] words) {
	    ArrayList<String> tags = new ArrayList<String>();
	    return tags;
	}
}
