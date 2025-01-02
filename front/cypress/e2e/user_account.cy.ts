// user_account.spec.js

describe('Feature: Consultation du profil', () => {

  it('Consultation du profil administrateur', () => {

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');

    cy.url().should('include', '/sessions');

    cy.get('span.link[routerlink="me"]').contains('Account').click();

    cy.url().should('include', '/me');

    cy.contains('h1', 'User information').should('be.visible');

    cy.contains('p', 'Name: Test ADMIN').should('be.visible');
    cy.contains('p', 'Email: yoga@studio.com').should('be.visible');
    cy.contains('p', 'You are admin').should('be.visible');

    cy.get('button.mat-icon-button').click();

    cy.url().should('include', '/sessions');
  });


  it('Consultation du profil utilisateur', () => {

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('test@test.com');
    cy.get('input[formControlName=password]').type('test1234!{enter}{enter}');

    cy.url().should('include', '/sessions');

    cy.get('span.link[routerlink="me"]').contains('Account').click();

    cy.url().should('include', '/me');

    cy.contains('h1', 'User information').should('be.visible');

    cy.contains('p', 'Name: Test USER').should('be.visible');
    cy.contains('p', 'Email: test@test.com').should('be.visible');
    cy.contains('p', 'Delete my account:').should('be.visible');

    cy.get('button.mat-raised-button.mat-warn').contains('Detail').should('be.visible');

    cy.get('button.mat-icon-button').click();

    cy.url().should('include', '/sessions');
  });

});
