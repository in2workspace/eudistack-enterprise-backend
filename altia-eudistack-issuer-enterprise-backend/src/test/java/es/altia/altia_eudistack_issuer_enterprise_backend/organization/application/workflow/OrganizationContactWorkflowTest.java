package es.altia.altia_eudistack_issuer_enterprise_backend.organization.application.workflow;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.ContactUpdateSource;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationContactService;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrganizationContactWorkflow}.
 *
 * @since EUD-226 (Task 15)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationContactWorkflow")
class OrganizationContactWorkflowTest {

    @Mock
    private OrganizationContactService contactService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private OrganizationContactWorkflow workflow;

    private static final String ORG_ID = "org-123";
    private static final String EMAIL = "contact@example.com";
    private static final OrganizationContact CONTACT = new OrganizationContact(EMAIL);

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(contactService, auditService);
    }

    @Test
    @DisplayName("findContactByOrganizationId returns existing contact (AC-01)")
    void findContactByOrganizationId_existingContact_returnsContact() {
        // Given
        when(contactService.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.of(CONTACT));

        // When
        Optional<OrganizationContact> result = workflow.findContactByOrganizationId(ORG_ID);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(EMAIL);
        verify(contactService).findContactByOrganizationId(ORG_ID);
    }

    @Test
    @DisplayName("findContactByOrganizationId returns empty when no contact exists (EC-01)")
    void findContactByOrganizationId_noContact_returnsEmpty() {
        // Given
        when(contactService.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.empty());

        // When
        Optional<OrganizationContact> result = workflow.findContactByOrganizationId(ORG_ID);

        // Then
        assertThat(result).isEmpty();
        verify(contactService).findContactByOrganizationId(ORG_ID);
    }

    @Test
    @DisplayName("saveContact updates contact and emits MANUAL audit event (AC-02)")
    void saveContact_manual_updatesAndEmitsAudit() {
        // Given
        OrganizationContact oldContact = new OrganizationContact("old@example.com");
        when(contactService.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.of(oldContact));
        doNothing().when(contactService).saveContact(any(), any(), any());
        doNothing().when(auditService).recordOrganizationContactEvent(any(), any(), any(), any());

        // When
        workflow.saveContact(ORG_ID, CONTACT, ContactUpdateSource.MANUAL);

        // Then
        verify(contactService).saveContact(ORG_ID, CONTACT, ContactUpdateSource.MANUAL);
        verify(auditService).recordOrganizationContactEvent(
                eq(es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model.AuditEventType.ORGANIZATION_CONTACT_UPDATED),
                eq(ORG_ID),
                eq("old@example.com"),
                eq(EMAIL)
        );
    }

    @Test
    @DisplayName("saveContact emits AUTO_PREFILL audit event when source is AUTO_PREFILL (AC-05)")
    void saveContact_autoPrefill_emitsCorrectAuditEvent() {
        // Given
        when(contactService.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.empty());
        doNothing().when(contactService).saveContact(any(), any(), any());
        doNothing().when(auditService).recordOrganizationContactEvent(any(), any(), any(), any());

        // When
        workflow.saveContact(ORG_ID, CONTACT, ContactUpdateSource.AUTO_PREFILL);

        // Then
        verify(contactService).saveContact(ORG_ID, CONTACT, ContactUpdateSource.AUTO_PREFILL);
        verify(auditService).recordOrganizationContactEvent(
                eq(es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model.AuditEventType.ORGANIZATION_CONTACT_AUTO_PREFILLED),
                eq(ORG_ID),
                eq(null), // no old email
                eq(EMAIL)
        );
    }

    @Test
    @DisplayName("autoPrefillContactIfAbsent skips when email is null (EC-03)")
    void autoPrefillContactIfAbsent_nullEmail_skips() {
        // When
        workflow.autoPrefillContactIfAbsent(ORG_ID, null);

        // Then
        verifyNoInteractions(contactService, auditService);
    }

    @Test
    @DisplayName("autoPrefillContactIfAbsent skips when email is blank (EC-03)")
    void autoPrefillContactIfAbsent_blankEmail_skips() {
        // When
        workflow.autoPrefillContactIfAbsent(ORG_ID, "   ");

        // Then
        verifyNoInteractions(contactService, auditService);
    }

    @Test
    @DisplayName("autoPrefillContactIfAbsent skips when contact already exists (EC-02)")
    void autoPrefillContactIfAbsent_existingContact_skips() {
        // Given
        when(contactService.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.of(CONTACT));

        // When
        workflow.autoPrefillContactIfAbsent(ORG_ID, EMAIL);

        // Then
        verify(contactService).findContactByOrganizationId(ORG_ID);
        verify(contactService, never()).saveContact(any(), any(), any());
        verifyNoInteractions(auditService);
    }

    @Test
    @DisplayName("autoPrefillContactIfAbsent saves contact when absent (AC-05)")
    void autoPrefillContactIfAbsent_absent_saves() {
        // Given
        // autoPrefillContactIfAbsent() calls findContactByOrganizationId() once,
        // then internally calls saveContact() which calls findContactByOrganizationId() again
        when(contactService.findContactByOrganizationId(ORG_ID))
                .thenReturn(Optional.empty()); // Return empty for both calls
        doNothing().when(contactService).saveContact(any(), any(), any());
        doNothing().when(auditService).recordOrganizationContactEvent(any(), any(), any(), any());

        // When
        workflow.autoPrefillContactIfAbsent(ORG_ID, EMAIL);

        // Then
        // findContactByOrganizationId called twice: once in autoPrefillContactIfAbsent, once in saveContact
        verify(contactService, times(2)).findContactByOrganizationId(ORG_ID);
        verify(contactService).saveContact(
                eq(ORG_ID),
                eq(new OrganizationContact(EMAIL)),
                eq(ContactUpdateSource.AUTO_PREFILL)
        );
    }

    @Test
    @DisplayName("Organization isolation: different orgIds are independent (AC-06)")
    void organizationIsolation_differentOrgIds_independent() {
        // Given
        String org1 = "org-1";
        String org2 = "org-2";
        OrganizationContact contact1 = new OrganizationContact("org1@example.com");
        OrganizationContact contact2 = new OrganizationContact("org2@example.com");

        when(contactService.findContactByOrganizationId(org1))
                .thenReturn(Optional.of(contact1));
        when(contactService.findContactByOrganizationId(org2))
                .thenReturn(Optional.of(contact2));

        // When
        Optional<OrganizationContact> result1 = workflow.findContactByOrganizationId(org1);
        Optional<OrganizationContact> result2 = workflow.findContactByOrganizationId(org2);

        // Then
        assertThat(result1).isPresent();
        assertThat(result1.get().email()).isEqualTo("org1@example.com");
        assertThat(result2).isPresent();
        assertThat(result2.get().email()).isEqualTo("org2@example.com");

        verify(contactService).findContactByOrganizationId(org1);
        verify(contactService).findContactByOrganizationId(org2);
    }
}
