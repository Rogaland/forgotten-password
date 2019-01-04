package no.rogfk.password.forgotten.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rogfk.password.forgotten.ldap.LdapService;
import no.rogfk.password.forgotten.model.UserInfo;
import no.rogfk.password.forgotten.model.UserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class PasswordReset {

    @Autowired
    private LdapService ldapService;

    @GetMapping("/userinfo/{dn}")
    public UserInfo getUserInfo(@PathVariable String dn) {
        UserInfo userInfo = ldapService.getUserInfo(dn);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writeValueAsString(userInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    @PostMapping("/password")
    public ResponseEntity setPassword(@RequestBody UserPassword userPassword) {
        if (ldapService.setPassword(userPassword)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
