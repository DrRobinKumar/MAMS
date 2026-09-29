package com.kristalball.mams.config;

import com.kristalball.mams.model.AppUser;
import com.kristalball.mams.model.Base;
import com.kristalball.mams.model.Role;
import com.kristalball.mams.repository.BaseRepository;
import com.kristalball.mams.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Runs once when the app starts.
// If there are no users in the database, it creates 2 bases and 3 users so we can login.
@Component
public class DataSeeder implements CommandLineRunner {

    private final BaseRepository baseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(BaseRepository baseRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.baseRepository = baseRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // data is already added, so do nothing
        if (userRepository.count() > 0) {
            return;
        }

        Base alpha = baseRepository.save(new Base("Base Alpha"));
        baseRepository.save(new Base("Base Bravo"));

        createUser("admin", "admin123", Role.ADMIN, null);
        createUser("commander", "cmd123", Role.BASE_COMMANDER, alpha);
        createUser("logistics", "log123", Role.LOGISTICS_OFFICER, alpha);
    }

    private void createUser(String username, String plainPassword, Role role, Base base) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setRole(role);
        user.setBase(base);
        userRepository.save(user);
    }
}
