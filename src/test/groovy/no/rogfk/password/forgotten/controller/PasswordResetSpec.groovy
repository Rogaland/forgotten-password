package no.rogfk.password.forgotten.controller

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.test.utils.MockMvcSpecification
import no.rogfk.password.forgotten.ldap.LdapService
import no.rogfk.password.forgotten.model.UserPassword
import no.rogfk.password.forgotten.utils.UserFactory
import org.springframework.http.MediaType
import org.springframework.ldap.NameNotFoundException
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQuery
import org.springframework.test.web.servlet.MockMvc

class PasswordResetSpec extends MockMvcSpecification {

    private MockMvc mockMvc
    private PasswordReset controller
    private LdapTemplate ldapTemplate
    private LdapService ldapService


    void setup() {
        ldapTemplate = Mock(LdapTemplate)
        ldapService = new LdapService(ldapTemplate: ldapTemplate, searchBase: "ou=persons,o=org")
        controller = new PasswordReset(ldapService: ldapService)
        mockMvc = standaloneSetup(controller)
    }

    def "Get user information"() {

        when:
        def response = mockMvc.perform(get("/api/userinfo/" + UserFactory.getUserInfo().dn))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPathSize('$..*', 3))
                .andExpect(jsonPathEquals('$.dn', UserFactory.getUserInfo().dn))
                .andExpect(jsonPathEquals('$.firstName', UserFactory.getUserInfo().firstName))
                .andExpect(jsonPathEquals('$.lastName', UserFactory.getUserInfo().lastName))
        1 * ldapTemplate.find(_ as LdapQuery, _ as Class) >> Arrays.asList(UserFactory.userInfo)
    }

    def "Set password successfully"() {

        when:
        def response = mockMvc.perform(post('/api/password')
                .content(new ObjectMapper().writeValueAsString(UserFactory.userPassword))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

        then:
        response.andExpect(status().isOk())
        1 * ldapTemplate.update(_ as UserPassword)
    }

    def "Set password unsuccessfully/user not found"() {

        when:
        def response = mockMvc.perform(post('/api/password')
                .content(new ObjectMapper().writeValueAsString(UserFactory.userPassword))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

        then:
        response.andExpect(status().isNotFound())
        1 * ldapTemplate.update(_ as UserPassword) >> { throw new NameNotFoundException("user not found") }
    }

}
