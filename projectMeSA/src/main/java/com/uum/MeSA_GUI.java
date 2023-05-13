package com.uum;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import java.awt.SystemColor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.uum.analogyAnalyzer;
import com.uum.metaphorAnalyzer;
import com.uum.simileAnalyzer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import javax.swing.border.MatteBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MeSA_GUI extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldViewResult;
	private JTextField InputTextField;
	private JTextField textFieldMetaphor;
	private JTextField textFieldSimile;
	private JTextField textFieldAnalogy;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MeSA_GUI frame = new MeSA_GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MeSA_GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1028, 768);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		panel_3.setBounds(72, 462, 632, 247);
		contentPane.add(panel_3);
		panel_3.setLayout(null);
		
		JPanel panel_10 = new JPanel();
		panel_10.setBackground(new Color(255, 240, 245));
		panel_10.setBorder(null);
		panel_10.setBounds(10, 10, 612, 227);
		panel_3.add(panel_10);
		panel_10.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 10, 568, 182);
		panel_10.add(textArea);
		textArea.setBackground(new Color(255, 240, 245));
		textArea.setFont(new Font("Times New Roman", Font.BOLD, 16));
		
		JLabel lblsentenceExamples = new JLabel("\"Keywords detected:");
		lblsentenceExamples.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblsentenceExamples.setBounds(72, 423, 300, 41);
		contentPane.add(lblsentenceExamples);
		
		textFieldViewResult = new JTextField();
		textFieldViewResult.setBounds(189, 349, 133, 41);
		contentPane.add(textFieldViewResult);
		textFieldViewResult.setEditable(false);
		textFieldViewResult.setFont(new Font("Times New Roman", Font.PLAIN, 22));
		textFieldViewResult.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldViewResult.setBackground(new Color(247, 164, 164));
		textFieldViewResult.setColumns(10);
		
		JLabel lblResult = new JLabel("Result:");
		lblResult.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblResult.setBounds(72, 349, 145, 41);
		contentPane.add(lblResult);
		
		JLabel lblAccuracy = new JLabel("Accuracy:");
		lblAccuracy.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblAccuracy.setBounds(72, 188, 145, 41);
		contentPane.add(lblAccuracy);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(72, 224, 854, 93);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_4.setBounds(0, 0, 291, 93);
		panel_2.add(panel_4);
		panel_4.setLayout(null);
		
		JLabel lblMetaphor = new JLabel("Metaphor");
		lblMetaphor.setForeground(Color.BLACK);
		lblMetaphor.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblMetaphor.setHorizontalAlignment(SwingConstants.CENTER);
		lblMetaphor.setBounds(0, 0, 291, 42);
		panel_4.add(lblMetaphor);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(Color.WHITE);
		panel_7.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_7.setBounds(0, 42, 291, 51);
		panel_4.add(panel_7);
		panel_7.setLayout(null);
		
		textFieldMetaphor = new JTextField();
		textFieldMetaphor.setBackground(Color.WHITE);
		textFieldMetaphor.setEditable(false);
		textFieldMetaphor.setFont(new Font("Times New Roman", Font.PLAIN, 24));
		textFieldMetaphor.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldMetaphor.setBounds(10, 10, 271, 31);
		panel_7.add(textFieldMetaphor);
		textFieldMetaphor.setColumns(10);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_5.setBounds(290, 0, 280, 93);
		panel_2.add(panel_5);
		panel_5.setLayout(null);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_8.setBackground(Color.WHITE);
		panel_8.setBounds(0, 43, 280, 50);
		panel_5.add(panel_8);
		panel_8.setLayout(null);
		
		textFieldSimile = new JTextField();
		textFieldSimile.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldSimile.setFont(new Font("Times New Roman", Font.PLAIN, 24));
		textFieldSimile.setBackground(Color.WHITE);
		textFieldSimile.setEditable(false);
		textFieldSimile.setBounds(10, 10, 260, 30);
		panel_8.add(textFieldSimile);
		textFieldSimile.setColumns(10);
		
		JLabel lblSimile = new JLabel("Simile");
		lblSimile.setForeground(Color.BLACK);
		lblSimile.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblSimile.setHorizontalAlignment(SwingConstants.CENTER);
		lblSimile.setBounds(0, 0, 280, 50);
		panel_5.add(lblSimile);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.WHITE);
		panel_6.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_6.setBounds(568, 0, 286, 93);
		panel_2.add(panel_6);
		panel_6.setLayout(null);
		
		JPanel panel_9 = new JPanel();
		panel_9.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_9.setBackground(Color.WHITE);
		panel_9.setBounds(0, 43, 286, 50);
		panel_6.add(panel_9);
		panel_9.setLayout(null);
		
		textFieldAnalogy = new JTextField();
		textFieldAnalogy.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldAnalogy.setBackground(Color.WHITE);
		textFieldAnalogy.setFont(new Font("Times New Roman", Font.PLAIN, 24));
		textFieldAnalogy.setEditable(false);
		textFieldAnalogy.setBounds(10, 10, 266, 30);
		panel_9.add(textFieldAnalogy);
		textFieldAnalogy.setColumns(10);
		
		JLabel lblAnalogy = new JLabel("Analogy");
		lblAnalogy.setForeground(Color.BLACK);
		lblAnalogy.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnalogy.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblAnalogy.setBounds(0, 0, 286, 44);
		panel_6.add(lblAnalogy);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(255, 255, 255));
		panel_1.setBounds(71, 98, 791, 59);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		InputTextField = new JTextField();
		InputTextField.setFont(new Font("Times New Roman", Font.PLAIN, 22));
		InputTextField.setBounds(0, 0, 719, 59);
		panel_1.add(InputTextField);
		InputTextField.setColumns(10);
		
		JButton btnReset = new JButton("");
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InputTextField.setText("");
				textFieldMetaphor.setText("");
				textFieldSimile.setText("");
				textFieldAnalogy.setText("");
				textFieldViewResult.setText("");
				textArea.setText(" ");
			}
		});
		btnReset.setBackground(Color.WHITE);
		btnReset.setIcon(new ImageIcon(MeSA_GUI.class.getResource("/com/uum/cross-flat_50x50.png")));
		btnReset.setBounds(716, 0, 75, 59);
		panel_1.add(btnReset);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(220, 20, 60));
		panel.setBounds(0, 0, 1014, 74);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblMeSA = new JLabel("MeSA");
		lblMeSA.setBounds(433, 5, 190, 64);
		lblMeSA.setForeground(Color.WHITE);
		lblMeSA.setFont(new Font("Times New Roman", Font.BOLD, 55));
		lblMeSA.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblMeSA);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(MeSA_GUI.class.getResource("/com/uum/logoMeSA.jpg")));
		lblNewLabel.setBounds(389, 0, 50, 74);
		panel.add(lblNewLabel);
		
		JButton btnCheck = new JButton("");
		btnCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCheck.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean input = false;
				
				if(InputTextField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "It cannot be null! Please enter a sentence before searching.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else {
			
					metaphorAnalyzer m = new metaphorAnalyzer(textArea, textFieldMetaphor, textFieldSimile, textFieldAnalogy, textFieldViewResult);
					boolean hasMetaphor = m.detectMetaphor(InputTextField.getText());
			        
					if(hasMetaphor) {
//						textFieldViewResult.setText("Metaphor");
						
//						double calcMetaphorAccuracy = m.detectMetaphorAccuracy(InputTextField.getText());
//						NumberFormat nm = NumberFormat.getNumberInstance();
//						textFieldMetaphor.setText(nm.format(calcMetaphorAccuracy) + "%");
//						textFieldSimile.setText("0%");
//						textFieldAnalogy.setText("0%");
						
//						String metaphor = new Boolean(m.findMetaphor(sentence)).toString();
//						textArea.setText(metaphor);
					}
					
					simileAnalyzer s = new simileAnalyzer(textArea, textFieldMetaphor, textFieldSimile, textFieldAnalogy, textFieldViewResult);
				    boolean hasSimile = s.detectSimile(InputTextField.getText());
				    if(hasSimile) {
						//textFieldViewResult.setText("Simile");
						
//						double calcSimileAccuracy = s.detectSimileAccuracy(InputTextField.getText());
//						NumberFormat nm = NumberFormat.getNumberInstance();
//						textFieldMetaphor.setText("0%");
//						textFieldSimile.setText(nm.format(calcSimileAccuracy) + "%");
//						textFieldAnalogy.setText("0%");
						
//						String simile = new Boolean(s.containsSimile(sentence)).toString();
//						textArea.setText(simile);
					}
			        
				    analogyAnalyzer a = new analogyAnalyzer(textArea);
				    boolean hasAnalogy = a.detectAnalogy(InputTextField.getText());
				    if(hasAnalogy) {
						textFieldViewResult.setText("Analogy");
						
						double calcAnalogyAccuracy = a.calcAccuracy(InputTextField.getText());
						NumberFormat nm = NumberFormat.getNumberInstance();
						textFieldMetaphor.setText("0%");
						textFieldSimile.setText("0%");
						textFieldAnalogy.setText(nm.format(calcAnalogyAccuracy) + "%");
						
//						String analogy = new Boolean(a.containsAnalogy(sentence)).toString();
//						textArea.setText(analogy);
					}
				}		
			}	
		});
		btnCheck.setIcon(new ImageIcon(MeSA_GUI.class.getResource("/com/uum/search.png")));
		btnCheck.setBackground(SystemColor.control);
		btnCheck.setBounds(861, 98, 65, 59);
		contentPane.add(btnCheck);
		
		JLabel lblbackground = new JLabel("");
		lblbackground.setBackground(Color.WHITE);
		lblbackground.setIcon(new ImageIcon(MeSA_GUI.class.getResource("/com/uum/ef936cc5bd6060c8cf9571f2d357c4f8.jpg")));
		lblbackground.setBounds(0, 0, 1024, 757);
		contentPane.add(lblbackground);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setFont(new Font("Times New Roman", Font.PLAIN, 24));
		textArea_2.setBounds(72, 276, 288, 41);
		contentPane.add(textArea_2);
	}
}