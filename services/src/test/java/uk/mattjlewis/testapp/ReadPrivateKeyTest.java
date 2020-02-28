package uk.mattjlewis.testapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import org.junit.jupiter.api.Test;

import uk.mattjlewis.testapp.util.PrivateKeyUtils;

@SuppressWarnings("static-method")
public class ReadPrivateKeyTest {
	@Test
	public void readPrivateKey() throws GeneralSecurityException, IOException {
		PrivateKey pk = PrivateKeyUtils.readPrivateKey("/privateKey.pem");
		assertNotNull(pk);
		assertEquals("RSA", pk.getAlgorithm());
		assertEquals("PKCS#8", pk.getFormat());
	}
}
