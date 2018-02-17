package no.rogfk.password.forgotten.model;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;


@Data
@Entry(objectClasses = {"person", "top"})
public class UserPassword {

    @Id
    private Name dn;

    @Attribute(name = "userPassword")
    private String password;

    public String getDn() {
        if (dn != null) {
            return dn.toString();
        } else {
            return null;
        }
    }

    public void setDn(String dn) {
        this.dn = LdapNameBuilder.newInstance(dn).build();
    }

}
