import jakarta.persistence.TypedQuery;
import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import java.sql.*;
import java.util.*;
import com.zaxxer.hikari.*;

/**
 * Manages database connections and ORM operations using Hibernate with HikariCP connection pooling.
 * Handles database initialization, schema updates, and provides CRUD operations for library items.
 * Configures environment-based database credentials and automatic shutdown persistence.
 */
@SuppressWarnings("unused")
public class DatabaseManager {
    /** Hibernate session factory for database operations */
    private static SessionFactory sessionFactory;

    // Environment variable configuration (set in deployment environment)
    private static final String DB_NAME = System.getenv("DATABASE_NAME");
    private static final String DB_USER = System.getenv("DATABASE_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DATABASE_PASSWORD");
    private static final String DB_HOST = System.getenv("DATABASE_HOST");
    private static final int DB_PORT = 5432;  // Default PostgreSQL port

    // Static initializer for database setup
    static {
        try {
            createDatabaseIfNotExists();  // Ensure database exists
            initHibernate();  // Initialize ORM framework
        } catch (Exception e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }

    /**
     * Creates the application database if it doesn't exist.
     * Connects to PostgreSQL's default 'postgres' database to check/create.
     * @throws SQLException if database connection fails
     */
    private static void createDatabaseIfNotExists() throws SQLException {
        String defaultUrl = String.format("jdbc:postgresql://%s:%d/postgres", DB_HOST, DB_PORT);
        try (Connection conn = DriverManager.getConnection(defaultUrl, DB_USER, DB_PASSWORD)) {
            // Check for existing database
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'");

            if (!rs.next()) {
                System.out.println("Database '" + DB_NAME + "' does not exist. Creating...");
                conn.createStatement().executeUpdate("CREATE DATABASE " + DB_NAME);
                System.out.println("Database created.");
            }
        }
    }

    /**
     * Initializes Hibernate ORM with connection pooling and entity mappings.
     * Configures HikariCP connection pool and automatic schema updates.
     */
    private static void initHibernate() {
        // Configure connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", DB_HOST, DB_PORT, DB_NAME));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("org.postgresql.Driver");
        HikariDataSource dataSource = new HikariDataSource(config);

        // Hibernate configuration
        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "update");  // Auto-update schema
        props.put("hibernate.show_sql", "false");  // Disable SQL logging
        props.put("hibernate.format_sql", "false");  // Keep SQL unformatted

        // Build service registry with connection pool
        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(props)
                .applySetting("hibernate.connection.datasource", dataSource)
                .build();

        // Register all entity classes
        sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Dvd.class)
                .addAnnotatedClass(Magazine.class)
                .addAnnotatedClass(VideoGame.class)
                .addAnnotatedClass(IdCounter.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    /**
     * Provides access to Hibernate SessionFactory with lazy initialization.
     * @return Configured SessionFactory instance
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            initHibernate();
        }
        return sessionFactory;
    }

    /**
     * Cleanly shuts down Hibernate and connection pool.
     */
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * Saves or updates an entity in the database.
     * @param entity The entity to persist
     * @param <T> Entity type
     */
    public <T> void saveOrUpdate(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            T managedEntity = session.merge(entity);  // Merge for detached entities
            tx.commit();
        } catch (Exception e) {
            System.out.println("Error saving entity: " + e.getMessage());
        }
    }

    /**
     * Retrieves an entity by its ID.
     * @param entity Class of the entity to retrieve
     * @param id ID of the entity
     * @param <T> Entity type
     * @return The found entity or null
     */
    public <T> T getById(Class<T> entity, String id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entity, id);
        }
    }

    /**
     * Deletes an entity from the database.
     * @param entity The entity to remove
     * @param <T> Entity type
     */
    public <T> void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    /**
     * Retrieves all entities of a specific type.
     * @param entity Class of the entities to retrieve
     * @param <T> Entity type
     * @return List of all entities
     */
    public <T> List<T> getAll(Class<T> entity) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + entity.getSimpleName(), entity).list();
        }
    }

    /**
     * Retrieves all library items regardless of specific subtype.
     * @return List of all LibraryItem entities
     */
    public List<LibraryItem> getAllLibraryItems() {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<LibraryItem> query = session.createQuery("FROM LibraryItem", LibraryItem.class);
            return query.getResultList();
        }
    }

    /**
     * Registers a shutdown hook to persist catalog data before JVM exit.
     * @param catalog Catalog containing items to persist
     */
    public void registerShutdownHook(GenericCatalog<LibraryItem> catalog) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            persistUnsavedData(catalog);
            IOHelper.clearScreen();
            System.out.println("Application is shutting down...");
        }));
    }

    /**
     * Persists all catalog items during shutdown sequence.
     * @param catalog Catalog containing items to save
     */
    public void persistUnsavedData(GenericCatalog<LibraryItem> catalog) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // Merge all items to handle both new and updated entities
            for (LibraryItem item : catalog.getAllItems()) {
                session.merge(item);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error during shutdown persistence: " + e.getMessage());
        }
    }
}