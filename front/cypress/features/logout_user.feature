Feature: Déconnection d'un utilisateur

  Scenario: Déconnection utilisateur avec succès [logout_success]
    Given je suis un utilisateur authentifié
    When je clique sur le lien "Logout"

    Then je suis redirigé vers la page /home
    And mon token jwt est révoqué
