export function loginUser_e2eTest() {
  describe('Login User ', () => {
    it('Login success : legit user', () => {
      cy.visit('/login');

      interceptLoginUserSuccess();
      interceptFetchSessionsEmpty();

      cy.get('input[formControlName=email]').type('yoga@studio.com');
      cy.get('input[formControlName=password]').type('test!1234');

      cy.get('button[type="submit"]').click();

      cy.wait('@loginSuccess');
      cy.url().should('include', '/sessions');
      cy.contains('Sessions available').should('be.visible');
    });

    it('Login fail : wrong password', () => {
      cy.visit('/login');

      interceptLoginFailWrongPassword();

      cy.get('input[formControlName=email]').type('jdoe@example.com');
      cy.get('input[formControlName=password]').type('wrongPass00*');

      cy.get('button[type="submit"]').click();

      cy.wait('@loginFail');
      cy.contains('p', 'An error occurred').should('be.visible');
      cy.url().should('include', '/login');
    });

    it('Login fail : unknown user', () => {
      cy.visit('/login');

      interceptLoginFailUnknownUser();

      cy.get('input[formControlName=email]').type('unknown@example.com');
      cy.get('input[formControlName=password]').type('password123*');

      cy.get('button[type="submit"]').click();

      cy.wait('@unknownUser');
      cy.contains('p', 'An error occurred').should('be.visible');
      cy.url().should('include', '/login');
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

  const interceptLoginFailWrongPassword = () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid password' },
    }).as('loginFail');
  };

  const interceptLoginFailUnknownUser = () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 404,
      body: { message: 'User not found' },
    }).as('unknownUser');
  };

  const interceptFetchSessionsEmpty = () => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [],
    }).as('fetchSessions');
  };
}
