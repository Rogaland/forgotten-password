package no.rogfk.password.forgotten.utils;

import no.rogfk.password.forgotten.model.UserInfo;
import no.rogfk.password.forgotten.model.UserPassword;

public enum UserFactory {
    ;

    public static UserInfo getUserInfo() {

        UserInfo userInfo = new UserInfo();

        userInfo.setDn("cn=duckd,o=andeby");
        userInfo.setFirstName("Donald");
        userInfo.setLastName("Duck");

        return userInfo;
    }

    public static UserPassword getUserPassword() {

        UserPassword userPassword = new UserPassword();

        userPassword.setDn("cn=duckd,o=andeby");
        userPassword.setPassword("p@ssword");

        return userPassword;
    }
}
