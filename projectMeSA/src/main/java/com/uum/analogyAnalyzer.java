package com.uum;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class analogyAnalyzer {
	
	static JTextArea textArea;
	static JTextField textFieldMetaphor;
	static JTextField textFieldSimile;
	static JTextField textFieldAnalogy;
	static JTextField textFieldViewResult;
	
	public analogyAnalyzer(JTextArea aa) {
		this.textArea = aa;
	}
	
	public static boolean detectAnalogy(String text) {
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
	        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	        
	        Annotation document = new Annotation(text);
	        pipeline.annotate(document);
	        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

	        for (CoreMap sentence : sentences) {
	        	//take a sentence, breaking down into individual tokens
	        	//collect tokens as a list of strings
	            List<String> words = sentence.get(CoreAnnotations.TokensAnnotation.class)
	                    .stream()
	                    .map(token -> token.get(CoreAnnotations.TextAnnotation.class))
	                    .collect(Collectors.toList());
	            
	            if(words.size() > 10) {
	            	if(containsAnalogy(sentence)) {
		                String analogy = sentence.toString();
		                System.out.println("Analogy detected: " + analogy);
		                return true;
		            } 
	            }
	        }
	        return false;
	}
	
	public static boolean containsAnalogy(CoreMap sentence) {
		//contain metaphor characteristics
    	metaphorAnalyzer metaphor = new metaphorAnalyzer(textArea, textFieldMetaphor, textFieldSimile, textFieldAnalogy, textFieldViewResult);
    	boolean mAnalyzer = metaphor.findMetaphor(sentence);
    	
    	//contain simile characteristics
    	simileAnalyzer simile = new simileAnalyzer(textArea, textFieldMetaphor, textFieldSimile, textFieldAnalogy, textFieldViewResult);
    	boolean sAnalyzer = simile.findSimile(sentence);
    	
    	//since the sentence has the characteristics of metaphor and simile
    	//then it is an analogy
    	//hence analogy is being detected
    	if (mAnalyzer && sAnalyzer) {
    		return true;
    	}
    	return false;
	}
	
	public static double detectAnalogyAccuracy(String text, List<String> expectedAnalogies) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        int numTruePositives = 0;
        int numFalsePositives = 0;
        int numFalseNegatives = 0;

        for (CoreMap sentence : sentences) {
            boolean detectedAnalogy = containsAnalogy(sentence);
            boolean expectedAnalogy = expectedAnalogies.contains(sentence.toString());
            if (detectedAnalogy && expectedAnalogy) {
                numTruePositives++;
            } else if (detectedAnalogy && !expectedAnalogy) {
                numFalsePositives++;
            } else if (!detectedAnalogy && expectedAnalogy) {
                numFalseNegatives++;
            }
        }
        
        //ratio of true positives to the total number of positive predictions 
        //true positives plus false positives
        //TP+FP
        double precision = (double) numTruePositives / (numTruePositives + numFalsePositives);
        
        //ratio of true positives to the total number of true metaphors 
        //true positives plus false negatives
        //TP+FN
        double recall = (double) numTruePositives / (numTruePositives + numFalseNegatives);
        
        //harmonic mean of precision and recall
        //reciprocal of the arithmetic mean of the reciprocals of a set of numbers
        double f1Score = 2 * precision * recall / (precision + recall);

        System.out.println("True positives: " + numTruePositives);
        System.out.println("False positives: " + numFalsePositives);
        System.out.println("False negatives: " + numFalseNegatives);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1 score: " + f1Score);

        return f1Score;
    }
	
	public static double calcAccuracy(String text) {
		List<String> expectedAnalogies = Arrays.asList(text);
		
		double accuracy = detectAnalogyAccuracy(text, expectedAnalogies) * 100;
        System.out.println("\nAnalogy detection accuracy: " + accuracy);
        
        return accuracy;
	}
}
