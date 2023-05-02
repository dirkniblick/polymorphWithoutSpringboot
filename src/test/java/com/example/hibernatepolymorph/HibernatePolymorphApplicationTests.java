package com.example.hibernatepolymorph;

import com.example.hibernatepolymorph.entity.IntegerProperty;
import com.example.hibernatepolymorph.entity.Property;
import com.example.hibernatepolymorph.entity.PropertyHolder;
import com.example.hibernatepolymorph.entity.PropertyRepository;
import com.example.hibernatepolymorph.entity.StringProperty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HibernatePolymorphApplicationTests {

    static EntityManagerFactory entityManagerFactory;
    static EntityManager entityManager;

    @BeforeAll
    static void beforeAll() {
        entityManagerFactory = Persistence.createEntityManagerFactory("H2DB");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll
    static void afterAll() {
        entityManager.close();
        entityManagerFactory.close();
    }

    @Test
    @Order(1)
    void createPropertyHolder() {
        IntegerProperty ageProperty = new IntegerProperty();
        ageProperty.setId(1L);
        ageProperty.setName("age");
        ageProperty.setValue(23);

        save(ageProperty);
        System.out.printf("Created: %s%n", ageProperty);

        StringProperty nameProperty = new StringProperty();
        nameProperty.setId(1L);
        nameProperty.setName("name");
        nameProperty.setValue("John Doe");

        save(nameProperty);
        System.out.printf("Created: %s%n", nameProperty);

        PropertyHolder namePropertyHolder = new PropertyHolder();
        namePropertyHolder.setId(1L);
        namePropertyHolder.setProperty(nameProperty);

        save(namePropertyHolder);
        System.out.printf("Created: %s%n", namePropertyHolder);

        assertThat(namePropertyHolder.getId()).isNotNull();

        PropertyHolder propertyHolder = retrieve(PropertyHolder.class, 1L);
        System.out.printf("Retrieved: %s%n", propertyHolder);

        assertThat(propertyHolder.getProperty().getName()).isEqualTo("name");
        assertThat(propertyHolder.getProperty().getValue()).isEqualTo("John Doe");
    }

    @Test
    @Order(2)
    void createPropertyRepository() {
        IntegerProperty ageProperty = new IntegerProperty();
        ageProperty.setId(2L);
        ageProperty.setName("age");
        ageProperty.setValue(23);

        save(ageProperty);
        System.out.printf("Created: %s%n", ageProperty);

        StringProperty nameProperty = new StringProperty();
        nameProperty.setId(2L);
        nameProperty.setName("name");
        nameProperty.setValue("John Doe");

        save(nameProperty);
        System.out.printf("Created: %s%n", nameProperty);

        PropertyRepository propertyRepository = new PropertyRepository();
        propertyRepository.setId(1L);

        propertyRepository.getProperties().add(ageProperty);
        propertyRepository.getProperties().add(nameProperty);

        save(propertyRepository);
        System.out.printf("Created: %s%n", propertyRepository);

        assertThat(propertyRepository.getId()).isNotNull();

        PropertyRepository retrievedPropertyRepository = retrieve(PropertyRepository.class, 1L);
        System.out.printf("Retrieved: %s%n", retrievedPropertyRepository);

        assertThat(retrievedPropertyRepository.getProperties()).hasSize(2);

        for(Property<?> property : retrievedPropertyRepository.getProperties()) {
            assertThat(property.getValue()).isNotNull();
        }
    }

    void save(Object object) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(object);
        transaction.commit();
    }

    <T> T retrieve(Class<T> entityType, Object id) {
        EntityManager newEntityManager = entityManagerFactory.createEntityManager();
        return newEntityManager.find(entityType, id);
    }
}
