import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

			DataOutputStream writer = new DataOutputStream(Files.newOutputStream(Paths.get(outputFile)));

			writer.writeInt(codeTable.size());

			for (Map.Entry<Character, String> entry : codeTable.entrySet()) {
				writer.writeChar(entry.getKey());
				writer.writeUTF(entry.getValue());
			}

			int buffer = 0;
			int bufferCounter = 0;

			for (char c : compressedContent.toString().toCharArray()) {
				buffer = (buffer << 1) | (c - '0');
				bufferCounter++;

				if (bufferCounter == 8) {
					writer.writeByte(buffer);
					buffer = 0;
					bufferCounter = 0;
				}
			}
			if (bufferCounter > 0) {
				buffer <<= (8 - bufferCounter);
			}
			writer.writeByte(bufferCounter);
			writer.writeByte(buffer);
			writer.close();

			int codeTableSize = 0;
			for (Map.Entry<Character, String> entry : codeTable.entrySet()) {
				codeTableSize += Character.BYTES; // size of the character
				codeTableSize += entry.getValue().length() * Byte.BYTES; // size of the Huffman code
			}

			int merkleRootSize = 64 * Character.BYTES;

			int paddingSize = 1;

			int totalOverheadSize = codeTableSize + merkleRootSize + paddingSize;

			System.out.println("Total overhead size: " + totalOverheadSize + " bytes");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void decryptAndDecompressFile(String inputFile, String outputFile, String encryptionKey) {
		try {
			DataInputStream reader = new DataInputStream(Files.newInputStream(Paths.get(inputFile)));
			StringBuilder compressedContent = new StringBuilder();
			Map<String, Character> codeTable = new HashMap<>();

			int codeTableSize = reader.readInt();

			for (int i = 0; i < codeTableSize; i++) {
				char key = reader.readChar();
				String value = reader.readUTF();
				codeTable.put(value, key);
			}
			int nextByte;
			int lastByteValidBits = 8;
			while (reader.available() > 2) {
				nextByte = reader.readByte() & 0xFF;
				for (int i = 7; i >= 0; i--) {
					int bit = (nextByte >> i) & 1;
					compressedContent.append(bit);
				}
			}
			if (reader.available() > 1) {
				lastByteValidBits = reader.readByte();
				nextByte = reader.readByte() & 0xFF;
				for (int i = 7; i >= 8 - lastByteValidBits; i--) {
					int bit = (nextByte >> i) & 1;
					compressedContent.append(bit);
				}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
