package server.components;

import commons.AdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.api.AdminUserController;
import server.api.AdminUserRepositoryTest;
import server.services.AdminUserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class AdminCleanupComponentTest {
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
    void cleanUp() {
        AdminUser admin = new AdminUser("hello");
        controller.add(admin);
        clean.cleanup();
        var actual = controller.add(new AdminUser());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
}
