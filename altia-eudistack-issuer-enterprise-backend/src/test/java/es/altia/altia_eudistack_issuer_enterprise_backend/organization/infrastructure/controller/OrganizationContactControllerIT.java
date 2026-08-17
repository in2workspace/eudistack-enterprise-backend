package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationContactService;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantFeatureFlags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link OrganizationContactController}.
 *
 * @since EUD-226 (Task 16)
 */
@WebMvcTest(OrganizationContactController.class)
@DisplayName("OrganizationContactController Integration Tests")
class OrganizationContactControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private es.altia.altia_eudistack_issuer_enterprise_backend.organization.application.workflow.OrganizationContactWorkflow workflow;

    @MockBean
    private TenantFeatureFlags featureFlags;

    @MockBean
    private es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationAuthorizationService authorizationService;

    private static final String ORG_ID = "org-123";
    private static final String VALID_EMAIL = "contact@example.com";

    @Test
    @DisplayName("GET /organizations/{id}/contact returns 200 with email when contact exists (AC-01)")
    void getContact_existingContact_returns200() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(workflow.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.of(new OrganizationContact(VALID_EMAIL)));

        // When & Then
        mockMvc.perform(get("/api/v1/organizations/{id}/contact", ORG_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));

        verify(workflow).findContactByOrganizationId(ORG_ID);
    }

    @Test
    @DisplayName("GET /organizations/{id}/contact returns 200 with null when no contact (EC-01)")
    void getContact_noContact_returns200WithNull() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(workflow.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/organizations/{id}/contact", ORG_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").doesNotExist());

        verify(workflow).findContactByOrganizationId(ORG_ID);
    }

    @Test
    @DisplayName("GET /organizations/{id}/contact returns 404 when feature disabled (AC-04)")
    void getContact_featureDisabled_returns404() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/v1/organizations/{id}/contact", ORG_ID))
                .andExpect(status().isNotFound());

        verifyNoInteractions(workflow);
    }

    @Test
    @DisplayName("PUT /organizations/{id}/contact returns 204 on success (AC-02)")
    void updateContact_validEmail_returns204() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(authorizationService.canWrite(ORG_ID)).thenReturn(true);
        doNothing().when(workflow).saveContact(any(), any(), any());

        // When & Then
        mockMvc.perform(put("/api/v1/organizations/{id}/contact", ORG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + VALID_EMAIL + "\"}"))
                .andExpect(status().isNoContent());

        verify(authorizationService).canWrite(ORG_ID);
        verify(workflow).saveContact(eq(ORG_ID), any(), any());
    }

    @Test
    @DisplayName("PUT /organizations/{id}/contact returns 400 for invalid email (ES-01)")
    void updateContact_invalidEmail_returns400() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(authorizationService.canWrite(ORG_ID)).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/v1/organizations/{id}/contact", ORG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workflow);
    }

    @Test
    @DisplayName("PUT /organizations/{id}/contact returns 403 when user lacks write capability (AC-03, ES-03)")
    void updateContact_noWriteCapability_returns403() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(true);
        when(authorizationService.canWrite(ORG_ID)).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/v1/organizations/{id}/contact", ORG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + VALID_EMAIL + "\"}"))
                .andExpect(status().isForbidden());

        verify(authorizationService).canWrite(ORG_ID);
        verifyNoInteractions(workflow);
    }

    @Test
    @DisplayName("PUT /organizations/{id}/contact returns 404 when feature disabled (AC-04)")
    void updateContact_featureDisabled_returns404() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/v1/organizations/{id}/contact", ORG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + VALID_EMAIL + "\"}"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(authorizationService, workflow);
    }

    @Test
    @DisplayName("PUT /organizations/{id}/contact checks feature flag before authorization (optimization)")
    void updateContact_featureDisabled_doesNotCheckAuthorization() throws Exception {
        // Given
        when(featureFlags.isOrganizationContactEnabled()).thenReturn(false);

        // When
        mockMvc.perform(put("/api/v1/organizations/{id}/contact", ORG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + VALID_EMAIL + "\"}"))
                .andExpect(status().isNotFound());

        // Then: authorization not checked when feature disabled (404 before 403)
        verifyNoInteractions(authorizationService);
    }
}
