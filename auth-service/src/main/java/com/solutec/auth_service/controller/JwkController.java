package com.solutec.auth_service.controller;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

@RestController
public class JwkController {
    private final KeyPair keyPair;

    public JwkController(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey rsaKey = new RSAKey.Builder(publicKey).build();
        return Map.of("keys", List.of(rsaKey.toPublicJWK().toJSONObject()));
    }
}
