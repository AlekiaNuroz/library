import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    // Method to get SessionFactory connected to a specific database
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Properties settings = new Properties();
                settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
                settings.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + System.getenv("DATABASE_NAME"));
                settings.put("hibernate.connection.username", System.getenv("DATABASE_USERNAME"));
                settings.put("hibernate.connection.password", System.getenv("DATABASE_PASSWORD"));
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.HBM2DDL_AUTO, "update"); // Let Hibernate handle table creation/updates
                settings.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
                settings.put("hibernate.hikari.minimumIdle", "5");
                settings.put("hibernate.hikari.maximumPoolSize", "20");
                settings.put("hibernate.hikari.idleTimeout", "30000");
                settings.put("hibernate.hikari.maxLifetime", "1800000");
                settings.put("hibernate.hikari.connectionTimeout", "10000");

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(settings)
                        .build();

                MetadataSources metadataSources = new MetadataSources(serviceRegistry);
                metadataSources.addAnnotatedClass(com.ecommerce.Product.class);
                metadataSources.addAnnotatedClass(com.ecommerce.Customer.class);
                metadataSources.addAnnotatedClass(com.ecommerce.orders.Order.class);

                sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    // Method to get a basic JDBC connection without a specific database
    public static Connection getConnectionWithoutDatabase() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/",
                System.getenv("DATABASE_USERNAME"),
                System.getenv("DATABASE_PASSWORD")
        );
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}