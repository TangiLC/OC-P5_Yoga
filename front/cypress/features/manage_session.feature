Feature: Gestion des sessions par Administrateur

  Scenario: Création de session
    Given je suis un Administrateur authentifié

    And mon statut Admin est true

    When je clique sur le lien "Create"

    Then je suis redirigé vers la page /sessions/create
    And le titre "Create session" est affiché

    When je remplis le formulaire (Name, Description, date, Teacher)
    And je clique sur le bouton "Save "

    Then la session est créée
    And une modale d'information s'affiche 'Session created !'
    And je suis redirigé sur la page /sessions

  Scenario: Modification de session
    Given je suis un Administrateur authentifié

    And mon statut Admin est true
    And je suis sur la page /sessions

    When je clique sur le lien "Edit" d'une session

    Then je suis redirigé vers la page /sessions/update
    And le titre "Update session" est affiché

    When je modifie le formulaire (Name, Description, date, Teacher)
    And je clique sur le bouton "Save "

    Then la session est modifiée
    And une modale d'information s'affiche 'Session updated !'
    And je suis redirigé sur la page /sessions

  Scenario: Suppression de session
    Given je suis un Administrateur authentifié

    And mon statut Admin est true
    And je suis sur la page /sessions

    When je clique sur le lien "Detail" d'une session

    Then je suis redirigé vers la page /sessions/detail
    And le Nom de session est affiché en titre

    When je clique sur le bouton "Delete "

    Then la session est supprimée
    And une modale d'information s'affiche 'Session deleted !'
    And je suis redirigé sur la page /sessions

