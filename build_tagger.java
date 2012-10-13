import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

class build_tagger {
	public static void main(String[] args) {
	    if (args.length != 3) {
	        System.out.println("error: Wrong number of arguments.");
	        System.out.println("usage: java build_tagger <sents.train> <sents.devt> <model_file>");
	        System.exit(1);
        }
        // take in params
        String trainFile = args[0];
        String devtFile = args[1];
        String modelFile = args[2];
        
        // init in-memory stats variables
        Set<String> tagSet = new HashSet<String>();
        Set<String> wordSet = new HashSet<String>();
        Map<String, Map<String, Integer>> TMatrix = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> EMatrix = new HashMap<String, Map<String, Integer>>();
        
        // train
        try {
            FileReader trainReader = new FileReader(trainFile);
            BufferedReader trainBr = new BufferedReader(trainReader);
            String currLine;
            while ((currLine = trainBr.readLine()) != null) {
                // break each line into an array of "word/tage" tokens
                String[] tokens = currLine.trim().split("\\s+");
                // keep track of the previous tag for transition matrix
                String prevTag = "<s>";
                for (int i = 0; i < tokens.length; i++) {
                    // for each "word/tag" token, break it by "/"
                    // the last entity will be the tag
                    // everything before the last "/" will be the word
                    String[] wordAndTag = tokens[i].split("/");
                    String word = wordAndTag[0];
                    String tag = wordAndTag[wordAndTag.length - 1];
                    for (int j = 0; j < wordAndTag.length; j++) {
                        if (j != 0 && j < wordAndTag.length - 1) {
                            word += "/" + wordAndTag[j];
                        }
                    }
                    // cumulate the sets where applicable
                    tagSet.add(tag);
                    wordSet.add(word);
                    
                    // cumulate emission count
                    if (EMatrix.containsKey(tag)) {
                        if (EMatrix.get(tag).containsKey(word)) {
                            EMatrix.get(tag).put( word, new Integer(EMatrix.get(tag).get(word).intValue() + 1) );
                        }
                        else {
                            EMatrix.get(tag).put( word, new Integer(1) );
                        }
                    }
                    else {
                        Map<String, Integer> hm = new HashMap<String, Integer>();
                        hm.put( word, new Integer(1) );
                        EMatrix.put( tag, hm );
                    }
                    
                    // cumulate transition count
                    if (TMatrix.containsKey(prevTag)) {
                        if (TMatrix.get(prevTag).containsKey(tag)) {
                            TMatrix.get(prevTag).put( tag, new Integer(TMatrix.get(prevTag).get(tag).intValue() + 1) );
                        }
                        else {
                            TMatrix.get(prevTag).put( tag, new Integer(1) );
                        }
                    }
                    else {
                        Map<String, Integer> hm = new HashMap<String, Integer>();
                        hm.put( tag, new Integer(1) );
                        TMatrix.put( prevTag, hm );
                    }
                    
                    prevTag = tag;
                    
                    // handle last token
                    if (i == tokens.length - 1) {
                        tag = "</s>";
                        if (TMatrix.containsKey(prevTag)) {
                            if (TMatrix.get(prevTag).containsKey(tag)) {
                                TMatrix.get(prevTag).put( tag, new Integer(TMatrix.get(prevTag).get(tag).intValue() + 1) );
                            }
                            else {
                                TMatrix.get(prevTag).put( tag, new Integer(1) );
                            }
                        }
                        else {
                            Map<String, Integer> hm = new HashMap<String, Integer>();
                            hm.put( tag, new Integer(1) );
                            TMatrix.put( prevTag, hm );
                        }
                        // reset previous tag
                        prevTag = "<s>";
                    }
                        
                }
            }
            trainBr.close();
            
            // output in-memory stats to <model_file>
            // arrange tag and word lists
            String[] tagArray = tagSet.toArray(new String[0]);
            String[] wordArray = wordSet.toArray(new String[0]);
            Arrays.sort(tagArray);
            Arrays.sort(wordArray);
            
            FileWriter modelWriter = new FileWriter(modelFile);
            BufferedWriter modelBw = new BufferedWriter(modelWriter);
            // 1st line of model file is tag list
            String tagLine = "";
            for (int i = 0; i < tagArray.length; i++)
            {
                tagLine += tagArray[i] + " ";
            }
            modelBw.write(tagLine.trim());
            modelBw.newLine();
            
            // 2nd line of model file is word list
            String wordLine = "";
            for (int i = 0; i < wordArray.length; i++)
            {
                wordLine += wordArray[i] + " ";
            }
            modelBw.write(wordLine);
            modelBw.newLine();
            
            /*
            for (Entry<String, Map<String, Integer>> entry : EMatrix.entrySet())
                        {
                            out.write(entry.getKey() + "/" + entry.getValue());
                        }
            */
            
            modelBw.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
	}
}
