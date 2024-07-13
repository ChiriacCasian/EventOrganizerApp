package server.api;

import commons.AdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.components.AdminCleanupComponent;
import server.services.AdminUserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class AdminUserControllerTest {
    AdminUserController controller;
    AdminUserService adminUserService;
    AdminUserRepositoryTest adminUserRepo;
    AdminCleanupComponent clean;

    @BeforeEach
    public void setup() {
        adminUserRepo = new AdminUserRepositoryTest();
        adminUserService = new AdminUserService(adminUserRepo);
        controller = new AdminUserController(adminUserService);
        clean = new AdminCleanupComponent(adminUserRepo);
    }

    @Test
    public void getById() {
        AdminUser a = getPass("hello");
        controller.add(a);
        assertEquals(a, controller.getByPassword(a.getPassword()).getBody());
    }

    @Test
    void cannotAddNullUser() {
        var actual = controller.add(new AdminUser());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }


    @Test
    void correctPass() {
        AdminUser admin = new AdminUser("qwerty");
        controller.add(admin);
        assertEquals("qwerty", admin.getPassword());
    }

    @Test
    void getPassTest() {
        AdminUser admin = new AdminUser();
        admin.setPassword("hello");
        assertNotNull(admin);
    }

    private static AdminUser getPass(String e) {
        AdminUser ret = new AdminUser(e);
        ret.setPassword(e);
        return ret;
    }

}
