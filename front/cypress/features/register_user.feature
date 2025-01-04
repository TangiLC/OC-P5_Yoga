Feature: Enregistrer un nouvel utilisateur

  Scenario: Créer un compte utilisateur avec succès
    Given je suis un invité
    And je remplis le formulaire d'enregistrement sur la page /register
    When je saisis les informations suivantes :
      | Champ      | Valeur           |
      | Last name  | Doe              |
      | First name | John             |
      | Email      | jdoe@example.com |
      | Password   | Password123!     |
    And je soumets le formulaire
    Then mon compte utilisateur est créé
    And je suis redirigé vers la page /login

  Scenario: L'adresse email existe déjà dans la base de données
    Given je suis un invité
    And je remplis le formulaire d'enregistrement sur la page /register
    When je saisis les informations suivantes :
      | Champ      | Valeur           |
      | Last name  | Doe              |
      | First name | John             |
      | Email      | jdoe@example.com |
      | Password   | Password123!     |
    And je soumets le formulaire
    Then un message d'erreur "An error occured" est affiché
    And mon compte utilisateur n'est pas créé
