Feature: Consultation du profil utilisateur

  Scenario: Consultation du profil utilisateur [user_account]
    Given je suis un utilisateur authentifié
    And Mon firstName est test
    And Mon lastName est Admin
    And Mon email est yoga@studio.com
    And mon statut Admin est true

    When je clique sur le lien "Account"

    Then je suis redirigé vers la page /me
    And le titre "User information" est affiché
    And Les informations (firstName, lastName, email, Admin)
      de mon compte sont correctement affichées

    When je clique sur le bouton "<Back "

    Then je suis redirigé sur la page de navigation précédente




