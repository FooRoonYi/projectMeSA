package com.uum;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class analogyAnalyzer {
	
	private static StanfordCoreNLP pipeline;
	
	static JTextArea textArea;
	
	static List<CoreLabel> getWords(CoreMap sentence) {
        return sentence.get(CoreAnnotations.TokensAnnotation.class);
	}
	
	public analogyAnalyzer(JTextArea aa) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		this.pipeline = new StanfordCoreNLP(props);
		this.textArea = aa;
	}
	
	public static boolean detectAnalogy(String text) {
		 	Annotation document = new Annotation(text);
	        pipeline.annotate(document);
	        
	        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

	        for (CoreMap sentence : sentences) {
	        	//take a sentence, breaking down into individual tokens
	        	//collect tokens as a list of strings
	        	List<CoreLabel> words = getWords(sentence);
	            
	            boolean foundAnalogy = findAnalogy(sentence);
	            
	            if(words.size() > 10) {
	            	if(foundAnalogy) {
		                return true;
		            } 
	            }
	            
	            printResult(foundAnalogy);
	        }
	        return false;
	}
	
	static boolean findAnalogy(CoreMap sentence) {
		
		textArea.append("For analogy part...\n" +
				"---------------------------------------\n");
		
		//contain metaphor characteristics
		metaphorAnalyzer metaphor = new metaphorAnalyzer(textArea);
    	boolean foundMetaphor = metaphor.findMetaphor(sentence);
    	
    	//contain simile characteristics
    	simileAnalyzer simile = new simileAnalyzer(textArea);
    	boolean foundSimile = simile.findSimile(sentence);
    	
    	//since the sentence has the characteristics of metaphor and simile
    	//then it is an analogy
    	//hence analogy is detected
    	if (foundMetaphor && foundSimile) {
    		return true;
    	}
    	textArea.append("---------------------------------------\n"
    			+ "∴Since there is no any metaphor or simile characteristics in this sentence, "
    			+ "\nhence no analogy sentence is detected.\n");
    	return false;
	}
	
	public static void printResult(boolean foundAnalogy) {
	   	 if (foundAnalogy) {
	   		System.out.println("\nThe sentence contains an analogy.");
	     } else {
	        System.out.println("\nThe sentence does not contain an analogy.");
	    }
	}
	
}
