package com.uum;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class simileAnalyzer {
	
	private static StanfordCoreNLP pipeline;
 	private static int numTruePositives = 0;
    private static int numFalsePositives = 0;
    private static int numTrueNegatives = 0;
    private static int numFalseNegatives = 0;
    
	static JTextArea textArea;
	static JTextField textFieldMetaphor;
	static JTextField textFieldSimile;
	static JTextField textFieldAnalogy;
	static JTextField textFieldViewResult;
	
	static List<CoreLabel> getWords(CoreMap sentence) {
	        return sentence.get(CoreAnnotations.TokensAnnotation.class);
    }
	
	public simileAnalyzer(JTextArea aa, JTextField mm, JTextField ss, JTextField al, JTextField rr) {
			Properties props = new Properties();
	        /*
	         * set a list of annotators for NLP pipeline using Stanford CoreNLP library
	         * tokenize: segment the text into individual tokens or words
	         * ssplit: split the text into sentences
	         * pos: perform parts of speech tagging on each token
	         * lemma: determine the base form or lemma of each word
	         * ner: identify named entities such as people, organizations, and locations
	         * parse: parse the sentence structure to identify grammatical relationships between words
	         * dcoref: perform coference resolution which means identifying which words refer to the same entities in the text
	         */
	        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,  dcoref");
	        pipeline = new StanfordCoreNLP(props);
			textArea = aa;
			this.textFieldMetaphor = mm;
    		this.textFieldSimile = ss;
    		this.textFieldAnalogy = al;
    		this.textFieldViewResult = rr;
	}
	
	public static boolean detectSimile(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        
        for (CoreMap sentence : sentences) {
        	// Get the root of the dependency tree
        	SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

            // Print the semantic graph
        	System.out.println("\nSemantic Graph:");
        	System.out.println(dependencies);
        	
        	//take a sentence, breaking down into individual tokens
        	//collect tokens as a list of strings
            List<CoreLabel> words = getWords(sentence);
            
            boolean foundSimile = findSimile(sentence);
            
            if(words.size() <= 10) {
            	if (foundSimile) {
            		 printResult(foundSimile);
            		 
            		 textFieldViewResult.setText("Simile");
                	 textFieldViewResult.setBackground(new Color(102, 255, 255));
                	 
    			     textFieldMetaphor.setText("0%");
    			     textFieldMetaphor.setBackground(Color.WHITE);
    			     
    			 	 textFieldSimile.setText("100%");
    			 	 textFieldSimile.setBackground(Color.GREEN);
    			 	 
    			 	 textFieldAnalogy.setText("0%");
    			 	 textFieldMetaphor.setBackground(Color.WHITE);
    			 	 
    			 	return true;
                } else {
                	textFieldViewResult.setText("Neutral");
   		   		 	textFieldViewResult.setBackground(new Color(255, 255, 51));
   		   		 	textFieldMetaphor.setText("0%");
   		   		 	textFieldMetaphor.setBackground(Color.WHITE);
   		   		 	textFieldSimile.setText("0%");
   		   		 	textFieldSimile.setBackground(Color.WHITE);
   		   		 	textFieldAnalogy.setText("0%");
   		   		 	textFieldAnalogy.setBackground(Color.WHITE);
   		   		 	
   		   		 	return false;
                }
            }
        }
        return true;
    }
	
	public static boolean findSimile(CoreMap sentence) {
		// Get the root of the dependency tree
    	SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
        
    	//take a sentence, breaking down into individual tokens
    	//collect tokens as a list of strings
    	List<CoreLabel> words = getWords(sentence);
    	
    	//loops through a list of words
        for (int i = 0; i < words.size(); i++) {
            CoreLabel currentWord = words.get(i);
            String token = currentWord.get(CoreAnnotations.TextAnnotation.class);
            
            // Check for simile patterns
            if (token.equalsIgnoreCase("like") || token.equalsIgnoreCase("as")) {
            	textArea.append("\nKeyword detected: " + token +"\n\n");
            	
            	if (i > 0 && i < words.size() - 1) {
                    CoreLabel prevToken = words.get(i - 1);
                    CoreLabel nextToken = words.get(i + 1);
                    String prevPos = prevToken.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    String nextPos = nextToken.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    
                    System.out.println("Previous Token: " + prevPos);
                    System.out.println("Next Token: " + nextPos);
                    
                    // Verify if adjacent words are adjectives
                    if ((prevPos.startsWith("JJ") && nextPos.startsWith("JJ")) ||
                    		(prevPos.startsWith("JJ") && nextPos.startsWith("NN")) ||
                    		(prevPos.startsWith("VBZ") && nextPos.startsWith("NNS")) ||
                    		(prevPos.startsWith("VBZ") && nextPos.startsWith("JJ")) ||
                    		(prevPos.startsWith("VBD") && nextPos.startsWith("JJ")) ||
                    		(prevPos.startsWith("VBD") && nextPos.startsWith("NNS")) ||
                    		(prevPos.startsWith("VBN") && nextPos.startsWith("NNS")) ||
                    		(prevPos.startsWith("VBN") && nextPos.startsWith("DT")) ||
                    		(prevPos.startsWith("VBD") && nextPos.startsWith("RB")) ||
                    		(prevPos.startsWith("VBD") && nextPos.startsWith("NN")) ||
                    		(prevPos.startsWith("VBD") && nextPos.startsWith("VBG")) ||
                    		(prevPos.startsWith("VBG") && nextPos.startsWith("DT")) ||
                    		((prevPos.startsWith("VBD") || prevPos.startsWith("VBP") || prevPos.startsWith("VBZ") || prevPos.startsWith("VBG"))  && nextPos.startsWith("DT")) ||
                    		(prevPos.startsWith("NN") && nextPos.startsWith("NN")) ||
                    		(prevPos.startsWith("NNS") && nextPos.startsWith("DT")) ||
                    		(prevPos.startsWith("NN") && nextPos.startsWith("DT")) ||
                    		(prevPos.startsWith("POS") && nextPos.startsWith("JJ"))) {
                         
                    	// Check if "like" or "as" is preceded by a noun or pronoun
                        if (i > 1) {
                            CoreLabel prevPrevToken = words.get(i - 2);
                            String prevPrevPos = prevPrevToken.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                            System.out.println("Previous prev Token: " + prevPrevPos);
                            
                            if (prevPrevPos.startsWith("NN") || prevPrevPos.startsWith("PRP") || prevPrevPos.startsWith("VBD") 
                            		|| prevPrevPos.startsWith("VBP") || prevPrevPos.startsWith("JJ") || prevPrevPos.startsWith("RB")) {
                                return true; // Simile detected
                            }
                        }
                    } else {
                        // Check if "like" or "as" is followed by a noun or pronoun
                        if (i < words.size() - 2) {
                            CoreLabel nextNextToken = words.get(i + 2);
                            String nextNextPos = nextNextToken.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                            System.out.println("Next next Token: " + nextNextPos);
                            if (nextNextPos.startsWith("NN") || nextNextPos.startsWith("PRP") || nextNextPos.startsWith("NNS")  || nextNextPos.startsWith("IN") ) {
                                return true; // Simile detected
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
	
	public static void printResult(boolean foundSimile) {
	   	 if (foundSimile) {
	             System.out.println("\nThe sentence contains a simile.");
	     } else {
	             System.out.println("\nThe sentence does not contain a simile.");
	     }
	}
}
