package server.services;

import commons.AdminUser;
import org.springframework.stereotype.Service;
import server.database.AdminUserRepository;

@Service
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

    /**
     * Constructor for an admin-user service.
     *
     * @param adminUserRepository the admin-user repository to use.
     */
    public AdminUserService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    /**
     * Retrieve the admin-user by its id.
     *
     * @param password the password of the admin-user.
     * @return the admin-user instance if it exists, null otherwise.
     */
    public AdminUser getById(String password) {
        return adminUserRepository.findById(password).orElse(null);
    }

    /**
     * Adds an admin-user to the repository if it is valid.
     *
     * @param user the admin-user to add.
     * @return the admin-user if adding was successful, null otherwise.
     */
    public AdminUser add(AdminUser user) {
        if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
            return null;
        } else {
            return adminUserRepository.save(user);
        }
    }
}