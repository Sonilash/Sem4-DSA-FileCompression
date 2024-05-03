import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {
	public static String encrypt(String content, String AES_KEY) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String encryptedContent, String AES_KEY) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void encryptFile(String inputFile, String outputFile, String encryptionKey) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
			reader.close();

			String encryptedContent = encrypt(content.toString(), encryptionKey);

			DataOutputStream writer = new DataOutputStream(new FileOutputStream(outputFile));
			writer.writeUTF(encryptedContent);
			writer.close();

			System.out.println("File encrypted successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void decryptFile(String inputFile, String outputFile, String encryptionKey) {
		try {
			DataInputStream reader = new DataInputStream(new FileInputStream(inputFile));
			String encryptedContent = reader.readUTF();
			reader.close();

			String decryptedContent = decrypt(encryptedContent, encryptionKey);

			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(decryptedContent);
			writer.close();

			System.out.println("File decrypted successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}