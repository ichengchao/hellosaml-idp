package name.chengchao.hellosaml.common;

public enum Algo {
    RSA_SHA256("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");

    private final String urn;

    Algo(String urn) {
        this.urn = urn;
    }

    @Override
    public String toString() {
        return urn;
    }
}