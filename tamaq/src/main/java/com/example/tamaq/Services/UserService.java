package com.example.tamaq.Services;

import com.example.tamaq.Entities.User;
import com.example.tamaq.Repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(User user) {
        logger.info("Сохранение нового пользователя: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            logger.info("Пользователь успешно сохранен: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    public User findByUsername(String username) {
        logger.info("Поиск пользователя по имени: {}", username);
        return userRepository.findByUsername(username).orElseGet(() -> {
            logger.warn("Пользователь с именем '{}' не найден.", username);
            return null;
        });
    }

    public void updateUser(User updatedUser) {
        logger.info("Попытка обновить профиль пользователя: {}", updatedUser.getUsername());

        User existingUser = findByUsername(updatedUser.getUsername());
        if (existingUser != null) {
            logger.info("Пользователь найден, обновляем данные: {}", existingUser.getUsername());

            // Обновляем контактные данные
            existingUser.setContactNumber(updatedUser.getContactNumber());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setBankCard(updatedUser.getBankCard());
            logger.info("Обновлены контактные данные для пользователя: {}", updatedUser.getUsername());


            try {
                userRepository.save(existingUser);
                logger.info("Профиль пользователя успешно обновлён: {}", updatedUser.getUsername());
            } catch (Exception e) {
                logger.error("Ошибка при обновлении пользователя {}: {}", updatedUser.getUsername(), e.getMessage());
                throw e;
            }
        } else {
            logger.warn("Пользователь с именем '{}' не найден, обновление невозможно.", updatedUser.getUsername());
        }
    }


}
