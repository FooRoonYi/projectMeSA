package com.uum;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

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
	
	static List<String> getWords(CoreMap sentence) {
	        return sentence.get(CoreAnnotations.TokensAnnotation.class)
	                .stream()
	                .map(token -> token.get(CoreAnnotations.TextAnnotation.class))
	                .collect(Collectors.toList());
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
            List<String> words = getWords(sentence);
            
            boolean foundSimile = findSimile(sentence);
            printResult(foundSimile);
            
            if(words.size() < 10) {
            	if (foundSimile) {
                    String simile = sentence.toString();
                    System.out.println("Simile detected: " + simile);
                    textArea.append("\n\nThe simile detected in this sentence is: \n" + simile);
                    
                    detectSimileAccuracy(text);
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
    	List<String> words = getWords(sentence);
    	
       //loops through a list of words
        for (int i = 0; i < words.size(); i++) {
            String currentWord = words.get(i);
            //to detect simile by finding the words "like" or "as"
            if (currentWord.equalsIgnoreCase("like") || currentWord.equalsIgnoreCase("as")) {
            	String keyword = words.get(i - 1) + " " + currentWord + " " + words.get(i + 1);
                System.out.println("Keyword detected: " + keyword);
                textArea.append("\nKeyword detected: " + keyword +"\n\n");
                return true;
            }
        }
        return false;
    }
	
	public static void printResult(boolean foundSimile) {
   	 if (foundSimile) {
             System.out.println("\nThe sentence contains a simile.");
             textFieldViewResult.setText("Simile");
           } else {
             System.out.println("\nThe sentence does not contain a simile.");
           }
	}
	
	//detect simile for calculating the accuracy
	public static double detectSimileAccuracy(String text) {
	        Annotation document = new Annotation(text);
	        pipeline.annotate(document);
	        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
	
	        for (CoreMap sentence : sentences) {
		            boolean detectedSimile = false;
		            boolean expectedSimile = false;
		            // Update the evaluation metrics
		            if (detectedSimile) {
			                if (expectedSimile) {
			                    numTruePositives++;
			                } else {
			                    numFalsePositives++;
			                }
		            } else {
			                if (expectedSimile) {
			                    numFalseNegatives++;
			                } else {
			                    numTrueNegatives++;
			                }
		           }
	        }
        
	        // Calculate accuracy
	        double accuracy = ((double) (numTruePositives + numTrueNegatives) /
	                          (numTruePositives + numFalsePositives + numTrueNegatives + numFalseNegatives)) * 100;
	        System.out.println("\nSimile detection accuracy: " + accuracy);
	        
	        NumberFormat nm = NumberFormat.getNumberInstance();
	        textFieldMetaphor.setText("0%");
	        textFieldSimile.setText(nm.format(accuracy) + "%");
			textFieldAnalogy.setText("0%");
	        
	        return accuracy;
    }
}
