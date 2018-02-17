package no.rogfk.password.forgotten.ldap

import no.rogfk.password.forgotten.model.UserPassword
import no.rogfk.password.forgotten.exceptions.MultipleUsersFound
import no.rogfk.password.forgotten.utils.UserFactory
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQuery
import spock.lang.Specification

class LdapServiceSpec extends Specification {

    private ldapTemplate
    private ldapService

    void setup() {
        ldapTemplate = Mock(LdapTemplate)
        ldapService = new LdapService(ldapTemplate: ldapTemplate, searchBase: "ou=persons,o=org")
    }

    def "Get userinfo from by NIN"() {

        when:
        def userInfo = ldapService.getUserInfo("12345678987")

        then:
        userInfo != null
        1 * ldapTemplate.find(_ as LdapQuery, _ as Class) >> Arrays.asList(UserFactory.userInfo)
    }

    def "Get userinfo from by NIN when multiple users found"() {

        when:
        def userInfo = ldapService.getUserInfo("12345678987")

        then:
        userInfo == null
        thrown MultipleUsersFound
        1 * ldapTemplate.find(_ as LdapQuery, _ as Class) >> Arrays.asList(UserFactory.userInfo, UserFactory.userInfo)
    }

    def "Set user password in LDAP"() {

        given:
        def userPassword = new UserPassword(dn: "cn=user1,o=org", password: 'p@ssword')

        when:
        def result = ldapService.setPassword(userPassword)

        then:
        result == true
    }

}
