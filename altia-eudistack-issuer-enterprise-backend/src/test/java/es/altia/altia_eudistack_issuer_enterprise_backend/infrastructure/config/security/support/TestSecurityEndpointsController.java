package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.support;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;

@RestController
public class TestSecurityEndpointsController {

    @GetMapping("/health")
    ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(DATA_ACQUISITION_PATH)
    ResponseEntity<Void> acquire() {
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = DATA_ACQUISITION_PATH, method = RequestMethod.OPTIONS)
    ResponseEntity<Void> acquireOptions() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forbidden")
    ResponseEntity<Void> forbidden() {
        return ResponseEntity.ok().build();
    }
}