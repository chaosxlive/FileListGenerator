package temp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
	private static int layer = 0;
	private static BufferedWriter writer;
	private static StringBuilder stringBuilder;
	// Window Objects
	private static JFrame frame;
	private static JPanel northPanel;
	private static JButton locationRoot;
	private static boolean isPathEmpty = true;
	private static JTextPane pathRoot;
	private static JFileChooser fileChooser;
	private static JButton runListing;
	private static JScrollPane scrollPane;
	private static JTextArea resultArea;
	private static JPanel southPanel;
	private static JCheckBox isResultEditable;
	private static JButton export;
	private static JFileChooser exportPath;
	
	public static void main(String[] args) throws IOException {
		// Window Main Frames
		Main.frame = new JFrame("File List Generator");
		Main.frame.setSize(600, 800);
		Main.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Main.frame.setLayout(new BorderLayout());
		
		// Window North Area
		Main.northPanel = new JPanel();
		Main.frame.add(Main.northPanel, BorderLayout.NORTH);
		Main.northPanel.setLayout(new BorderLayout());
		// // Select-Root Button
		Main.locationRoot = new JButton("Select Root Directory...");
		Main.locationRoot.setHorizontalAlignment(SwingConstants.LEFT);
		Main.fileChooser = new JFileChooser();
		Main.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		Main.locationRoot.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int option = Main.fileChooser.showOpenDialog(frame);
				if(option == JFileChooser.APPROVE_OPTION) {
					Main.pathRoot.setText(Main.fileChooser.getSelectedFile().toString());
					Main.isPathEmpty = false;
					Main.runListing.setEnabled(true);
				}
			}
		});
		// // Select-Root Text
		Main.pathRoot = new JTextPane();
		Main.pathRoot.setText("Or input the path here");
		Main.pathRoot.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if(Main.isPathEmpty) {
					Main.pathRoot.setText("");
					Main.isPathEmpty = false;
					Main.runListing.setEnabled(true);
				}
			}
		});
		Main.pathRoot.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				if(Main.pathRoot.getText().length() == 0) {
					Main.isPathEmpty = true;
					Main.pathRoot.setText("Or input the path here");
					Main.runListing.setEnabled(false);
				}
			}
		});
		Main.northPanel.add(pathRoot, BorderLayout.CENTER);
		Main.northPanel.add(locationRoot, BorderLayout.NORTH);
		// // Run Button
		Main.runListing = new JButton("Start");
		Main.runListing.setEnabled(false);
		Main.runListing.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(Main.pathRoot.getText().length() == 0) {
					return;
				}
				Main processing = new Main();
				try {
					processing.startListing(Main.pathRoot.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		Main.northPanel.add(runListing, BorderLayout.EAST);
		
		// Window Center Area
		Main.resultArea = new JTextArea();
		Main.resultArea.setEditable(false);
		Main.scrollPane = new JScrollPane(Main.resultArea);
		Main.frame.add(Main.scrollPane, BorderLayout.CENTER);
		
		// Window South Area
		Main.isResultEditable = new JCheckBox("Enable text typing");
		Main.isResultEditable.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(Main.isResultEditable.isSelected()) {
					Main.resultArea.setEditable(true);
				}
				else {
					Main.resultArea.setEditable(false);
				}
			}
		});
		Main.export = new JButton("Export...");
		Main.exportPath = new JFileChooser();
		Main.exportPath.setFileSelectionMode(JFileChooser.FILES_ONLY);
		Main.exportPath.setSelectedFile(new File("./output.txt"));
		Main.exportPath.setFileFilter(new FileNameExtensionFilter("txt", "txt"));
		Main.export.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int option = Main.exportPath.showSaveDialog(frame);
				if(option == JFileChooser.APPROVE_OPTION) {
					try {
						Main.writer = new BufferedWriter(new FileWriter(Main.exportPath.getSelectedFile()));
						Main.writer.write(Main.resultArea.getText());
						Main.writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		Main.southPanel = new JPanel();
		Main.southPanel.setLayout(new FlowLayout());
		Main.southPanel.add(Main.isResultEditable);
		Main.southPanel.add(Main.export);
		Main.frame.add(Main.southPanel, BorderLayout.SOUTH);
		
		// Window enable
		Main.frame.setVisible(true);
	}
	
	private void startListing(String path) throws IOException {
		Main.stringBuilder = new StringBuilder();
		loop(path);
		Main.resultArea.setText(Main.stringBuilder.toString());
	}
	
	private void loop(String path) throws IOException {
		File file = new File(path);
		if(!file.exists()) return;
		String[] paths = file.list();
		for(String p : paths) {
			for(int i = 0; i < Main.layer; i++) {
				Main.stringBuilder.append("│");
			}
			Main.stringBuilder.append("├");
			File tester = new File(path + "\\" + p);
			if(tester.isDirectory()) {
				Main.layer++;
				String[] pathName = path.split("\\\\");
				Main.stringBuilder.append(pathName[pathName.length - 1] + "\n");
				loop(path + "\\" +p);
				Main.layer--;
			}
			else {
				Main.stringBuilder.append(p + "\n");
			}
		}
		for(int i = 0; i < Main.layer; i++) {
			Main.stringBuilder.append("│");
		}
		Main.stringBuilder.append("\n");
	}
}
