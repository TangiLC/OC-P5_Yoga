Feature: Connection d'un utilisateur

  Scenario: Connection utilisateur avec succès [login_success]
    Given je suis un utilisateur enregistré
    And je remplis le formulaire de connection sur la page /login
    When je saisis les informations suivantes :
      | Champ      | Valeur           |
      | Email      | jdoe@example.com |
      | Password   | Password123!     |
    And je soumets le formulaire
    Then mon compte utilisateur est authentifié par token jwt valide
    And je suis redirigé vers la page /sessions
    And je vois la liste des sessions disponibles

  Scenario: Erreur de mot de passe lors de la connection [login_fail_wrong_password]
    Given je suis un utilisateur enregistré
    And je remplis le formulaire de connection sur la page /login
    When je saisis les informations suivantes :
      | Champ      | Valeur           |
      | Email      | jdoe@example.com |
      | Password   | wrongPass00*     |
    And je soumets le formulaire
    Then un message d'erreur "An error occured" est affiché
    And je reste sur la page /login

  Scenario: Erreur d'email utilisateur lors de la connection [login_fail_unknown_user]
    Given je suis un invité
    And je remplis le formulaire de connection sur la page /login
    When je saisis les informations suivantes :
      | Champ      | Valeur           |
      | Email      | bob@test.com     |
      | Password   | password1234     |
    And je soumets le formulaire
    Then un message d'erreur "An error occured" est affiché
    And je reste sur la page /login
