package com.example.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Equipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private String marque;

    @Column(length = 500)
    private String description;

    private LocalDate dateAcquisition;

    @Column(nullable = false)
    private boolean enService = true;

    @ManyToMany(mappedBy = "equipements")
    private Set<Salle> salles = new HashSet<>();

    public Equipement() {
    }

    public Equipement(String nom, String marque, String description, LocalDate dateAcquisition) {
        this.nom = nom;
        this.marque = marque;
        this.description = description;
        this.dateAcquisition = dateAcquisition;
    }

    void addSalleInternal(Salle salle) { salles.add(salle); }
    void removeSalleInternal(Salle salle) { salles.remove(salle); }

    public void signalerPanne() { enService = false; }
    public void remettreEnService() { enService = true; }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }
    public boolean isEnService() { return enService; }
    public Set<Salle> getSalles() { return Collections.unmodifiableSet(salles); }
}
