package Entities;

public class User {

    private int id;
    private String nom;
    private String email;
    private String mdp;
    private int age;

    public User() {}

    public User(int id, String nom, String email, String mdp, int age) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.mdp = mdp;
        this.age = age;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return this.nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
    public String getMdp() { return this.mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }
    public int getAge() { return this.age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return String.format("[%d] - %s - %s - %s - %d", id, nom,  email, mdp, age);
    }
}
