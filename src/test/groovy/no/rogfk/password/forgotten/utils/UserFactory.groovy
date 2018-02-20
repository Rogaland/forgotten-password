package no.rogfk.password.forgotten.utils

import no.rogfk.password.forgotten.model.UserInfo
import no.rogfk.password.forgotten.model.UserPassword

class UserFactory {

    static UserInfo getUserInfo() {
        return new UserInfo(
                dn: 'cn=duckd,o=andeby',
                firstName: 'Donald',
                lastName: 'Duck'
        )
    }

    static UserPassword getUserPassword() {
        return new UserPassword(
                dn: 'cn=duckd,o=andeby',
                password: 'p@ssword'
        )
    }
}
