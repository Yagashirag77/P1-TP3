package com.example;

import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.TypeSalle;
import com.example.model.Utilisateur;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersistenceTest {
    private static EntityManagerFactory emf;

    @BeforeAll
    static void createEntityManagerFactory() {
        Map<String, Object> testProperties = new HashMap<>();
        testProperties.put("javax.persistence.jdbc.url",
                "jdbc:h2:mem:reservation-tests;MODE=LEGACY;DB_CLOSE_DELAY=-1");
        testProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        testProperties.put("hibernate.show_sql", "false");
        emf = Persistence.createEntityManagerFactory("gestion-reservations-pu", testProperties);
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        emf.close();
    }

    @Test
    void persistUtilisateurCascadeToutesLesAssociations() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Utilisateur utilisateur = new Utilisateur(
                "hamza@example.com", "Hamza", "El Amrani", "+212600000001");
        Salle salle = new Salle(
                "Salle Cascade", 18, "Bloc A", TypeSalle.REUNION);
        Equipement equipement = new Equipement(
                "Écran interactif", "Samsung", "Écran tactile 65 pouces", LocalDate.of(2025, 6, 10));
        salle.addEquipement(equipement);

        Reservation reservation = creerReservation(2, 14);
        reservation.setSalle(salle);
        utilisateur.addReservation(reservation);

        em.persist(utilisateur);
        em.getTransaction().commit();

        assertNotNull(utilisateur.getId());
        assertNotNull(reservation.getId());
        assertNotNull(salle.getId());
        assertNotNull(equipement.getId());
        em.close();
    }

    @Test
    void retirerReservationSupprimeOrphelinSansSupprimerSalleNiEquipement() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Utilisateur utilisateur = new Utilisateur(
                "salma@example.com", "Salma", "Bennani", "+212600000002");
        Salle salle = new Salle(
                "Salle Orphelin", 12, "Bloc C", TypeSalle.FORMATION);
        Equipement equipement = new Equipement(
                "Tableau blanc", "Bi-Office", "Tableau magnétique", LocalDate.of(2024, 3, 20));
        salle.addEquipement(equipement);
        Reservation reservation = creerReservation(3, 10);
        reservation.setSalle(salle);
        utilisateur.addReservation(reservation);
        em.persist(utilisateur);
        em.getTransaction().commit();

        Long reservationId = reservation.getId();
        Long salleId = salle.getId();
        Long equipementId = equipement.getId();

        em.getTransaction().begin();
        utilisateur.removeReservation(reservation);
        em.getTransaction().commit();
        em.clear();

        assertNull(em.find(Reservation.class, reservationId));
        assertNotNull(em.find(Salle.class, salleId));
        assertNotNull(em.find(Equipement.class, equipementId));
        em.close();
    }

    private static Reservation creerReservation(int jour, int heure) {
        LocalDateTime debut = LocalDateTime.of(2026, 11, jour, heure, 0);
        return new Reservation(debut, debut.plusHours(2), "Atelier de conception");
    }
}
