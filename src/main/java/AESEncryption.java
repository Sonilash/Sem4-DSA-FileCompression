import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class AESEncryption {
	public static String encrypt(String content, String AES_KEY) throws Exception {
		if (AES_KEY.length() != 16 && AES_KEY.length() != 24 && AES_KEY.length() != 32) {
			throw new IllegalArgumentException("Invalid AES key length");
		}
		SecretKeySpec secretKey = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(AES_KEY.getBytes(StandardCharsets.UTF_8)); // using the key as IV, replace with a random IV in production
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public static String decrypt(String encryptedContent, String AES_KEY) throws Exception {
		if (AES_KEY.length() != 16 && AES_KEY.length() != 24 && AES_KEY.length() != 32) {
			throw new IllegalArgumentException("Invalid AES key length");
		}
		SecretKeySpec secretKey = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(AES_KEY.getBytes(StandardCharsets.UTF_8)); // using the key as IV, replace with a random IV in production
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}
}