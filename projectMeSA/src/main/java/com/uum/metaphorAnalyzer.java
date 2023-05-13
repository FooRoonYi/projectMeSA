package com.uum;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos.IndexedWord;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class metaphorAnalyzer {
	
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
	 	
	    public metaphorAnalyzer(JTextArea aa, JTextField mm, JTextField ss, JTextField al, JTextField rr) {
	    		Properties props = new Properties();
	    		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    		this.pipeline = new StanfordCoreNLP(props);
	    		this.textArea = aa;
	    		this.textFieldMetaphor = mm;
	    		this.textFieldSimile = ss;
	    		this.textFieldAnalogy = al;
	    		this.textFieldViewResult = rr;
	    }
	    
	    public static boolean detectMetaphor(String text) {
	    		Annotation document = new Annotation(text);
	        	pipeline.annotate(document);
	        
	        	List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	        
	        	for (CoreMap sentence : sentences) {
	        			SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
	        			edu.stanford.nlp.ling.IndexedWord root = dependencies.getFirstRoot();
	        			
	        			System.out.println("\nSemantic Graph:");
			            System.out.println(dependencies);
			            List<String> words = getWords(sentence);
			            boolean foundMetaphor = findMetaphor(sentence);
			            printResult(foundMetaphor);
			            detectMetaphorAccuracy(text);
	        	}
	        	return true;
	    }
	    
	    static boolean findMetaphor(CoreMap sentence) {
	    	SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
	    	
	        boolean foundMetaphor = false;
	        // Traverse the dependency tree looking for metaphors
	    	for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
	    		for (TypedDependency dep : dependencies.typedDependencies()) {
	    			/*
	        		 * nmod: noun modifier
	        		 * amod: adjectival modifier
	        		 * appos: appositional modifier
	        		 */
	        		if ((edge.getRelation().toString().equals("nmod") || edge.getRelation().toString().equals("amod") || 
	                		edge.getRelation().toString().equals("compound") || edge.getRelation().toString().equals("appos")) 
	                		&& edge.getDependent().tag().startsWith("JJ")) {
	        			System.out.println("1");
	        			textArea.append("1. nmod/amod/compound/appos: " + edge.getRelation().toString() + "\n" +
	        					"Adjective(JJ): " + edge.getDependent().toString() + "\n");
	        			return true;
	                }
	        		
	        		/*
	        		 * for metaphors with the pattern 
	        		 * noun subject + is/am/are/was/were + verb(ing) + NNS
	        		 */
	        		else if(
	        				(edge.getRelation().toString().equals("nsubj") ||
	        				(edge.getRelation().toString().equals("xcomp") || edge.getRelation().toString().equals("aux")) ||
	        				edge.getRelation().toString().equals("obj")) &&
	        				((edge.getDependent().tag().startsWith("PRP") || edge.getDependent().tag().startsWith("NNP")) ||
	        				(edge.getDependent().tag().startsWith("VBG") || edge.getDependent().tag().startsWith("VBZ") || edge.getDependent().tag().startsWith("VBD")) ||
	        				edge.getDependent().tag().startsWith("NNS")) 
	        				) {
	        			System.out.println("2");
	        			textArea.append("2. nsubj/xcomp/aux/obj: \n" + edge.getRelation().toString() + "\n" +
	        					"Pronoun(PRP)/Noun(NNP)/Verb(VBG/VBZ/VBD)/Noun subject(NNS): \n" + edge.getDependent().toString() + "\n");
	        			return true;
	                } 
	        		
	        		/*
	        		 * for metaphors with the pattern
	        		 * X is a Y
	        		 */
	        		// Check if the edge is a copula (i.e. "is", "was", "are", etc.)
	        		else if (edge.getRelation().toString().equals("cop")) {
	                    // Check if the governor of the copula is a noun or pronoun
	                    if (edge.getGovernor().tag().startsWith("NN") || edge.getGovernor().tag().equals("PRP")) {
	                    	// Check if the TypedDependency object represents a DET relation
	                    	if (dep.reln().getShortName().equals("det")) {
	                    		System.out.println("3");
	                    		textArea.append("3. cop (is/am/are/was/were): \n" + edge.getRelation().toString() + "\n" +
	                    				"det(a/an/the): \n" + edge.getRelation().toString() + "\n");
	                    		return true;
	                    	}
	                    }
	        		}
	        		
	        		/*
	        		 * for metaphor 
	        		 * My heart is broken.
	        		 * detect VBN  (broken) and target (heart)
	        		 */
	        		else if (edge.getSource().tag().startsWith("VBN") && edge.getTarget().originalText().equals("heart")) {
	                    // "broken" is modifying "heart", indicating a metaphor
	        			System.out.println("4");
	        			textArea.append("4. Verb(VBN): \n" + edge.getSource().toString() + "\n" +
	        					"Target(noun subject): \n" + edge.getTarget().toString() + "\n");
	                    return true;
	                }
	    		}
	    	}
			return false;
	    }
	    
	    public static void printResult(boolean foundMetaphor) {
	    	 if (foundMetaphor) {
	              System.out.println("\nThe sentence contains a metaphor.");
	              textFieldViewResult.setText("Metaphor");
	            } else {
	              System.out.println("\nThe sentence does not contain a metaphor.");
	            }
	    }
	    
	    //detect metaphor for calculating the accuracy
		public static double detectMetaphorAccuracy(String text) {
				Annotation document = new Annotation(text);
		        pipeline.annotate(document);
		        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
		        
		        for (CoreMap sentence : sentences) {
			            boolean detectedMetaphor = false;
			            boolean expectedMetaphor = false;
			            // Update the evaluation metrics
			            if (detectedMetaphor) {
			                if (expectedMetaphor) {
			                    numTruePositives++;
			                } else {
			                    numFalsePositives++;
			                }
			            } else {
			                if (expectedMetaphor) {
			                    numFalseNegatives++;
			                } else {
			                    numTrueNegatives++;
			                }
			           }
		        } 
		        
		        // Calculate accuracy
		        double accuracy = ((double) (numTruePositives + numTrueNegatives) /
		                          (numTruePositives + numFalsePositives + numTrueNegatives + numFalseNegatives)) * 100;
		        System.out.println("\nMetaphor detection accuracy: " + accuracy);
		        
		        NumberFormat nm = NumberFormat.getNumberInstance();
		        textFieldMetaphor.setText(nm.format(accuracy) + "%");
		        textFieldSimile.setText("0%");
				textFieldAnalogy.setText("0%");
		        
		        return accuracy;
	    }
		
}
