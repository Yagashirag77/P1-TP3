package com.example;

import com.example.model.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestion-reservations-pu");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            String email = "yassine.gartite@example.com";
            List<Utilisateur> resultats = em.createQuery(
                            "select u from Utilisateur u where u.email = :email", Utilisateur.class)
                    .setParameter("email", email)
                    .getResultList();

            Utilisateur user;
            if (resultats.isEmpty()) {
                user = creerExempleYassine(email);
                em.persist(user);
            } else {
                user = resultats.get(0);
            }
            em.getTransaction().commit();

            Long utilisateurId = user.getId();
            em.clear();

            Utilisateur utilisateurRelu = em.find(Utilisateur.class, utilisateurId);
            System.out.printf(
                    "Database result: %s (%s), %d reservation(s).%n",
                    utilisateurRelu.getNomComplet(),
                    utilisateurRelu.getEmail(),
                    utilisateurRelu.getReservations().size());
        } catch (RuntimeException exception) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw exception;
        } finally {
            em.close();
            emf.close();
        }
    }

    private static Utilisateur creerExempleYassine(String email) {
        Utilisateur user = new Utilisateur(
                email, "Yassine", "Gartite", "+212600112233");
        Salle salle = new Salle(
                "Atlas Innovation", 24, "Bloc B - 2e étage", TypeSalle.FORMATION);
        Equipement projecteur = new Equipement(
                "Vidéoprojecteur Atlas", "Epson",
                "Projecteur laser utilisé pour les formations techniques",
                LocalDate.of(2026, 9, 15));
        salle.addEquipement(projecteur);

        Reservation reservation = new Reservation(
                LocalDateTime.of(2026, 10, 20, 9, 0),
                LocalDateTime.of(2026, 10, 20, 11, 0),
                "Présentation du projet JEE");
        reservation.setSalle(salle);
        reservation.confirmer();
        user.addReservation(reservation);
        return user;
    }
}
