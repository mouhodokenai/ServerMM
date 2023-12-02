package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class Database {
    protected String dbHost = "localhost";
    protected String dbPort = "3306";
    protected String dbUser = "root";
    protected String dbPass = "MA58sh62.";
    protected String dbName = "socservices";
    //protected static User user = new User();
    //protected static Suggestion suggestion = new Suggestion();
    Connection dbConnection;

    {
        try {
            dbConnection = getDbConnection();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String request;

    /*Database(){
        try {
            dbConnection = getDbConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        System.out.println("Все заебок");
        return dbConnection;
    }
/*
    public String CheckUser(String snils, String salt_password) {
        request = "SELECT * FROM user_profile WHERE snils = ? AND salt_password = ?";
        try {
            PreparedStatement prSt = dbConnection.prepareStatement(request);
            prSt.setString(1, snils);
            prSt.setString(2, salt_password);
            ResultSet resultSet = prSt.executeQuery();
            int schet = 0;
            String answerbd = null;
            while (resultSet.next()) {
                answerbd =  "OK";
                schet ++ ;
            }
            if (schet == 0) {
                answerbd = "BADLY";
            }
            else if (schet > 1){
                answerbd = "Вы что ебланы как получилось 2 или больше одинаковых?"; //главное не забыть убрать
            }
            return answerbd;

        } catch (SQLException e) {
            System.out.println("ошибка");
            throw new RuntimeException(e);
        }
    }

 */
    //немножка переписала метод сверху, добавила проверку
    public String CheckUser(String snils, String enteredPassword) {
        request = "SELECT hashed_password, salt_password FROM user_profile WHERE snils = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, snils);
            ResultSet resultSet = prSt.executeQuery();

            int schet = 0;
            String answerbd = null;

            while (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("hashed_password");
                String saltFromDB = resultSet.getString("salt_password");

                // Проверка пароля
                if (BCrypt.checkpw(enteredPassword, hashedPasswordFromDB)) {
                    answerbd = "OK";
                    schet++;
                }
            }

            if (schet == 0) {
                answerbd = "BADLY";
            } else if (schet > 1) {
                answerbd = "Вы что ебланы как получилось 2 или больше одинаковых?"; //главное не забыть убрать
            }

            return answerbd;

        } catch (SQLException e) {
            System.out.println("ошибка");
            throw new RuntimeException(e);
        }
    }
    //проверка работника
    public String checkWorkerCredentials(String login, String password) {
        request = "SELECT hashed_password, salt_password FROM worker_profile WHERE login = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, login);
            ResultSet resultSet = prSt.executeQuery();
            String answerbd = null;
            if (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("hashed_password");
                String saltFromDB = resultSet.getString("salt_password");

                // Проверка пароля
                if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                    answerbd = "OK";
                }
                else {
                    answerbd = "BADLY";
                }
            }
            return answerbd;
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке учетных данных работника.");
            throw new RuntimeException(e);
        }
    }
    //метод для регистрации пользователя с хешированным паролем и солью
    public void registerUser(String snils, String phone, String plainPassword) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(plainPassword, salt);
        request = "INSERT INTO user_profile (snils, phone, hashed_password, salt_password) VALUES (?, ?, ?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, snils);
            prSt.setString(2, phone);
            prSt.setString(3, hashedPassword);
            prSt.setString(4, salt);

            prSt.executeUpdate();
            System.out.println("Пользователь успешно зарегистрирован.");
        } catch (SQLException e) {
            System.out.println("Ошибка при регистрации пользователя.");
            throw new RuntimeException(e);
        }
    }
    //метод для регистрации работника с хешированным паролем и солью
    public void registerWorker(String login, String plainPassword, String name, String surname, String patronymic, String post, boolean isAdmin) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(plainPassword, salt);

        request = "INSERT INTO worker_profile (login, hashed_password, salt_password, name, surname, patronymic, post, admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, login);
            prSt.setString(2, hashedPassword);
            prSt.setString(3, salt);
            prSt.setString(4, name);
            prSt.setString(5, surname);
            prSt.setString(6, patronymic);
            prSt.setString(7, post);
            prSt.setBoolean(8, isAdmin);

            prSt.executeUpdate();
            System.out.println("Работник успешно зарегистрирован.");

        } catch (SQLException e) {
            System.out.println("Ошибка при регистрации работника.");
            throw new RuntimeException(e);
        }
    }
    //Я пока не разобралась что такое эдюзер и с чем его едят
    public void addUser(String snils, String documentName, String documentNumber,
                        String name, String surname, String patronymic,
                        Date birthdate, String phone, String email,
                        int regionId, String regionSmall,
                        String city, String street, String home, String apartment) {
        request = "INSERT INTO user_data (snils, document_name, document_number, name, surname, patronymic, brithdate, phone, email, region_id, region_small, city, street, home, apartment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, snils);
            prSt.setString(2, documentName);
            prSt.setString(3, documentNumber);
            prSt.setString(4, name);
            prSt.setString(5, surname);
            prSt.setString(6, patronymic);
            prSt.setDate(7, birthdate);
            prSt.setString(8, phone);
            prSt.setString(9, email);
            prSt.setInt(10, regionId);
            prSt.setString(11, regionSmall);
            prSt.setString(12, city);
            prSt.setString(13, street);
            prSt.setString(14, home);
            prSt.setString(15, apartment);

            prSt.executeUpdate();
            System.out.println("Пользователь успешно добавлен в таблицу user_data.");

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении пользователя в таблицу user_data.");
            throw new RuntimeException(e);
        }
    }
    //заявление?
    public void addApplication(String userSnils, Integer userAddId, Integer workerProfileId,
                               int socOrganizationOgrrn, String form, String reason,
                               boolean domestic, boolean medical, boolean psychological,
                               boolean pedagogical, boolean labour, boolean legal,
                               boolean communication, boolean urgent, String family,
                               String living, int income, String status) {
        request = "INSERT INTO application (user_data_snils, user_data_add_, worker_profile_id, soc_organization_ogrrn, form, reason, " +
                "domestic, medical, psychological, pedagogical, labour, legal, communication, urgent, famaly, living, income, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, userSnils);
            prSt.setObject(2, userAddId);  // Может быть NULL, поэтому используем setObject
            prSt.setObject(3, workerProfileId);  // Может быть NULL, поэтому используем setObject
            prSt.setInt(4, socOrganizationOgrrn);
            prSt.setString(5, form);
            prSt.setString(6, reason);
            prSt.setBoolean(7, domestic);
            prSt.setBoolean(8, medical);
            prSt.setBoolean(9, psychological);
            prSt.setBoolean(10, pedagogical);
            prSt.setBoolean(11, labour);
            prSt.setBoolean(12, legal);
            prSt.setBoolean(13, communication);
            prSt.setBoolean(14, urgent);
            prSt.setString(15, family);
            prSt.setString(16, living);
            prSt.setInt(17, income);
            prSt.setString(18, status);

            prSt.executeUpdate();
            System.out.println("Заявка успешно добавлена в таблицу ");

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении заявки в таблицу ");
            throw new RuntimeException(e);
        }
    }
    //форма
    public void addForm(String name) {
        request = "INSERT INTO form (name) VALUES (?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, name);

            prSt.executeUpdate();
            System.out.println("Форма успешно добавлена в таблиц");

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении формы в таблицу");
            throw new RuntimeException(e);
        }
    }
    // Метод для добавления новой социальной организации
    private void addSocOrganization(int ogrrn, String name) {
        request = "INSERT INTO soc_organization (ogrrn, name) VALUES (?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, ogrrn);
            prSt.setString(2, name);

            prSt.executeUpdate();
            System.out.println("Социальная организация успешно добавлена в таблицу");

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении социальной организации в таблицу");
            throw new RuntimeException(e);
        }
    }
    // Метод для добавления нового документа
    private void addDocument(String name, String regex) {
        request = "INSERT INTO document (name, regex) VALUES (?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, name);
            prSt.setString(2, regex);

            prSt.executeUpdate();
            System.out.println("Документ успешно добавлен в таблицу document.");

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении документа в таблицу document.");
            throw new RuntimeException(e);
        }
    }
    // Метод для добавления нового региона
    private void addRegion(int id, String name) {
        request = "INSERT INTO region (id, name) VALUES (?, ?)";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, id);
            prSt.setString(2, name);

            prSt.executeUpdate();
            System.out.println("Регион успешно добавлен в таблицу");

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении региона в таблицу");
            throw new RuntimeException(e);
        }
    }
    //удаление работника
    public void deleteWorker(int workerId) {
        request = "DELETE FROM worker_profile WHERE id = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, workerId);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Работник успешно удален.");
            } else {
                System.out.println("Работник с указанным ID не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении работника.");
            throw new RuntimeException(e);
        }
    }
    //удаление пользователя
    public void deleteUser(String snils) {
        request = "DELETE FROM user_profile WHERE snils = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, snils);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Пользователь успешно удален.");
            } else {
                System.out.println("Пользователь с указанным СНИЛС не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении пользователя.");
            throw new RuntimeException(e);
        }
    }
    //удаление документа
    public void deleteDocument(String documentName) {
        request = "DELETE FROM document WHERE name = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, documentName);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Документ успешно удален.");
            } else {
                System.out.println("Документ с указанным именем не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении документа.");
            throw new RuntimeException(e);
        }
    }
    //удаление региона
    public void deleteRegion(int regionId) {
        String request = "DELETE FROM region WHERE id = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, regionId);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Регион успешно удален.");
            } else {
                System.out.println("Регион с указанным ID не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении региона.");
            throw new RuntimeException(e);
        }
    }
    //удаление данных пользователя
    public void deleteUserData(String snils) {
        String request = "DELETE FROM user_data WHERE snils = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, snils);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Данные пользователя успешно удалены.");
            } else {
                System.out.println("Пользователь с указанным СНИЛС не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных пользователя.");
            throw new RuntimeException(e);
        }
    }
    //
    public void deleteUserAdditionalData(int dataId) {
        String request = "DELETE FROM user_data_add WHERE id = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, dataId);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Дополнительные данные пользователя успешно удалены.");
            } else {
                System.out.println("Данные с указанным ID не найдены.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении дополнительных данных пользователя.");
            throw new RuntimeException(e);
        }
    }
    //удаление организации
    public void deleteSocialOrganization(int ogrrn) {
        String request = "DELETE FROM soc_organization WHERE ogrrn = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, ogrrn);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Социальная организация успешно удалена.");
            } else {
                System.out.println("Организация с указанным OGRRN не найдена.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении социальной организации.");
            throw new RuntimeException(e);
        }
    }
    //удаление формы
    public void deleteForm(String formName) {
        String request = "DELETE FROM form WHERE name = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setString(1, formName);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Форма успешно удалена.");
            } else {
                System.out.println("Форма с указанным именем не найдена.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении формы.");
            throw new RuntimeException(e);
        }
    }
    //заявление удаление
    public void deleteApplication(int applicationId) {
        String request = "DELETE FROM application WHERE id = ?";

        try (PreparedStatement prSt = dbConnection.prepareStatement(request)) {
            prSt.setInt(1, applicationId);

            int rowsDeleted = prSt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Заявление успешно удалено.");
            } else {
                System.out.println("Заявление с указанным ID не найдено.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении заявления.");
            throw new RuntimeException(e);
        }
    }







}



