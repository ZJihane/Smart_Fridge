package com.example.smart_fridge;

public class User {
    private String activityLevel;
    private String genre;
    private String nom;
    private String poids;
    private String prenom;
    private String taille;
    private String uid;

    private String TDEE ;


    public String getTDEE() {
        return TDEE;
    }

  public void setTDEE(String TDEE){
        this.TDEE=TDEE;
  }
    // Constructeur par défaut
    public User() {}

    // Constructeur avec paramètres
    public User(String nom, String prenom, String uid) {
       this.nom = nom;
       this.prenom = prenom;
       this.uid = uid;
    }

    // Getters et Setters
    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPoids() {
        return poids;
    }

    public void setPoids(String poids) {
        this.poids = poids;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "User{" +
                "activityLevel='" + activityLevel + '\'' +
                ", genre='" + genre + '\'' +
                ", nom='" + nom + '\'' +
                ", poids='" + poids + '\'' +
                ", prenom='" + prenom + '\'' +
                ", taille='" + taille + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}

