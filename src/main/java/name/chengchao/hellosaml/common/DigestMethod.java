
package name.chengchao.hellosaml.common;

public enum DigestMethod {


	/**
	 * The <a href="http://www.w3.org/2000/09/xmldsig#sha1">
	 * SHA1</a> digest method algorithm URI.
	 */
	SHA1(javax.xml.crypto.dsig.DigestMethod.SHA1),

	/**
	 * The <a href="http://www.w3.org/2001/04/xmlenc#sha256">
	 * SHA256</a> digest method algorithm URI.
	 */
	SHA256(javax.xml.crypto.dsig.DigestMethod.SHA256),

	/**
	 * The <a href="http://www.w3.org/2001/04/xmlenc#sha512">
	 * SHA512</a> digest method algorithm URI.
	 */
	SHA512(javax.xml.crypto.dsig.DigestMethod.SHA512),

	/**
	 * The <a href="http://www.w3.org/2001/04/xmlenc#ripemd160">
	 * RIPEMD-160</a> digest method algorithm URI.
	 */
	RIPEMD160(javax.xml.crypto.dsig.DigestMethod.RIPEMD160);

	private final String urn;

	DigestMethod(String urn) {
		this.urn = urn;
	}

	public static DigestMethod fromUrn(String digestAlgorithm) {
		for (DigestMethod m : values()) {
			if (m.urn.equalsIgnoreCase(digestAlgorithm)) {
				return m;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return urn;
	}
}
