package com.example.model;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private int capacite;

    @Column(nullable = false)
    private String localisation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSalle type;

    @Column(nullable = false)
    private boolean disponible = true;

    @OneToMany(mappedBy = "salle")
    private Set<Reservation> reservations = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "salle_equipement",
            joinColumns = @JoinColumn(name = "salle_id"),
            inverseJoinColumns = @JoinColumn(name = "equipement_id"))
    private Set<Equipement> equipements = new HashSet<>();

    public Salle() {
    }

    public Salle(String nom, int capacite, String localisation, TypeSalle type) {
        if (capacite <= 0) {
            throw new IllegalArgumentException("La capacité doit être positive");
        }
        this.nom = nom;
        this.capacite = capacite;
        this.localisation = localisation;
        this.type = type;
    }

    public void addEquipement(Equipement equipement) {
        if (equipement == null) {
            throw new IllegalArgumentException("L'équipement ne peut pas être nul");
        }
        equipements.add(equipement);
        equipement.addSalleInternal(this);
    }

    public void removeEquipement(Equipement equipement) {
        if (equipements.remove(equipement)) {
            equipement.removeSalleInternal(this);
        }
    }

    void addReservationInternal(Reservation reservation) { reservations.add(reservation); }
    void removeReservationInternal(Reservation reservation) { reservations.remove(reservation); }

    public boolean peutAccueillir(int nombrePersonnes) {
        return disponible && nombrePersonnes > 0 && nombrePersonnes <= capacite;
    }

    public void fermerTemporairement() { disponible = false; }
    public void rendreDisponible() { disponible = true; }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) {
        if (capacite <= 0) {
            throw new IllegalArgumentException("La capacité doit être positive");
        }
        this.capacite = capacite;
    }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public TypeSalle getType() { return type; }
    public void setType(TypeSalle type) { this.type = type; }
    public boolean isDisponible() { return disponible; }
    public Set<Reservation> getReservations() { return Collections.unmodifiableSet(reservations); }
    public Set<Equipement> getEquipements() { return Collections.unmodifiableSet(equipements); }
}
