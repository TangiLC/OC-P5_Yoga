describe('Logout spec', () => {

  it('logout success', () => {
    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );

    cy.url().should('include', '/sessions');
    cy.contains('Sessions available').should('be.visible');

    cy.get('span.link').contains('Logout').click();

    cy.url().should('eq', Cypress.config().baseUrl + '/');
    cy.contains('Login').should('be.visible');

  });

});
