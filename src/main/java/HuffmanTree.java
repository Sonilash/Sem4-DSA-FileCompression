import java.util.Map;

public class HuffmanTree {
	public static void buildCodeTable(HuffmanNode node, String code, Map<Character, String> codeTable) {
		if (node == null) {
			return;
		}

		if (node.left == null && node.right == null) {
			codeTable.put(node.data, code);
		}

		buildCodeTable(node.left, code + "0", codeTable);
		buildCodeTable(node.right, code + "1", codeTable);
	}

	// Other Huffman tree related methods can go here
}