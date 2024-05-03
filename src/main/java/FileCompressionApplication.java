import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileCompressionApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		VBox vbox = new VBox(8);
		vbox.setPadding(new Insets(10));

		TextField encryptionKeyField = new TextField();
		encryptionKeyField.setPromptText("Enter your 16-bit encryption key");

		DirectoryChooser directoryChooser = new DirectoryChooser();

		Button inputDirectoryButton = new Button("Select Input File");
		inputDirectoryButton.setOnAction(e -> {
			File selectedFile = directoryChooser.showDialog(primaryStage);
			if (selectedFile != null) {
				inputDirectoryButton.setText(selectedFile.getPath());
			}
		});

		FileChooser fileChooser = new FileChooser();

		Button outputDirectoryButton = new Button("Select Output File");
		outputDirectoryButton.setOnAction(e -> {
			fileChooser.setInitialFileName("outputFileName");
			File selectedFile = fileChooser.showSaveDialog(primaryStage);
			if (selectedFile != null) {
				outputDirectoryButton.setText(selectedFile.getPath());
			}
		});

		Button compressAndEncryptButton = new Button("Compress and Encrypt File");
		compressAndEncryptButton.setOnAction(e -> {
			String inputFile = inputDirectoryButton.getText();
			String outputFile = outputDirectoryButton.getText();
			String encryptionKey = encryptionKeyField.getText();
			try {
				Compression.compressAndEncryptFile(inputFile, outputFile, encryptionKey);
				System.out.println("File compressed and encrypted successfully!");
			} catch (Exception ex) {
				System.out.println("An error occurred: " + ex.getMessage());
			}
		});

		Button decryptAndDecompressButton = new Button("Decrypt and Decompress File");
		decryptAndDecompressButton.setOnAction(e -> {
			String inputFile = inputDirectoryButton.getText();
			String outputFile = outputDirectoryButton.getText();
			String encryptionKey = encryptionKeyField.getText();
			try {
				Compression.decryptAndDecompressFile(inputFile, outputFile, encryptionKey);
				System.out.println("File decrypted and decompressed successfully!");
			} catch (Exception ex) {
				System.out.println("An error occurred: " + ex.getMessage());
			}
		});

		vbox.getChildren().addAll(
				new Label("Input File:"),
				inputDirectoryButton,
				new Label("Output Directory:"),
				outputDirectoryButton,
				new Label("Encryption Key:"),
				encryptionKeyField,
				compressAndEncryptButton,
				decryptAndDecompressButton
		);

		Scene scene = new Scene(vbox, 300, 200);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
