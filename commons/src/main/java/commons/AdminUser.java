package commons;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Entity
public class AdminUser implements Serializable {

    @Id
    @Column(unique = true)
    private String password;

    /**
     * Constructs a new AdminUser with the specified password.
     *
     * @param password the password of the user
     */
    public AdminUser(String password) {
        this.password = password;
    }

    /**
     * Creates a new User without parameters
     */
    public AdminUser() {

    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Tests if the specified object is equal to this AdminUser.
     *
     * @param o the object to compare
     * @return true if the object is equal to this AdminUser, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AdminUser adminUser = (AdminUser) o;

        return new EqualsBuilder().append(password, adminUser.password).isEquals();
    }

    /**
     * Returns the hash code of this AdminUser.
     *
     * @return the hash code of this AdminUser
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(password).toHashCode();
    }
}

