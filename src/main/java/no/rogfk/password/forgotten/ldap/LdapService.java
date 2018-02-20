package no.rogfk.password.forgotten.ldap;

import no.rogfk.password.forgotten.exceptions.MultipleUsersFound;
import no.rogfk.password.forgotten.model.UserInfo;
import no.rogfk.password.forgotten.model.UserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${rfk.ldap.searchBase}")
    private String searchBase;

    public UserInfo getUserInfo(String nin) {
        LdapQuery filter = query()
                .base(searchBase)
                .where("norEduPersonNin").is(nin);

        List<UserInfo> userInfos = ldapTemplate.find(filter, UserInfo.class);

        if (userInfos.size() == 1) {
            return userInfos.get(0);
        }

        throw new MultipleUsersFound();
    }

    public boolean setPassword(UserPassword userPassword) {
        try {
            ldapTemplate.update(userPassword);
            return true;
        } catch (org.springframework.ldap.NamingException e) {
            return false;
        }
    }
}
