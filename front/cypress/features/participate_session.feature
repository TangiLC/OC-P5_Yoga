Feature: Gestion de la participation aux sessions par Utilisateur

  Scenario: Participation session
    Given je suis un Utilisateur authentifié

    And je suis sur la page /sessions

    When je clique sur le lien "Detail" d'une carte session

    Then je suis redirigé vers la page /sessions/detail
    And le titre contient le nom de la session
    And le bouton mat-warn "Do not participate" n'existe pas
    And

    When je clique sur le bouton mat-primary "Participate"

    Then la participation est enregistrée
    And le bouton mat-primary "Participate" n'existe plus
    And le bouton mat-warn "Do not participate" existe
    And la liste des attendees augmente de 1

  Scenario: Annulation de participation à une session
    Given je suis un Utilisateur authentifié

    And je suis sur la page /sessions
    And je suis déjà inscrit à la session 1

    When je clique sur le lien "Detail" de la session 1

    Then je suis redirigé vers la page /sessions/detail
    And le titre contient le nom de la session

    When je clique sur le bouton "Do not participate"

    Then la participation à la session est supprimée
    And le bouton mat-primary "Participate" existe
    And le bouton mat-warn "Do not participate" n'existe plus
    And la liste des attendees diminue de 1
