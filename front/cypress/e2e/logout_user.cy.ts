import {
  interceptFetchSessionsEmpty,
  interceptLoginUserSuccess,
} from './utils/intercepts';

describe('Logout spec', () => {
  it('logout success', () => {
    cy.visit('/login');

    interceptLoginUserSuccess();
    interceptFetchSessionsEmpty();

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');

    cy.get('button[type="submit"]').click();

    cy.wait('@loginSuccess');
    cy.url().should('include', '/sessions');

    cy.get('span.link').contains('Logout').click();

    cy.url().should('eq', Cypress.config().baseUrl +'/');
    cy.contains('Login').should('be.visible');
  });
});
