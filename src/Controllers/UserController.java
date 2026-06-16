package Controllers;

import Entities.User;
import repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    private final UserRepository userRepository;

    public UserController() throws SQLException {
        this.userRepository = new UserRepository();
    }

    public void index() throws SQLException {

        try {
            List<User> users = userRepository.getAll();

            IO.println("=====================================");
            IO.println("LISTE DES UTILISATEURS");
            IO.println("=====================================");
            if(users.isEmpty()){
                IO.println("La liste des utilisateurs est vide");
            } else {
                for (User user : users) {
                    System.out.println(user);
                }
            }
        } catch (SQLException ex) {
            IO.println("Erreur lors de la recuperation de la liste des utilisateurs.");
        }


    }

     public boolean store() throws SQLException {
        User user = new User();
        IO.println("Votre nom :  ");
        String nom = IO.readln();
        IO.println("Votre email :  ");
        String email = IO.readln();
        IO.println("Votre mot de passe :  ");
        String password = IO.readln();
         IO.println("Votre age : ");
         String ageInput = IO.readln();

         if (ageInput.trim().isEmpty()) {
             ageInput = IO.readln();
         }


         int age = Integer.parseInt(ageInput.trim());


         user.setNom(nom);
        user.setEmail(email);
        user.setAge(age);
        user.setMdp(password);

        return userRepository.create(user);
    }
}
