package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.application.workflow.OrganizationContactWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationAuthorizationService;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller.dto.UpdateOrganizationContactRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantFeatureFlags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrganizationContactController.
 * <p>
 * Tests authorization logic, feature flag enforcement, and HTTP response mapping.
 * Integration tests with real DB are in OrganizationContactControllerIT.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class OrganizationContactControllerTest {

    @Mock
    private OrganizationContactWorkflow workflow;

    @Mock
    private TenantFeatureFlags tenantFeatureFlags;

    @Mock
    private OrganizationAuthorizationService authorizationService;

    @InjectMocks
    private OrganizationContactController controller;

    @Test
    void getContact_whenFeatureEnabled_returns200() {
        // Given
        String orgId = "VATES-A12345678";
        OrganizationContact contact = new OrganizationContact("test@example.com");
        when(tenantFeatureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(workflow.findContactByOrganizationId(orgId)).thenReturn(Optional.of(contact));

        // When
        ResponseEntity<?> response = controller.getContact(orgId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(workflow).findContactByOrganizationId(orgId);
    }

    @Test
    void getContact_whenFeatureDisabled_returns404() {
        // Given
        String orgId = "VATES-A12345678";
        when(tenantFeatureFlags.isOrganizationContactEnabled()).thenReturn(false);

        // When
        ResponseEntity<?> response = controller.getContact(orgId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(workflow, never()).findContactByOrganizationId(any());
    }

    @Test
    void updateContact_whenUserHasWriteCapability_returns204() {
        // Given
        String orgId = "VATES-A12345678";
        UpdateOrganizationContactRequest request = new UpdateOrganizationContactRequest("test@example.com");
        when(tenantFeatureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(authorizationService.canWrite(orgId)).thenReturn(true);

        // When
        ResponseEntity<Void> response = controller.updateContact(orgId, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService).canWrite(orgId);
        verify(workflow).saveContact(eq(orgId), any(OrganizationContact.class), any());
    }

    @Test
    void updateContact_whenUserLacksWriteCapability_returns403() {
        // Given — Caso A: multi-org admin (read-only)
        String orgId = "VATES-A12345678";
        UpdateOrganizationContactRequest request = new UpdateOrganizationContactRequest("test@example.com");
        when(tenantFeatureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(authorizationService.canWrite(orgId)).thenReturn(false);

        // When
        ResponseEntity<Void> response = controller.updateContact(orgId, request);

        // Then — AC-03, ES-03: SoD enforcement
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(authorizationService).canWrite(orgId);
        verify(workflow, never()).saveContact(any(), any(), any());
    }

    @Test
    void updateContact_whenFeatureDisabled_returns404() {
        // Given
        String orgId = "VATES-A12345678";
        UpdateOrganizationContactRequest request = new UpdateOrganizationContactRequest("test@example.com");
        when(tenantFeatureFlags.isOrganizationContactEnabled()).thenReturn(false);

        // When
        ResponseEntity<Void> response = controller.updateContact(orgId, request);

        // Then — AC-04: Feature flag gating
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(authorizationService, never()).canWrite(any());
        verify(workflow, never()).saveContact(any(), any(), any());
    }

    @Test
    void updateContact_whenFeatureDisabled_doesNotCheckAuthorization() {
        // Given — Feature flag takes precedence over authorization
        String orgId = "VATES-A12345678";
        UpdateOrganizationContactRequest request = new UpdateOrganizationContactRequest("test@example.com");
        when(tenantFeatureFlags.isOrganizationContactEnabled()).thenReturn(false);

        // When
        controller.updateContact(orgId, request);

        // Then — Authorization check is skipped if feature disabled (404 before 403)
        verify(authorizationService, never()).canWrite(any());
    }
}
