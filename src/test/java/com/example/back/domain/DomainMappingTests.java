package com.example.back.domain;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DomainMappingTests {

    @Autowired
    private EntityManager entityManager;

    @Test
    void mapsEveryEntityFromTheDiagram() {
        Set<Class<?>> mappedTypes = entityManager.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .collect(Collectors.toSet());

        assertThat(mappedTypes).containsExactlyInAnyOrder(
                Admin.class,
                Agency.class,
                AgencyVehicle.class,
                Bill.class,
                Client.class,
                Conversation.class,
                Message.class,
                Rental.class,
                Role.class,
                Vehicle.class);
    }

    @Test
    void mapsTheDiagramForeignKeysAsAssociations() {
        assertAssociation(Admin.class, "role", Role.class);
        assertAssociation(AgencyVehicle.class, "agency", Agency.class);
        assertAssociation(AgencyVehicle.class, "vehicle", Vehicle.class);
        assertAssociation(Rental.class, "vehicle", Vehicle.class);
        assertAssociation(Rental.class, "client", Client.class);
        assertAssociation(Rental.class, "agency", Agency.class);
        assertAssociation(Rental.class, "startAgency", Agency.class);
        assertAssociation(Rental.class, "endAgency", Agency.class);
        assertAssociation(Bill.class, "client", Client.class);
        assertAssociation(Bill.class, "rental", Rental.class);
        assertAssociation(Conversation.class, "client", Client.class);
        assertAssociation(Conversation.class, "admin", Admin.class);
        assertAssociation(Message.class, "conversation", Conversation.class);
    }

    private void assertAssociation(Class<?> owner, String attributeName, Class<?> target) {
        SingularAttribute<?, ?> attribute = entityManager.getMetamodel()
                .entity(owner)
                .getSingularAttribute(attributeName);

        assertThat(attribute.isAssociation()).isTrue();
        assertThat(attribute.getJavaType()).isEqualTo(target);
    }
}
