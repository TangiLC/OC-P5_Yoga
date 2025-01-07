export function logoutUser_e2eTest() {
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

      cy.contains('Login').should('be.visible');
    });
  });

  const interceptLoginUserSuccess = () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        token: 'mockJwtToken',
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    }).as('loginSuccess');
  };

  const interceptFetchSessionsEmpty = () => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [],
    }).as('fetchSessions');
  };
}
