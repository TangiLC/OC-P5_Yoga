export function userAccount_e2eTest() {
  describe('User account', () => {
    beforeEach(() => {
      interceptLogin();
      interceptAdminProfile();
      interceptUserProfile();
      interceptGetSessionsEmpty();
    });

    it('Displays admin profile', () => {
      cy.visit('/login');
      cy.get('input[formControlName=email]').type('yoga@studio.com');
      cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');

      cy.wait('@login');
      cy.url().should('include', '/sessions');

      cy.get('span.link[routerlink="me"]').contains('Account').click();

      cy.wait('@getAdminProfile');
      cy.url().should('include', '/me');

      cy.contains('h1', 'User information').should('be.visible');
      cy.contains('p', 'Name: Test ADMIN').should('be.visible');
      cy.contains('p', 'Email: yoga@studio.com').should('be.visible');
      cy.contains('p', 'You are admin').should('be.visible');

      cy.get('button.mat-icon-button').click();
      cy.url().should('include', '/sessions');
    });

    it('Displays user profile', () => {
      cy.visit('/login');
      cy.get('input[formControlName=email]').type('test@test.com');
      cy.get('input[formControlName=password]').type('test1234!{enter}{enter}');

      cy.wait('@login');
      cy.url().should('include', '/sessions');

      cy.get('span.link[routerlink="me"]').contains('Account').click();

      cy.wait('@getUserProfile');
      cy.url().should('include', '/me');

      cy.contains('h1', 'User information').should('be.visible');
      cy.contains('p', 'Name: Test USER').should('be.visible');
      cy.contains('p', 'Email: test@test.com').should('be.visible');
      cy.contains('p', 'Delete my account:').should('be.visible');

      cy.get('button.mat-raised-button.mat-warn')
        .contains('Detail')
        .should('be.visible');

      cy.get('button.mat-icon-button').click();
      cy.url().should('include', '/sessions');
    });
  });

  const interceptLogin = () => {
    cy.intercept('POST', '/api/auth/login', (req) => {
      const { email } = req.body;
      if (email === 'yoga@studio.com') {
        req.reply({
          statusCode: 200,
          body: {
            token: 'mock-jwt-token-admin',
            type: 'Bearer',
            id: 1,
            username: 'yoga@studio.com',
            firstName: 'Test',
            lastName: 'ADMIN',
            admin: true,
          },
        });
      } else if (email === 'test@test.com') {
        req.reply({
          statusCode: 200,
          body: {
            token: 'mock-jwt-token-user',
            type: 'Bearer',
            id: 2,
            username: 'test@test.com',
            firstName: 'Test',
            lastName: 'USER',
            admin: false,
          },
        });
      }
    }).as('login');
  };

  const interceptAdminProfile = () => {
    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'yoga@studio.com',
        firstName: 'Test',
        lastName: 'ADMIN',
        admin: true,
        createdAt: '2025-01-01T12:12:00',
        updatedAt: '2025-01-01T13:13:00',
      },
    }).as('getAdminProfile');
  };

  const interceptUserProfile = () => {
    cy.intercept('GET', '/api/user/2', {
      statusCode: 200,
      body: {
        id: 2,
        email: 'test@test.com',
        firstName: 'Test',
        lastName: 'USER',
        admin: false,
        createdAt: '2025-01-02T12:12:00',
        updatedAt: '2025-01-02T13:13:00',
      },
    }).as('getUserProfile');
  };

  const interceptGetSessionsEmpty = () => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [],
    }).as('GetSessions');
  };
}
