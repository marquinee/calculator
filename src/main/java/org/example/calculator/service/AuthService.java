package org.example.calculator.service;
import org.example.calculator.dao.UserDao;
import org.example.calculator.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public boolean register(String username, String password, String role) {
        try {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            userDao.saveUser(username, hash, role);
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        try {
            User user = userDao.findByUsername(username);
            if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
                return user;
            }
        } catch (Exception e) {
            System.out.println("Ошибка входа: " + e.getMessage());
        }
        return null;
    }
}

