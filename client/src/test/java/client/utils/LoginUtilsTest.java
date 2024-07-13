package client.utils;

import commons.AdminUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginUtilsTest {

    LoginUtils loginUtils = new LoginUtils();

    @Test
    void nullUser() {
        String pass = loginUtils.randPass();
        AdminUser user = new AdminUser(pass);
        assertNotNull(user);
    }

    @Test
    void correctPassGen(){
        String pass = loginUtils.randPass();
        AdminUser user = new AdminUser(pass);
        assertEquals(user.getPassword(), pass);
    }
}
