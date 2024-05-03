import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String choice;
		String inputFile, outputFile, encryptionKey, encryptedFile, decryptedFile, compressedFile, decryptionKey;

		do {
			System.out.println("\n--- Menu ---");
			System.out.println("1. Compress and encrypt file");
			System.out.println("2. Decrypt and decompress file");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");
			choice = scanner.nextLine();

			switch (choice) {
				case "1":
					System.out.println("Enter the path of the input file:");
					inputFile = scanner.nextLine();

					System.out.println("Enter the path of the output file:");
					outputFile = scanner.nextLine();

					System.out.println("Enter your 16-biy encryption key:");
					encryptionKey = scanner.nextLine();

					try {
						Compression.compressAndEncryptFile(inputFile, outputFile, encryptionKey);
						System.out.println("File compressed and encrypted successfully!");
					} catch (Exception e) {
						System.out.println("An error occurred: " + e.getMessage());
					}
					break;

				case "2":
					System.out.println("Enter the path of the input file:");
					compressedFile = scanner.nextLine();

					System.out.println("Enter the path of the output file:");
					decryptedFile = scanner.nextLine();

					System.out.println("Enter your 16-bit encryption key:");
					decryptionKey = scanner.nextLine();

					try {
						Compression.decryptAndDecompressFile(compressedFile, decryptedFile, decryptionKey);
						System.out.println("File decrypted and decompressed successfully!");
					} catch (Exception e) {
						System.out.println("An error occurred: " + e.getMessage());
					}
					break;

				case "3":
					System.out.println("Exiting the program...");
					break;

				default:
					System.out.println("Invalid choice. Please enter 1, 2, or 3.");
					break;
			}
		} while (!choice.equals("5"));

		scanner.close();
	}
}