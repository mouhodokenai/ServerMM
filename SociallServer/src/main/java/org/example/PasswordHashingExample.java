package org.example;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashingExample {

    public static void main(String[] args) {
        // Пример создания засоленного и хешированного пароля
        String originalPassword = "userPassword";
        String hashedPassword = hashPassword(originalPassword);

        // Проверка пароля
        boolean passwordMatch = checkPassword(originalPassword, hashedPassword);

        // Вывод результата
        System.out.println("Original Password: " + originalPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("Password Match: " + passwordMatch);
    }

    // Метод для засолки и хеширования пароля
    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    // Метод для проверки пароля
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
