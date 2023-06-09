package com.uum;

import java.awt.Color;
import java.util.List;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class metaphorAnalyzer {
		private static StanfordCoreNLP pipeline;
		
		static JTextArea textArea;
		static JTextField textFieldMetaphor;
		static JTextField textFieldSimile;
		static JTextField textFieldAnalogy;
		static JTextField textFieldViewResult;
		
		static List<CoreLabel> getWords(CoreMap sentence) {
	        return sentence.get(CoreAnnotations.TokensAnnotation.class);
		}
		
		public metaphorAnalyzer(JTextArea aa, JTextField mm, JTextField ss, JTextField al, JTextField rr) {
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
		
		public static boolean detectMetaphor(String text) {
			Annotation document = new Annotation(text);
	        pipeline.annotate(document);
	        
	        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
			
	        for(CoreMap sentence : sentences) {
	        	// Get the root of the dependency tree
	        	SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

	        	// Print the semantic graph
	        	System.out.println("\nSemantic Graph:");
	        	System.out.println(dependencies);
	        	
	        	//take a sentence, breaking down into individual tokens
	        	//collect tokens as a list of strings
	            List<CoreLabel> words = getWords(sentence);
	            
	            boolean foundMetaphor = findMetaphor(sentence);
	            
	            if (foundMetaphor) {
	 	       		 printResult(foundMetaphor);
	 	       		 
	 	       		 textFieldViewResult.setText("Metaphor");
	 	           	 textFieldViewResult.setBackground(new Color(102, 255, 255));
	 	           	 
	 			     textFieldMetaphor.setText("100%");
	 			     textFieldMetaphor.setBackground(Color.GREEN);
	 			     
	 			 	 textFieldSimile.setText("0%");
	 			 	 textFieldMetaphor.setBackground(Color.WHITE);
	 			 	
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
	        return false;
		}
		
		public static boolean findMetaphor(CoreMap sentence) {
			// Get the root of the dependency tree
	    	SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
	        
	    	//take a sentence, breaking down into individual tokens
	    	//collect tokens as a list of strings
	    	List<CoreLabel> words = getWords(sentence);
	    	
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
	        for (IndexedWord vertex : dependencies.vertexSet()) {
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
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }
		
		private static boolean hasMetaphoricalNoun2Pattern(SemanticGraph dependencies) {
	        for (IndexedWord vertex : dependencies.vertexSet()) {
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
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }

	    private static boolean hasMetaphoricalAdjectiveNounPattern(SemanticGraph dependencies) {
	        for (IndexedWord vertex : dependencies.vertexSet()) {
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
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalNounVerbPattern(SemanticGraph dependencies) {
	        for (IndexedWord vertex : dependencies.vertexSet()) {
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
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalNounNounPattern(SemanticGraph dependencies) {
	        for (IndexedWord vertex : dependencies.vertexSet()) {
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
	                         return true;
	                     }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalXisaYPattern(SemanticGraph dependencies) {
	        for (IndexedWord vertex : dependencies.vertexSet()) {
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
	                             return true;
	                         }
	                	 }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalVerbAdjectivePattern(SemanticGraph dependencies) {
	        for (IndexedWord vertex : dependencies.vertexSet()) {
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
	                        return true;
	                    }
	                }
	            }
	        }
	        return false;
	    }
	    
	    private static boolean hasMetaphoricalSpecificCase(SemanticGraph dependencies) {
	        for (IndexedWord vertex : dependencies.vertexSet()) {
	        	List<SemanticGraphEdge> outgoingEdges = dependencies.outgoingEdgeList(vertex);
	            System.out.println(outgoingEdges);
	            for (SemanticGraphEdge edge : outgoingEdges) {
	            	System.out.println(edge.getTarget().toString());
	                if (edge.getSource().tag().startsWith("VBN") && edge.getTarget().originalText().equals("heart")) {
	                	System.out.println("8");
	                    return true;
	                }
	            }
	        }
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