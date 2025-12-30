package tr.mhrs.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:4444/MHRSProje3"; 
    private static final String USER = "postgres"; 
    private static final String PASSWORD = "aynur"; 

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("PostgreSQL'e başarılı bir şekilde bağlandı.");
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.err.println("Bağlantı kesilmiş. Yeniden bağlanmaya çalışılıyor...");
                instance = new DatabaseManager();
                return instance.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}