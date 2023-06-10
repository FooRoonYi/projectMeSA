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
	    
	    static JTextArea textArea;
	 	
	    static List<CoreLabel> getWords(CoreMap sentence) {
	        return sentence.get(CoreAnnotations.TokensAnnotation.class);
		}
	 	
	    public metaphorAnalyzer(JTextArea aa) {
	    		Properties props = new Properties();
	    		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    		this.pipeline = new StanfordCoreNLP(props);
	    		textArea = aa;
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
			            List<CoreLabel> words = getWords(sentence);
			            boolean foundMetaphor = findMetaphor(sentence);
			            if(foundMetaphor) {
			            	return true;
			            }
			            printResult(foundMetaphor);
	        	}
	        	return false;
	    }
	    
	    public static boolean findMetaphor(CoreMap sentence) {
			// Get the root of the dependency tree
	    	SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
	        
	    	//take a sentence, breaking down into individual tokens
	    	//collect tokens as a list of strings
	    	List<CoreLabel> words = getWords(sentence);
	    	
	    	textArea.append("For metaphor part...\n"
	    			+ "\n**(for your information...)\n"
	    			+ "Noun - NN/NNS/NNP \t	 Pronoun - NNP/PRP\n "
	    			+ "Verb - VB/VBZ/VBG/VBD/VBP/VBZ \n"
	    			+ "Adjective - JJ \t Determinant (a/an/the)\n\n");
	    	
	    	// Check for verb-noun patterns
	        if (hasMetaphoricalVerbNounPattern(dependencies)) {
	            return true;
	        } 
	        
	        // Check for noun-noun patterns
	        else if (hasMetaphoricalNoun2Pattern(dependencies)) {
	            return true;
	        }
	        
	        // Check for adjective-noun patterns
	        else if (hasMetaphoricalAdjectiveNounPattern(dependencies)) {
	            return true;
	        }
	        
	        // Check for noun-verb patterns
	        else if (hasMetaphoricalNounVerbPattern(dependencies)) {
	            return true;
	        }
	        
	        else if(hasMetaphoricalVerbAdjectivePattern(dependencies)) {
	        	return true;
	        }
	        
	        else if(hasMetaphoricalNounNounPattern(dependencies)) {
	        	return true;
	        }
	        
	        else if(hasMetaphoricalXisaYPattern(dependencies)) {
	        	return true;
	        }
	        
	        /*
			 * for metaphor 
			 * My heart is broken.
			 * detect VBN  (broken) and target (heart)
			 */
	        else if(hasMetaphoricalSpecificCase(dependencies)) {
	        	return true;
	        }
	        return false;
	    }
		
		private static boolean hasMetaphoricalVerbNounPattern(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	            String pos = vertex.get(CoreAnnotations.PartOfSpeechAnnotation.class);

	            // Check for verb-noun pattern (VB + NN)
	            if (pos.startsWith("VB")) {
	            	System.out.println("POS: "+ pos.toString());
	            	
	                List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	                for (SemanticGraphEdge edge : outgoingEdges) {
	                    if (edge.getRelation().getShortName().equals("dobj")) {
	                        String pos2 = edge.getTarget().get(CoreAnnotations.PartOfSpeechAnnotation.class);
	                        if (pos2.startsWith("NN")) {
	                        	System.out.println("POS2: "+ pos2.toString());
	                        	System.out.println("1");
	                        	textArea.append("POS tag of keyword detected as metaphor: \n" 
	                        			+ "1. " + pos.toString() + "\n" 
	                        			+ "2. " + pos2.toString() + "\n");
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }
		
		private static boolean hasMetaphoricalNoun2Pattern(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	            String pos = vertex.get(CoreAnnotations.PartOfSpeechAnnotation.class);

	            // Check for verb-noun pattern (NNP + NN)
	            if (pos.startsWith("NN") || pos.startsWith("NNP")) {
	            	System.out.println("POS: " + pos.toString());
	                List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	                for (SemanticGraphEdge edge : outgoingEdges) {
	                    if (edge.getRelation().getShortName().equals("dobj") || edge.getRelation().getShortName().equals("nsubj") ||
		        				(edge.getRelation().getShortName().equals("xcomp") || edge.getRelation().getShortName().equals("aux")) ||
		        				edge.getRelation().getShortName().equals("obj")) {
	                        String pos2 = edge.getTarget().get(CoreAnnotations.PartOfSpeechAnnotation.class);
	                        System.out.println("Target: " + edge.getTarget().toString());
	                        if (pos2.startsWith("NN") || pos2.startsWith("NNS") || pos2.startsWith("PRP")) {
	                        	System.out.println("POS2: "+ pos2.toString());
	                        	System.out.println("2");
	                        	textArea.append("POS tag of keyword detected as metaphor: \n" 
	                        			+ "1. " + pos.toString() + "\n" 
	                        			+ "2. " + pos2.toString() + "\n");
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }

	    private static boolean hasMetaphoricalAdjectiveNounPattern(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	            String pos = vertex.get(CoreAnnotations.PartOfSpeechAnnotation.class);

	            // Check for adjective-noun pattern (JJ + NN)
	            if (pos.startsWith("JJ")) {
	            	System.out.println("POS: "+ pos.toString());
	            	
	                List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	                System.out.println(outgoingEdges);
	                for (SemanticGraphEdge edge : outgoingEdges) {
	                    if (edge.getRelation().getShortName().equals("nmod") || edge.getRelation().getShortName().equals("nsubj")) {
	                        String pos2 = edge.getTarget().get(CoreAnnotations.PartOfSpeechAnnotation.class);
	                        if (pos2.startsWith("NN")) {
	                        	System.out.println("POS2: "+ pos2.toString());
	                        	System.out.println("3");
	                        	textArea.append("POS tag of keyword detected as metaphor: \n" 
	                        			+ "1. " + pos.toString() + "\n" 
	                        			+ "2. " + pos2.toString() + "\n");
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalNounVerbPattern(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	            String pos = vertex.get(CoreAnnotations.PartOfSpeechAnnotation.class);

	            // Check for noun-verb pattern (PRP/NN + VB + NN)
	            if (pos.startsWith("VBG") || pos.startsWith("VBZ")) {
	            	System.out.println("POS: "+ pos.toString());
	            	
	                List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	                System.out.println(outgoingEdges);
	                for (SemanticGraphEdge edge : outgoingEdges) {
	                    if (edge.getRelation().getShortName().equals("xcomp") || edge.getRelation().getShortName().equals("obj")) {
	                        String pos2 = edge.getTarget().get(CoreAnnotations.PartOfSpeechAnnotation.class);
	                        if (pos2.startsWith("NNS") || pos2.startsWith("NN")) {
	                        	System.out.println("POS2: "+ pos2.toString());
	                        	System.out.println("4");
	                        	textArea.append("POS tag of keyword detected as metaphor: \n" 
	                        			+ "1. " + pos.toString() + "\n" 
	                        			+ "2. " + pos2.toString() + "\n");
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalNounNounPattern(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	            String pos = vertex.get(CoreAnnotations.PartOfSpeechAnnotation.class);

	            // Check for noun-verb pattern (PRP/NN + VB + NN)
	            if (pos.startsWith("NN")) {
	            	System.out.println("POS: "+ pos.toString());
	            	
	                List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	                System.out.println(outgoingEdges);
	                for (SemanticGraphEdge edge : outgoingEdges) {
	                	 String pos2 = edge.getTarget().get(CoreAnnotations.PartOfSpeechAnnotation.class);
	                     if (pos2.startsWith("NNS")) {
	                    	System.out.println("POS2: "+ pos2.toString());
	                     	System.out.println("5");
	                     	textArea.append("POS tag of keyword detected as metaphor: \n" 
                        			+ "1. " + pos.toString() + "\n" 
                        			+ "2. " + pos2.toString() + "\n");
	                         return true;
	                     }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalXisaYPattern(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	            String pos = vertex.get(CoreAnnotations.PartOfSpeechAnnotation.class);

	            // Check for noun-verb pattern (PRP/NN + VB + NN)
	            if (pos.startsWith("NN")) {
	            	System.out.println("POS: "+ pos.toString());
	            	
	                List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	                System.out.println(outgoingEdges);
	                for (SemanticGraphEdge edge : outgoingEdges) {
	                	 if(edge.getDependent().tag().startsWith("VBZ") || edge.getDependent().tag().startsWith("VB") || edge.getDependent().tag().startsWith("DT")) {
	                		 System.out.println("Dependent: " + edge.getDependent().tag().toString());
	                		 String pos2 = edge.getTarget().get(CoreAnnotations.PartOfSpeechAnnotation.class);
	                         if (pos2.startsWith("JJ")) {
	                        	 System.out.println("POS2: "+ pos2.toString());
	                         	System.out.println("6");
	                         	textArea.append("POS tag of keyword detected as metaphor: \n" 
	                        			+ "1. " + pos.toString() + "\n" 
	                        			+ "2. " + pos2.toString() + "\n");
	                             return true;
	                         }
	                	 }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalVerbAdjectivePattern(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	            String pos = vertex.get(CoreAnnotations.PartOfSpeechAnnotation.class);

	            // Check for noun-verb pattern (PRP/NN + VB + NN)
	            if (pos.startsWith("JJ")) {
	            	System.out.println("POS: "+ pos.toString());
	                List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	                System.out.println(outgoingEdges);
	                for (SemanticGraphEdge edge : outgoingEdges) {
	                	String pos2 = edge.getTarget().get(CoreAnnotations.PartOfSpeechAnnotation.class);
	                	System.out.println("POS2: "+ pos2.toString());
	                	System.out.println(edge.getTarget().toString());
	                    if (pos2.startsWith("PRP") || pos2.startsWith("VBP")) {
	                    	System.out.println("7");
	                    	textArea.append("POS tag of keyword detected as metaphor: \n" 
                        			+ "1. " + pos.toString() + "\n" 
                        			+ "2. " + pos2.toString() + "\n");
	                        return true;
	                    }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalSpecificCase(SemanticGraph dependencies) {
	        for (edu.stanford.nlp.ling.IndexedWord vertex : dependencies.vertexSet()) {
	        	List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	            System.out.println(outgoingEdges);
	            for (SemanticGraphEdge edge : outgoingEdges) {
	            	System.out.println(edge.getTarget().toString());
	                if (edge.getSource().tag().startsWith("VBN") && edge.getTarget().originalText().equals("heart")) {
	                	System.out.println("8");
	                	textArea.append("Key Point (grammar structure) of keyword detected as metaphor: \n" 
                    			+ "1. " + edge.getSource().tag().toString() + "\n" 
                    			+ "2. " + edge.getTarget().toString() + "\n");
	                    return true;
	                }
	            }
	        }
	        textArea.append("No metaphorical sentence is detected.\n");
	        return false;
	    }
	    
	    public static void printResult(boolean foundMetaphor) {
	    	 if (foundMetaphor) {
	              System.out.println("\nThe sentence contains a metaphor.");
	            } else {
	              System.out.println("\nThe sentence does not contain a metaphor.");
	            }
	    }
		
}
