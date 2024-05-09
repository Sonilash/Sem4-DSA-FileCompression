import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String choice;
		String inputFile, outputFile, encryptionKey, decryptionKey;

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
//					inputFile = scanner.nextLine();
					inputFile = "D:\\Coding\\ASEB\\Sem-4\\DSA Project\\File_compression_and_encryption\\src\\main\\resources\\sample.txt";

					System.out.println("Enter the path of the output file:");
//					outputFile = scanner.nextLine();
					outputFile = "D:\\Coding\\ASEB\\Sem-4\\DSA Project\\File_compression_and_encryption\\src\\main\\resources\\compressed.bin";

					System.out.println("Enter your 16-bit encryption key:");
//					encryptionKey = scanner.nextLine();
					encryptionKey = "1234567890123456";

					try {
						Compression.compressAndEncryptFile(inputFile, outputFile, encryptionKey);
//						AESEncryption.encryptFile(inputFile, encryptionKey);
						System.out.println("File compressed and encrypted successfully!");
					} catch (Exception e) {
						System.out.println("An error occurred: " + e.getMessage());
					}
					break;

				case "2":
					System.out.println("Enter the path of the input file:");
//					inputFile = scanner.nextLine();
					inputFile = "D:\\Coding\\ASEB\\Sem-4\\DSA Project\\File_compression_and_encryption\\src\\main\\resources\\compressed.bin";


					System.out.println("Enter the path of the output file:");
//					outputFile = scanner.nextLine();
					outputFile = "D:\\Coding\\ASEB\\Sem-4\\DSA Project\\File_compression_and_encryption\\src\\main\\resources\\decompressed.txt";

					System.out.println("Enter your 16-bit encryption key:");
//					decryptionKey = scanner.nextLine();
					decryptionKey = "1234567890123456";

					try {
						Compression.decryptAndDecompressFile(inputFile, outputFile, decryptionKey);
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
		} while (!choice.equals("3"));

		scanner.close();
	}
}