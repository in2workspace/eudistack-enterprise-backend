package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteSignaturePropertiesTest {

    public static final String SIGNATURE_REMOTE_TYPE_SERVER = "server";

    @Test
    void testRemoteSignatureProperties() {
        RemoteSignatureProperties.Paths paths = new RemoteSignatureProperties.Paths("signPath");
        RemoteSignatureProperties remoteSignatureProperties = new RemoteSignatureProperties(SIGNATURE_REMOTE_TYPE_SERVER,"domain", paths, "clientId", "clientSecret", "credentialId", "credentialPassword", null);

        assertEquals(SIGNATURE_REMOTE_TYPE_SERVER, remoteSignatureProperties.type());
        assertEquals("domain", remoteSignatureProperties.url());
        assertEquals(paths, remoteSignatureProperties.paths());
        assertEquals("clientId", remoteSignatureProperties.clientId());
        assertEquals("clientSecret", remoteSignatureProperties.clientSecret());
        assertEquals("credentialId", remoteSignatureProperties.credentialId());
        assertEquals("credentialPassword", remoteSignatureProperties.credentialPassword());
    }
}