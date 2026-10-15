package com.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime debut;

    @Column(nullable = false)
    private LocalDateTime fin;

    @Column(nullable = false, length = 300)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // A new booking may create its room, but deleting the booking must never
    // delete a room that can still be reused by other users.
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    public Reservation() {
    }

    public Reservation(LocalDateTime debut, LocalDateTime fin, String motif) {
        setPeriode(debut, fin);
        this.motif = motif;
    }

    public void setPeriode(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null || !fin.isAfter(debut)) {
            throw new IllegalArgumentException("La fin doit être postérieure au début");
        }
        this.debut = debut;
        this.fin = fin;
    }

    public void confirmer() {
        if (statut == StatutReservation.ANNULEE) {
            throw new IllegalStateException("Une réservation annulée ne peut plus être confirmée");
        }
        statut = StatutReservation.CONFIRMEE;
    }

    public void annuler() { statut = StatutReservation.ANNULEE; }

    public boolean chevauche(Reservation autre) {
        return autre != null && debut.isBefore(autre.fin) && fin.isAfter(autre.debut);
    }

    public Long getId() { return id; }
    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }
    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public StatutReservation getStatut() { return statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public Salle getSalle() { return salle; }

    public void setSalle(Salle nouvelleSalle) {
        if (salle == nouvelleSalle) {
            return;
        }
        if (salle != null) {
            salle.removeReservationInternal(this);
        }
        salle = nouvelleSalle;
        if (nouvelleSalle != null) {
            nouvelleSalle.addReservationInternal(this);
        }
    }
}
