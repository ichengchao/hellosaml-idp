package name.chengchao.hellosaml.util;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;

import name.chengchao.hellosaml.common.Algo;
import name.chengchao.hellosaml.common.CanonicalizationMethod;
import name.chengchao.hellosaml.common.CommonConstants;
import name.chengchao.hellosaml.common.DigestMethod;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.opensaml.xmlsec.signature.support.Signer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@org.springframework.stereotype.Component
public class OpenSamlImplementation {

    private final AtomicBoolean hasInitCompleted = new AtomicBoolean(false);
    private BasicParserPool parserPool;
    private static RandomIdentifierGenerationStrategy secureRandomIdGenerator;

    static {
        secureRandomIdGenerator = new RandomIdentifierGenerationStrategy();
    }

    @PostConstruct
    public void springInit() {
        init();
    }

    public OpenSamlImplementation() {
        this(Clock.systemUTC());
    }

    public OpenSamlImplementation(Clock time) {
        this.parserPool = new BasicParserPool();
    }

    public String transformSAMLObject2String(SAMLObject samlObject) throws MarshallingException {
        return SerializeSupport.nodeToString(getMarshallerFactory().getMarshaller(samlObject).marshall(samlObject));
    }

    private OpenSamlImplementation init() {
        if (!hasInitCompleted.get()) {
            performInit();
        }
        return this;
    }

    private synchronized void performInit() {
        if (hasInitCompleted.compareAndSet(false, true)) {
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            bootstrap();
        }
    }

    public MarshallerFactory getMarshallerFactory() {
        return XMLObjectProviderRegistrySupport.getMarshallerFactory();
    }

    public Credential getSelfCredential() {
        return IDPCredentials.getCredential(CommonConstants.PASSWORD);
    }

    public void signObject(SignableSAMLObject signable, Algo algorithm, DigestMethod digest) {

        try {
            XMLObjectBuilder<Signature> signatureBuilder = (XMLObjectBuilder<org.opensaml.xmlsec.signature.Signature>) getBuilderFactory()
                    .getBuilder(org.opensaml.xmlsec.signature.Signature.DEFAULT_ELEMENT_NAME);
            org.opensaml.xmlsec.signature.Signature signature = signatureBuilder
                    .buildObject(org.opensaml.xmlsec.signature.Signature.DEFAULT_ELEMENT_NAME);

            signable.setSignature(signature);

            SignatureSigningParameters parameters = new SignatureSigningParameters();
            parameters.setSigningCredential(IDPCredentials.getCredential(CommonConstants.PASSWORD));
            parameters.setKeyInfoGenerator(getKeyInfoGenerator(IDPCredentials.getCredential(CommonConstants.PASSWORD)));
            parameters.setSignatureAlgorithm(algorithm.toString());
            parameters.setSignatureReferenceDigestMethod(digest.toString());
            parameters.setSignatureCanonicalizationAlgorithm(
                    CanonicalizationMethod.ALGO_ID_C14N_EXCL_OMIT_COMMENTS.toString());

            SignatureSupport.prepareSignatureParams(signature, parameters);
            Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signable);
            marshaller.marshall(signable);
            Signer.signObject(signature);
        } catch (SecurityException | MarshallingException | SignatureException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {

        }
    }

    public KeyInfoGenerator getKeyInfoGenerator(Credential credential) {
        NamedKeyInfoGeneratorManager manager = DefaultSecurityConfigurationBootstrap
                .buildBasicKeyInfoGeneratorManager();
        return manager.getDefaultManager().getFactory(credential).newInstance();
    }

    public XMLObjectBuilderFactory getBuilderFactory() {
        return XMLObjectProviderRegistrySupport.getBuilderFactory();
    }

    protected void bootstrap() {
        // configure default values
        // maxPoolSize = 5;
        parserPool.setMaxPoolSize(50);
        // coalescing = true;
        parserPool.setCoalescing(true);
        // expandEntityReferences = false;
        parserPool.setExpandEntityReferences(false);
        // ignoreComments = true;
        parserPool.setIgnoreComments(true);
        // ignoreElementContentWhitespace = true;
        parserPool.setIgnoreElementContentWhitespace(true);
        // namespaceAware = true;
        parserPool.setNamespaceAware(true);
        // schema = null;
        parserPool.setSchema(null);
        // dtdValidating = false;
        parserPool.setDTDValidating(false);
        // xincludeAware = false;
        parserPool.setXincludeAware(false);

        Map<String, Object> builderAttributes = new HashMap<>();
        parserPool.setBuilderAttributes(builderAttributes);

        Map<String, Boolean> parserBuilderFeatures = new HashMap<>();
        parserBuilderFeatures.put("http://apache.org/xml/features/disallow-doctype-decl", TRUE);
        parserBuilderFeatures.put("http://javax.xml.XMLConstants/feature/secure-processing", TRUE);
        parserBuilderFeatures.put("http://xml.org/sax/features/external-general-entities", FALSE);
        parserBuilderFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", FALSE);
        parserBuilderFeatures.put("http://xml.org/sax/features/external-parameter-entities", FALSE);
        parserBuilderFeatures.put("http://apache.org/xml/features/dom/defer-node-expansion", FALSE);
        parserPool.setBuilderFeatures(parserBuilderFeatures);

        try {
            parserPool.initialize();
        } catch (ComponentInitializationException x) {
            throw new RuntimeException("Unable to initialize OpenSaml v3 ParserPool", x);
        }

        try {
            InitializationService.initialize();
        } catch (InitializationException e) {
            throw new RuntimeException("Unable to initialize OpenSaml v3", e);
        }

        XMLObjectProviderRegistry registry;
        synchronized (ConfigurationService.class) {
            registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
            if (registry == null) {
                registry = new XMLObjectProviderRegistry();
                ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
            }
        }
        registry.setParserPool(parserPool);
    }

    public static String generateSecureRandomId() {
        return secureRandomIdGenerator.generateIdentifier();
    }

    public <T> T buildSAMLObject(final Class<T> clazz) {
        T object = null;
        try {
            QName defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
            object = (T) getBuilderFactory().getBuilder(defaultElementName).buildObject(defaultElementName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not create SAML object", e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not create SAML object", e);
        }

        return object;
    }

}
