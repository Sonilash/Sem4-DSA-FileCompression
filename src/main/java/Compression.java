import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Compression {
	public static void compressAndEncryptFile(String inputFile, String outputFile, String encryptionKey) throws Exception {

		StringBuilder content;
		MerkleTree merkleTree;
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			content = new StringBuilder();
			String line;
			merkleTree = new MerkleTree();
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("\n");
				merkleTree.addTransaction(line);
			}
		}

		String merkleRoot = merkleTree.computeRoot();
		content.append(merkleRoot);
		String contentString = content.toString();
		Map<Character, Integer> frequencyMap = new HashMap<>();
		assert contentString != null;
		for (char c : contentString.toCharArray()) {
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
		for (char c : contentString.toCharArray()) {
			compressedContent.append(codeTable.get(c));
		}

		int lastByteValidBits = compressedContent.length() % 8;
		if (lastByteValidBits != 0) {
			int padding = 8 - compressedContent.length() % 8;
			for (int i = 0; i < padding; i++) {
				compressedContent.append("0");
			}
		}
		String lastByteSizeBinary = String.format("%8s", Integer.toBinaryString(lastByteValidBits)).replace(' ', '0');
		compressedContent.append(lastByteSizeBinary);

		contentString = binaryToAscii(compressedContent.toString());
		contentString = AESEncryption.encrypt(contentString, encryptionKey);
		contentString = asciiToBinary(contentString);


		try (DataOutputStream writer = new DataOutputStream(Files.newOutputStream(Paths.get(outputFile)))) {

			writer.writeInt(codeTable.size());

			for (Map.Entry<Character, String> entry : codeTable.entrySet()) {
				writer.writeChar(entry.getKey());
				writer.writeUTF(entry.getValue());
			}

			int buffer = 0;
			int bufferCounter = 0;
			for (char c : contentString.toCharArray()) {
				buffer = (buffer << 1) | (c - '0');
				bufferCounter++;

				if (bufferCounter == 8) {
					writer.writeByte(buffer);
					buffer = 0;
					bufferCounter = 0;
				}
			}
		}
	}

	public static void decryptAndDecompressFile(String inputFile, String outputFile, String encryptionKey) throws Exception {

		Map<String, Character> codeTable;
		StringBuilder compressedContent;
		try (DataInputStream reader = new DataInputStream(Files.newInputStream(Paths.get(inputFile)))) {
			codeTable = new HashMap<>();

			int codeTableSize = reader.readInt();

			for (int i = 0; i < codeTableSize; i++) {
				char key = reader.readChar();
				String value = reader.readUTF();
				codeTable.put(value, key);
			}

			compressedContent = new StringBuilder();
			int nextByte;
			while (reader.available() > 0) {
				nextByte = reader.readByte() & 0xFF;
				for (int i = 7; i >= 0; i--) {
					int bit = (nextByte >> i) & 1;
					compressedContent.append(bit);
				}
			}
		}

		String compressedString = compressedContent.toString();
		compressedString = binaryToAscii(compressedString);
		compressedString = AESEncryption.decrypt(compressedString, encryptionKey);
		compressedString = asciiToBinary(compressedString);

		int lastByteValidBits = Integer.parseInt(compressedString.substring(compressedString.length() - 8), 2);
		compressedString = compressedString.substring(0, compressedString.length() - 16 + lastByteValidBits);

		StringBuilder decompressedContent = new StringBuilder();
		StringBuilder currentCode = new StringBuilder();

		for (char c : compressedString.toString().toCharArray()) {
			currentCode.append(c);
			if (codeTable.containsKey(currentCode.toString())) {
				decompressedContent.append(codeTable.get(currentCode.toString()));
				currentCode.setLength(0);
			}
		}

		String decompressedString = decompressedContent.toString();
		String extractedMerkleRoot = decompressedString.substring(decompressedString.length() - 64);
		decompressedString = decompressedString.substring(0, decompressedString.length() - 64);

		MerkleTree merkleTree = new MerkleTree();

		for (String transaction : decompressedString.split("\n")) {
			merkleTree.addTransaction(transaction);
		}
		String computedMerkleRoot = merkleTree.computeRoot();

		if (computedMerkleRoot.equals(extractedMerkleRoot)) {
			System.out.println("Data is verified!");
		} else {
			System.out.println("Data is not verified!");
			return;
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			writer.write(decompressedString);
		}
	}

	public static String binaryToAscii(String binary) {
		byte[] bytes = new byte[binary.length() / 8];
		for (int i = 0; i < binary.length(); i += 8) {
			String binaryChunk = binary.substring(i, i + 8);
			bytes[i / 8] = (byte) Integer.parseInt(binaryChunk, 2);
		}
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static String asciiToBinary(String ascii) {
		byte[] bytes = Base64.getDecoder().decode(ascii);
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			String binaryString = Integer.toBinaryString(b & 0xFF);
			binaryString = String.format("%8s", binaryString).replace(' ', '0');
			binary.append(binaryString);
		}
		return binary.toString();
	}
}
