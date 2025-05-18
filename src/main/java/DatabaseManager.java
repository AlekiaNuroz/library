import jakarta.persistence.TypedQuery;
import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.sql.*;
import java.util.*;
import com.zaxxer.hikari.*;


@SuppressWarnings("unused")
public class DatabaseManager {
    private static SessionFactory sessionFactory;

    private static final String DB_NAME = System.getenv("DATABASE_NAME");
    private static final String DB_USER = System.getenv("DATABASE_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DATABASE_PASSWORD");
    private static final String DB_HOST = System.getenv("DATABASE_HOST");
    private static final int DB_PORT = 5432;

    static {
        try {
            createDatabaseIfNotExists();
            initHibernate();
        } catch (Exception e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }

    private static void createDatabaseIfNotExists() throws SQLException {
        String defaultUrl = String.format("jdbc:postgresql://%s:%d/postgres", DB_HOST, DB_PORT);
        try (Connection conn = DriverManager.getConnection(defaultUrl, DB_USER, DB_PASSWORD)) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'");

            if (!rs.next()) {
                System.out.println("Database '" + DB_NAME + "' does not exist. Creating...");
                conn.createStatement().executeUpdate("CREATE DATABASE " + DB_NAME);
                System.out.println("Database created.");
            }
        }
    }

    private static void initHibernate() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", DB_HOST, DB_PORT, DB_NAME));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("org.postgresql.Driver");

        HikariDataSource dataSource = new HikariDataSource(config);

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "update"); // Creates tables if they don't exist
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");

        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(props)
                .applySetting("hibernate.connection.datasource", dataSource)
                .build();

        sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Dvd.class)
                .addAnnotatedClass(Magazine.class)
                .addAnnotatedClass(VideoGame.class)
                .addAnnotatedClass(IdCounter.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            initHibernate();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public <T> void saveOrUpdate(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            T managedEntity = session.merge(entity);
            tx.commit();
        } catch (Exception e) {
            System.out.println("Error saving entity: " + e.getMessage());
        }
    }

    public <T> T getById(Class<T> entity, String id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entity, id);
        }
    }

    public <T> void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    public <T> List<T> getAll(Class<T> entity) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + entity.getSimpleName(), entity).list();
        }
    }

    public List<LibraryItem> getAllLibraryItems() {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<LibraryItem> query = session.createQuery("FROM LibraryItem", LibraryItem.class);
            return query.getResultList();
        }
    }


    public void registerShutdownHook(GenericCatalog<LibraryItem> catalog) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> persistUnsavedData(catalog)));
    }

    public void persistUnsavedData(GenericCatalog<LibraryItem> catalog) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            for (LibraryItem item : catalog.getAllItems()) {
                session.merge(item);  // Saves or updates existing items
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error during shutdown persistence: " + e.getMessage());
        }
    }

}
