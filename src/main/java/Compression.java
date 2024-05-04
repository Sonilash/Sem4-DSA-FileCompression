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
			MerkleTree merkleTree = new MerkleTree();
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("\n");
				merkleTree.addTransaction(line);
			}
			reader.close();

			String merkleRoot = merkleTree.computeRoot();
			content.append(merkleRoot);
			String encryptedContent = AESEncryption.encrypt(content.toString(), encryptionKey);

			Map<Character, Integer> frequencyMap = new HashMap<>();
			assert encryptedContent != null;
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

			writer.writeInt(codeTable.size());

			for (Map.Entry<Character, String> entry : codeTable.entrySet()) {
				writer.writeChar(entry.getKey());
				writer.writeUTF(entry.getValue());
			}

			for (char c : compressedContent.toString().toCharArray()) {
				writer.writeChar(c);
			}

			writer.close();

			System.out.println("File compressed and encrypted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void decryptAndDecompressFile(String inputFile, String outputFile, String encryptionKey) {
		try {
			DataInputStream reader = new DataInputStream(new FileInputStream(inputFile));
			StringBuilder compressedContent = new StringBuilder();
			Map<String, Character> codeTable = new HashMap<>();

			int codeTableSize = reader.readInt();

			for (int i = 0; i < codeTableSize; i++) {
				char key = reader.readChar();
				String value = reader.readUTF();
				codeTable.put(value, key);
			}

			while (reader.available() > 0) {
				compressedContent.append(reader.readChar());
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

			String decryptedContent = AESEncryption.decrypt(decompressedContent.toString(), encryptionKey);

			String extractedMerkleRoot = decryptedContent.substring(decryptedContent.length() - 64);
			String originalContent = decryptedContent.substring(0, decryptedContent.length() - 64);

			MerkleTree merkleTree = new MerkleTree();

			for (String transaction : originalContent.split("\n")) {
				merkleTree.addTransaction(transaction);
			}
			String computedMerkleRoot = merkleTree.computeRoot();

			if (computedMerkleRoot.equals(extractedMerkleRoot)) {
				System.out.println("Data is verified!");
			} else {
				System.out.println("Data is not verified!");
				return;
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(originalContent);
			writer.close();

			System.out.println("File decrypted and decompressed successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}}
