package no.rogfk.password.forgotten.ldap

import no.rogfk.password.forgotten.exceptions.MultipleUsersFound
import no.rogfk.password.forgotten.model.UserInfo
import no.rogfk.password.forgotten.model.UserPassword
import no.rogfk.password.forgotten.utils.UserFactory
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQuery
import spock.lang.Specification

class LdapServiceSpec extends Specification {

    private LdapTemplate ldapTemplate
    private LdapService ldapService

    void setup() {
        ldapTemplate = Mock(LdapTemplate)
        ldapService = new LdapService(ldapTemplate: ldapTemplate, searchBase: 'ou=persons,o=org')
    }

    def "Get userinfo from by NIN"() {
        when:
        def userInfo = ldapService.getUserInfo('12345678987')

        then:
        1 * ldapTemplate.find(_ as LdapQuery, UserInfo) >> [UserFactory.userInfo]
        userInfo != null
    }

    def "Get userinfo from by NIN when multiple users found"() {
        when:
        def userInfo = ldapService.getUserInfo('12345678987')

        then:
        1 * ldapTemplate.find(_ as LdapQuery, _ as Class) >> [UserFactory.userInfo, UserFactory.userInfo]
        userInfo == null
        thrown MultipleUsersFound
    }

    def "Set user password in LDAP"() {
        given:
        def userPassword = new UserPassword(dn: 'cn=user1,o=org', password: 'p@ssword')

        when:
        def result = ldapService.setPassword(userPassword)

        then:
        result
    }

}
