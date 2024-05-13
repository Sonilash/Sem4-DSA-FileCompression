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
				if (inputFile.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Operation cancelled by user.");
					return;
				}

				String outputFile = getOutputBinFile();
				if (outputFile.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Operation cancelled by user.");
					return;
				}

				String encryptionKey = getEncryptionKey();
				if (encryptionKey.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Operation cancelled by user.");
					return;
				}

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
				if (inputFile.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Operation cancelled by user.");
					return;
				}

				String outputFile = getOutputTextFile();
				if (outputFile.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Operation cancelled by user.");
					return;
				}

				String encryptionKey = getEncryptionKey();
				if (encryptionKey.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Operation cancelled by user.");
					return;
				}

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

		frame.add(compressButton);
		frame.add(decompressButton);
		frame.add(exitButton);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(500, 300);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		frame.setVisible(true);
	}

	public static String getInputTextFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Select Input File");

		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files",
				"txt", "text", "csv", "tsv", "log");
		jfc.setFileFilter(filter);

		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		}
		return "";
	}

	public static String getOutputBinFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogTitle("Select Output Directory");
		int returnValue = jfc.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String directoryPath = jfc.getSelectedFile().getAbsolutePath();
			String fileName = JOptionPane.showInputDialog("Enter the output file name:");
			if (fileName == null) {
				return "";
			}
			return directoryPath + File.separator + fileName + ".bin";
		}
		return "";
	}

	public static String getEncryptionKey() {
		String key = JOptionPane.showInputDialog("Enter your 16-bit encryption key:");
		if (key == null) {
			return "";
		}
		return key;
	}

	public static String getInputBinaryFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Select Input Binary File");

		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary Files", "bin");
		jfc.setFileFilter(filter);

		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		}
		return "";
	}

	public static String getOutputTextFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogTitle("Select Output Directory");
		int returnValue = jfc.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String directoryPath = jfc.getSelectedFile().getAbsolutePath();
			String fileName = JOptionPane.showInputDialog("Enter the output file name:");
			if (fileName == null) {
				return "";
			}
			if (!fileName.toLowerCase().endsWith(".txt")) {
				fileName += ".txt";
			}
			return directoryPath + File.separator + fileName;
		}
		return "";
	}
}
