package com.example.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true, length = 20)
    private String telephone;

    @Column(nullable = false, updatable = false)
    private LocalDate dateInscription = LocalDate.now();

    @Column(nullable = false)
    private boolean actif = true;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations = new HashSet<>();

    public Utilisateur() {
    }

    public Utilisateur(String email, String prenom, String nom, String telephone) {
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
    }

    public void addReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("La réservation ne peut pas être nulle");
        }
        if (reservation.getUtilisateur() != null && reservation.getUtilisateur() != this) {
            reservation.getUtilisateur().removeReservation(reservation);
        }
        reservations.add(reservation);
        reservation.setUtilisateur(this);
    }

    public void removeReservation(Reservation reservation) {
        if (reservations.remove(reservation)) {
            reservation.setUtilisateur(null);
        }
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public void desactiverCompte() { actif = false; }
    public void reactiverCompte() { actif = true; }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public LocalDate getDateInscription() { return dateInscription; }
    public boolean isActif() { return actif; }
    public Set<Reservation> getReservations() { return Collections.unmodifiableSet(reservations); }
}
