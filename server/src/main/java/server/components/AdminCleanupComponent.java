package server.components;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.database.AdminUserRepository;

@Component
public class AdminCleanupComponent {

    private final AdminUserRepository adminUserRepository;

    /**
     * Constructor for a new admin cleanup component
     *
     * @param adminUserRepository the admin user repository
     */
    @Autowired
    public AdminCleanupComponent(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }


    /**
     * Cleanup the admin user repository
     */
    @PreDestroy
    public void cleanup() {
        adminUserRepository.deleteAll();
    }
}
