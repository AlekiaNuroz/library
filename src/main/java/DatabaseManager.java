import com.ecommerce.Customer;
import com.ecommerce.Product;
import com.ecommerce.orders.Order;
import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.sql.*;
import java.util.*;
import com.zaxxer.hikari.*;

/**
 * Manages the database connection and provides methods for performing common
 * database operations using Hibernate.
 * <p>
 * This class initializes a Hibernate {@code SessionFactory} upon loading,
 * which is configured to connect to a PostgreSQL database. It reads database
 * connection details (name, username, password, host) from environment variables.
 * The class also includes functionality to create the database if it doesn't
 * already exist during the initialization phase.
 * </p>
 * <p>
 * It offers methods for basic CRUD (Create, Read, Update, Delete) operations
 * on entities, as well as a method to retrieve all entities of a specific type.
 * Each operation is performed within a new Hibernate {@code Session} and
 * manages transactions to ensure data integrity.
 * </p>
 * <p>
 * The {@code sessionFactory} is a static, shared resource intended to be used
 * throughout the application for interacting with the database.
 * </p>
 */
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
            e.printStackTrace();
        }
    }

    /**
     * Checks if the database specified by {@code DB_NAME} exists and creates it if it does not.
     * This method attempts to connect to the default PostgreSQL server and queries the
     * {@code pg_database} system catalog to determine if the target database is present.
     * If the database does not exist, it executes a {@code CREATE DATABASE} statement.
     *
     * @throws SQLException if a database access error occurs during the connection attempt,
     * the query for database existence, or the database creation process.
     */
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

    /**
     * Initializes the Hibernate SessionFactory using HikariCP for connection pooling
     * and configures it with the database details and Hibernate properties.
     * This method sets up the data source using the provided database host, port, name,
     * user, and password. It also configures Hibernate to automatically update the
     * database schema based on the annotated entity classes (Customer, Product, Order)
     * and disables SQL logging and formatting.
     * <p>
     * The created SessionFactory is stored in the static {@code sessionFactory} field
     * for use throughout the application.
     */
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
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Order.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    /**
     * Persists the given entity to the database.
     * This method opens a new Hibernate session, begins a transaction, saves the
     * provided entity using the {@code persist()} method, and then commits the
     * transaction. The session and transaction are properly managed within a
     * try-with-resources block to ensure resources are released.
     *
     * @param <T>    The type of the entity to save.
     * @param entity The entity object to be persisted in the database.
     */
    public <T> void save(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    /**
     * Retrieves an entity of the specified class from the database based on its ID.
     * This method opens a new Hibernate session and uses the {@code get()} method
     * to retrieve the entity with the given ID. The session is automatically closed
     * after the operation.
     *
     * @param <T>    The type of the entity to retrieve.
     * @param entity The {@code Class} object representing the entity.
     * @param id     The unique identifier (primary key) of the entity to retrieve.
     * @return The entity of the specified type with the given ID, or {@code null}
     * if no such entity exists in the database.
     */
    public <T> T getById(Class<T> entity, Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entity, id);
        }
    }

    /**
     * Updates the given entity in the database.
     * This method opens a new Hibernate session, begins a transaction, merges the
     * state of the provided entity with the current persistence context using the
     * {@code merge()} method, and then commits the transaction. The session and
     * transaction are properly managed within a try-with-resources block.
     *
     * @param <T>    The type of the entity to update.
     * @param entity The entity object with updated state to be saved to the database.
     */
    public <T> void update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    /**
     * Deletes the given entity from the database.
     * This method opens a new Hibernate session, begins a transaction, removes the
     * provided entity using the {@code remove()} method, and then commits the
     * transaction. The session and transaction are properly managed within a
     * try-with-resources block to ensure resources are released.
     *
     * @param <T>    The type of the entity to delete.
     * @param entity The entity object to be removed from the database.
     */
    public <T> void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    /**
     * Retrieves all entities of the specified class from the database.
     * This method opens a new Hibernate session, creates a query to select all
     * instances of the provided entity class, and returns the results as a List.
     * The session is automatically closed after the operation.
     *
     * @param <T>    The type of the entity to retrieve.
     * @param entity The {@code Class} object representing the entity.
     * @return A {@code List} containing all entities of the specified type found in the database.
     */
    public <T> List<T> getAll(Class<T> entity) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + entity.getSimpleName(), entity).list();
        }
    }
}
