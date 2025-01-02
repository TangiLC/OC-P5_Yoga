Feature: Suppression du profil utilisateur

  Scenario: Suppression du profil utilisateur
    Given je suis un utilisateur authentifié

    And mon statut Admin est false

    When je clique sur le lien "Account"

    Then je suis redirigé vers la page /me
    And le titre "User information" est affiché
    And Les informations (firstName, lastName, email, Admin)
      de mon compte sont correctement affichées

    When je clique sur le bouton "Detail "

    Then mon compte est supprimé
    And je suis redirigé sur la page de navigation précédente
