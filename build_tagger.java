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
import java.util.ArrayList;

class build_tagger {
    // quick ref: java build_tagger sents.train sents.devt model_file
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
        String[] unknownWordCatArray = {
            "TOTAL",
            "ONE-COUNT",
            "CAP",
            "able",
            "ance",
            "ence",
            "hood",
            "ible",
            "less",
            "like",
            "ment",
            "ness",
            "sion",
            "tion",
            "acy",
            "age",
            "ate",
            "est",
            "ful",
            "ian",
            "ify",
            "ing",
            "ion",
            "ise",
            "ish",
            "ism",
            "ist",
            "ize",
            "n't",
            "ous",
            "al",
            "ar",
            "ed",
            "en",
            "er",
            "fy",
            "ic",    
            "ly",
            "or",
            "ty",
            "s",
            "y"};
        // counts
        Map<String, Map<String, Integer>> TMatrix = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> EMatrix = new HashMap<String, Map<String, Integer>>();
        // this umatrix deal with unknown words
        Map<String, Map<String, Integer>> UMatrix = new HashMap<String, Map<String, Integer>>();
        // probability, i/o time consuming
        //Map<String, Map<String, Double>> tMatrix = new HashMap<String, Map<String, Double>>();
        //Map<String, Map<String, Double>> eMatrix = new HashMap<String, Map<String, Double>>();
        
        
        // train
        try {
            FileReader trainReader = new FileReader(trainFile);
            BufferedReader trainBr = new BufferedReader(trainReader);
            String currLine;
            while ((currLine = trainBr.readLine()) != null) {
                // break each line into an array of "word/tag" tokens
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
                    
                    // cumulate unknown word category count
                    if (UMatrix.containsKey(tag)) {
                        for (int j = 3; j < unknownWordCatArray.length; j++) {
                            if ( word.endsWith(unknownWordCatArray[j]) ) {
                                int prevCount = UMatrix.get(tag).get(unknownWordCatArray[j]).intValue();
                                int oneCount = UMatrix.get(tag).get("ONE-COUNT").intValue();
                                UMatrix.get(tag).put( unknownWordCatArray[j], new Integer( prevCount + 1) );
                                if (prevCount == 1) {
                                    // curr count > 1 => one-count will minus 1
                                    UMatrix.get(tag).put( "ONE-COUNT", new Integer( oneCount - 1) );
                                } else if (prevCount == 0) {
                                    UMatrix.get(tag).put( "ONE-COUNT", new Integer( oneCount + 1) );
                                }
                                break;
                            }
                        }
                        if (Character.isUpperCase(word.charAt(0)))
                            UMatrix.get(tag).put("CAP", new Integer( UMatrix.get(tag).get("CAP").intValue() + 1) );
                        UMatrix.get(tag).put( "TOTAL", new Integer(UMatrix.get(tag).get("TOTAL").intValue() + 1) );
                    }
                    else {
                        Map<String, Integer> hm = new HashMap<String, Integer>();
                        // init hm with all posssible categories
                        boolean foundLongestSuffix = false;
                        for (int j = 0; j < unknownWordCatArray.length; j++) {
                            if ( j > 2 && !foundLongestSuffix && word.endsWith(unknownWordCatArray[j]) ) {
                                hm.put( unknownWordCatArray[j], new Integer(1) );
                                foundLongestSuffix = true;
                            }
                            else
                                hm.put( unknownWordCatArray[j], new Integer(0) );
                        }
                        // then handle current word
                        hm.put( "TOTAL", new Integer(1) );
                        hm.put( "ONE-COUNT", new Integer(1) );
                        if (Character.isUpperCase(word.charAt(0)))
                            hm.put("CAP", new Integer(1));
                        UMatrix.put( tag, hm );
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
            ArrayList<String> tmpTagList = new ArrayList<String>(Arrays.asList(tagArray));
            tmpTagList.add("</s>");
            tagArray = tmpTagList.toArray(new String[0]);
            Arrays.sort(wordArray);
            
            FileWriter modelWriter = new FileWriter(modelFile);
            BufferedWriter modelBw = new BufferedWriter(modelWriter);
            
            // LINE_1 of model file is tag list
            String tagLine = "";
            for (int i = 0; i < tagArray.length; i++)
            {
                tagLine += tagArray[i] + " ";
            }
            modelBw.write(tagLine.trim());
            modelBw.newLine();
            
            // LINE_2 of model file is word list
            String wordLine = "";
            for (int i = 0; i < wordArray.length; i++)
            {
                wordLine += wordArray[i] + " ";
            }
            modelBw.write(wordLine);
            modelBw.newLine();
            
            // LINE_3 of model file is unknown word model categories
            String ucatLine = "";
            for (int i = 0; i < unknownWordCatArray.length; i++)
            {
                ucatLine += unknownWordCatArray[i] + " ";
            }
            modelBw.write(ucatLine);
            modelBw.newLine();
            
            // LINE_4 - LINE_48 (45 lines): transition matrix
            for (int i = 0; i < tagArray.length; i++)
            {
                // skip </s>
                if (tagArray[i].equals("</s>")) continue;
                
                String tLine = "";
                for (int j = 0; j < tagArray.length; j++)
                {
                    Integer count = TMatrix.get(tagArray[i]).get(tagArray[j]);
                    tLine += (count == null) ? 0 : count.intValue();
                    tLine += " ";
                }
                modelBw.write(tLine.trim());
                modelBw.newLine();
            }
            
            // LINE_49 - LINE_93 (45 lines): emission matrix
            for (int i = 0; i < tagArray.length; i++)
            {
                // skip </s>
                if (tagArray[i].equals("</s>")) continue;
                
                String eLine = "";
                for (int j = 0; j < wordArray.length; j++)
                {
                    Integer count = EMatrix.get(tagArray[i]).get(wordArray[j]);
                    eLine += (count == null) ? 0 : count.intValue();
                    eLine += " ";
                }
                modelBw.write(eLine.trim());
                modelBw.newLine();
            }
            
            // LINE_94 - LINE_138 (45 lines): unknown word model
            for (int i = 0; i < tagArray.length; i++)
            {
                // skip </s>
                if (tagArray[i].equals("</s>")) continue;
                
                String uLine = "";
                for (int j = 0; j < unknownWordCatArray.length; j++)
                {
                    Integer count = UMatrix.get(tagArray[i]).get(unknownWordCatArray[j]);
                    uLine += (count == null) ? 0 : count.intValue();
                    uLine += " ";
                }
                modelBw.write(uLine.trim());
                modelBw.newLine();
            }
            // close buffer and file
            modelBw.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
	}
}