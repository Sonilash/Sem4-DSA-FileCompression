import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

public class MainGUI {
	public static void main(String[] args) {
		JFrame frame = new JFrame("File Compression and Encryption");

		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		JButton compressButton = new JButton("Compress Text File");
		JButton decompressButton = new JButton("Decompress Binary file");
		JButton exitButton = new JButton("Exit");

		Dimension maxDimension = new Dimension(Integer.MAX_VALUE, compressButton.getPreferredSize().height);
		compressButton.setMaximumSize(maxDimension);
		decompressButton.setMaximumSize(maxDimension);
		exitButton.setMaximumSize(maxDimension);



		compressButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputFile = getInputTextFile();
				String outputFile = getOutputBinFile();
				String encryptionKey = getEncryptionKey();

				try {
					Compression.compressAndEncryptFile(inputFile, outputFile, encryptionKey);
					JOptionPane.showMessageDialog(frame, "File compressed and encrypted successfully!");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage());
				}
			}
		});

		decompressButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputFile = getInputBinaryFile();
				String outputFile = getOutputTextFile();
				String encryptionKey = getEncryptionKey();

				try {
					Compression.decryptAndDecompressFile(inputFile, outputFile, encryptionKey);
					JOptionPane.showMessageDialog(frame, "File decrypted and decompressed successfully!");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage());
				}
			}
		});

		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Add buttons to JFrame
		frame.add(compressButton);
		frame.add(decompressButton);
		frame.add(exitButton);

		// Set the default operation to exit when the window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the size of the JFrame
		frame.setSize(300, 200);

		// Make the JFrame visible
		frame.setVisible(true);
	}

	public static String getInputTextFile(){
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Select Input File");

		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		jfc.setFileFilter(filter);

		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	public static String getOutputBinFile(){
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogTitle("Select Output Directory");
		int returnValue = jfc.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String directoryPath = jfc.getSelectedFile().getAbsolutePath();
			String fileName = JOptionPane.showInputDialog("Enter the output file name:");
			return directoryPath + File.separator + fileName + ".bin";
		}
		return null;
	}

	public static String getEncryptionKey(){
		return JOptionPane.showInputDialog("Enter your 16-bit encryption key:");
	}

	public static String getInputBinaryFile(){
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Select Input Binary File");

		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("BIN FILES", "bin");
		jfc.setFileFilter(filter);

		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	public static String getOutputTextFile(){
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogTitle("Select Output Directory");
		int returnValue = jfc.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String directoryPath = jfc.getSelectedFile().getAbsolutePath();
			String fileName = JOptionPane.showInputDialog("Enter the output file name:");
			if(!fileName.toLowerCase().endsWith(".txt")) {
				fileName += ".txt";
			}
			return directoryPath + File.separator + fileName;
		}
		return null;
	}
}
