package no.rogfk.password.forgotten.controller

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.test.utils.MockMvcSpecification
import no.rogfk.password.forgotten.ldap.LdapService
import no.rogfk.password.forgotten.model.UserInfo
import no.rogfk.password.forgotten.model.UserPassword
import no.rogfk.password.forgotten.utils.UserFactory
import org.springframework.http.MediaType
import org.springframework.ldap.NameNotFoundException
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQuery
import org.springframework.test.web.servlet.MockMvc

import javax.naming.ldap.LdapName

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
        def response = mockMvc.perform(get("/api/userinfo/${UserFactory.userInfo.dn}"))

        then:
        1 * ldapTemplate.findByDn(_ as LdapName, UserInfo) >> UserFactory.userInfo
        response.andExpect(status().isOk())
                .andExpect(jsonPathEquals('$.dn', UserFactory.userInfo.dn))
                .andExpect(jsonPathEquals('$.firstName', UserFactory.userInfo.firstName))
                .andExpect(jsonPathEquals('$.lastName', UserFactory.userInfo.lastName))
    }

    def "Set password successfully"() {
        when:
        def response = mockMvc.perform(post('/api/password')
                .content(new ObjectMapper().writeValueAsString(UserFactory.userPassword))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

        then:
        1 * ldapTemplate.update(_ as UserPassword)
        response.andExpect(status().isOk())
    }

    def "Set password unsuccessfully/user not found"() {
        given:
        def userPassword = UserFactory.userPassword

        when:
        def response = mockMvc.perform(post('/api/password')
                .content(new ObjectMapper().writeValueAsString(userPassword))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

        then:
        1 * ldapTemplate.update(_ as UserPassword) >> { throw new NameNotFoundException('user not found') }
        response.andExpect(status().isNotFound())
    }

}
