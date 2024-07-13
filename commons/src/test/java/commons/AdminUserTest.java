package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserTest {


    @Test
    void getPassword() {
        AdminUser admin = new AdminUser("1234");
        assertEquals("1234", admin.getPassword());
    }

    @Test
    void setPassword() {
        AdminUser admin1 = new AdminUser("GoodMorning");
        AdminUser admin2 = new AdminUser("1234");
        admin1.setPassword("1234");
        assertEquals(admin1, admin2);
    }


    @Test
    void testEquals() {
        AdminUser admin1 = new AdminUser("1234");
        AdminUser admin2 = new AdminUser("1234");
        assertEquals(admin1, admin2);
    }

    @Test
    void testEqualsEmptyConstructor() {
        AdminUser admin1 = new AdminUser();
        AdminUser admin2 = new AdminUser();
        assertEquals(admin1, admin2);
    }

    @Test
    void testHashCode() {
        AdminUser admin1 = new AdminUser("1234");
        AdminUser admin2 = new AdminUser("1235");
        int hash1 = admin1.hashCode();
        int hash2 = admin2.hashCode();
        assertNotEquals(hash1, hash2);
    }

    @Test
    void testHashCodeEq() {
        AdminUser admin1 = new AdminUser("1234");
        AdminUser admin2 = new AdminUser("1234");
        int hash1 = admin1.hashCode();
        int hash2 = admin2.hashCode();
        assertEquals(hash1, hash2);
    }
}