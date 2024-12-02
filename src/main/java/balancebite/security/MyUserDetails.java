package balancebite.security;

import balancebite.model.user.Role;
import balancebite.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MyUserDetails is an implementation of the UserDetails interface,
 * which provides core user information to Spring Security.
 */
public class MyUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructs a MyUserDetails with the specified User.
     *
     * @param user the User entity
     */
    public MyUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     * Adds "ROLE_" prefix to the roles to conform to Spring Security standards.
     *
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add "ROLE_" prefix to each role
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        }

        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username (email) used to authenticate the user.
     *
     * @return the email
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Returns the ID of the user.
     * This is specific to our application logic and is not part of the UserDetails interface.
     *
     * @return the user ID
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Returns the underlying User entity.
     * Useful when you need access to the full User object.
     *
     * @return the User entity
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the account is non-expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the account is non-locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return true if the credentials are non-expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
