import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Compression {
	public static void compressAndEncryptFile(String inputFile, String outputFile, String encryptionKey) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
			reader.close();

			String encryptedContent = AESEncryption.encrypt(content.toString(), encryptionKey);

			Map<Character, Integer> frequencyMap = new HashMap<>();
			for (char c : encryptedContent.toCharArray()) {
				frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
			}

			PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
			for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
				HuffmanNode node = new HuffmanNode();
				node.data = entry.getKey();
				node.frequency = entry.getValue();
				node.left = null;
				node.right = null;
				pq.add(node);
			}

			while (pq.size() > 1) {
				HuffmanNode left = pq.poll();
				HuffmanNode right = pq.poll();
				HuffmanNode parent = new HuffmanNode();
				parent.frequency = left.frequency + right.frequency;
				parent.left = left;
				parent.right = right;
				pq.add(parent);
			}

			HuffmanNode root = pq.peek();

			Map<Character, String> codeTable = new HashMap<>();
			HuffmanTree.buildCodeTable(root, "", codeTable);

			StringBuilder compressedContent = new StringBuilder();
			for (char c : encryptedContent.toCharArray()) {
				compressedContent.append(codeTable.get(c));
			}

			DataOutputStream writer = new DataOutputStream(new FileOutputStream(outputFile));

			// Write the size of the code table
			writer.writeInt(codeTable.size());

			// Write the code table
			for (Map.Entry<Character, String> entry : codeTable.entrySet()) {
				writer.writeChar(entry.getKey());
				writer.writeUTF(entry.getValue());
			}

			// Write the separator
			writer.writeUTF("END_OF_CODE_TABLE");

			// Write the compressed content
			for (char c : compressedContent.toString().toCharArray()) {
				writer.writeByte(Integer.parseInt(String.valueOf(c), 2));
			}

			writer.close();

			System.out.println("File compressed and encrypted successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void decryptAndDecompressFile(String inputFile, String outputFile, String encryptionKey) {
		try {
			// Read compressed file
			DataInputStream reader = new DataInputStream(new FileInputStream(inputFile));
			StringBuilder compressedContent = new StringBuilder();
			Map<String, Character> codeTable = new HashMap<>();
			String line;
			boolean readingCodes = true;

			// Read the size of the code table
			int codeTableSize = reader.readInt();

			// Read the code table
			for (int i = 0; i < codeTableSize; i++) {
				char key = reader.readChar();
				String value = reader.readUTF();
				codeTable.put(value, key);
			}

			// Read the separator
			String separator = reader.readUTF();

			// Read the compressed content
			while (reader.available() > 0) {
				char c = (char) reader.readByte();
				compressedContent.append(Integer.toBinaryString(c & 0xFF));
			}
			reader.close();

			// Decompress content
			StringBuilder decompressedContent = new StringBuilder();
			StringBuilder currentCode = new StringBuilder();
			for (char c : compressedContent.toString().toCharArray()) {
				currentCode.append(c);
				if (codeTable.containsKey(currentCode.toString())) {
					decompressedContent.append(codeTable.get(currentCode.toString()));
					currentCode = new StringBuilder();
				}
			}

			// Decrypt content
			String decryptedContent = AESEncryption.decrypt(decompressedContent.toString(), encryptionKey);

			// Write decrypted content to output file
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(decryptedContent);
			writer.close();

			System.out.println("File decrypted and decompressed successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void compressBinary(String inputFile, String outputFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
			reader.close();

			Map<Character, Integer> frequencyMap = new HashMap<>();
			for (char c : content.toString().toCharArray()) {
				frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
			}

			PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
			for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
				HuffmanNode node = new HuffmanNode();
				node.data = entry.getKey();
				node.frequency = entry.getValue();
				node.left = null;
				node.right = null;
				pq.add(node);
			}

			while (pq.size() > 1) {
				HuffmanNode left = pq.poll();
				HuffmanNode right = pq.poll();
				HuffmanNode parent = new HuffmanNode();
				parent.frequency = left.frequency + right.frequency;
				parent.left = left;
				parent.right = right;
				pq.add(parent);
			}

			HuffmanNode root = pq.peek();

			Map<Character, String> codeTable = new HashMap<>();
			HuffmanTree.buildCodeTable(root, "", codeTable);

			StringBuilder compressedContent = new StringBuilder();
			for (char c : content.toString().toCharArray()) {
				compressedContent.append(codeTable.get(c));
			}

			DataOutputStream writer = new DataOutputStream(new FileOutputStream(outputFile));
			for (char c : compressedContent.toString().toCharArray()) {
				writer.writeByte(Integer.parseInt(String.valueOf(c), 2));
			}
			writer.close();

			System.out.println("File compressed successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void decompressBinary(String inputFile, String outputFile) {
		try {
			DataInputStream reader = new DataInputStream(new FileInputStream(inputFile));
			StringBuilder compressedContent = new StringBuilder();
			Map<String, Character> codeTable = new HashMap<>();
			String line;
			boolean readingCodes = true;
			while (reader.available() > 0) {
				char c = (char) reader.readByte();
				compressedContent.append(Integer.toBinaryString(c & 0xFF));
			}
			reader.close();

			StringBuilder decompressedContent = new StringBuilder();
			StringBuilder currentCode = new StringBuilder();
			for (char c : compressedContent.toString().toCharArray()) {
				currentCode.append(c);
				if (codeTable.containsKey(currentCode.toString())) {
					decompressedContent.append(codeTable.get(currentCode.toString()));
					currentCode = new StringBuilder();
				}
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(decompressedContent.toString());
			writer.close();

			System.out.println("File decompressed successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
