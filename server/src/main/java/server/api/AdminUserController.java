package server.api;

import commons.AdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.AdminUserService;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Constructor for the controller
     *
     * @param adminUserService admin user service
     */
    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * Handles a GET request to retrieve an admin user by its password.
     *
     * @param password the password of the admin user
     * @return a ResponseEntity with the admin user if it exists
     */
    @GetMapping("/{password}")
    public ResponseEntity<AdminUser> getByPassword(@PathVariable String password) {
        AdminUser user = adminUserService.getById(password);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(user);
        }
    }

    /**
     * Handles a POST request to create a new admin user with the provided username and password.
     *
     * @param user the admin user
     * @return a ResponseEntity with a success message if the admin user is created successfully
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<AdminUser> add(@RequestBody AdminUser user) {
        AdminUser newUser = adminUserService.add(user);
        Logger logger = LoggerFactory.getLogger(AdminUserController.class);
        logger.info("Your new password is: " + user.getPassword());
        if (newUser == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(newUser);
        }
    }

}
