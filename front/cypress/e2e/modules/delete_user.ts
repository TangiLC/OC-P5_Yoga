import { randomName } from '../utils/e2eUtils';

export function deleteUser_e2eTest() {
describe('Delete User Spec', () => {
  let userId;
  let authToken;

  beforeEach(() => {
    cy.intercept('POST', '/api/auth/register', (req) => {
      req.reply({
        statusCode: 200,
        body: {
          message: 'User registered successfully!',
        },
      });
    }).as('createUser');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        token: 'mock-jwt-token',
        type: 'Bearer',
        id: 8,
        username: 'test@test.com',
        firstName: 'Test',
        lastName: 'User',
        admin: false,
      },
    }).as('login');

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [
        {
          id: 1,
          name: 'Yoga Session',
          date: '2025-01-01',
        },
        {
          id: 2,
          name: 'Meditation Session',
          date: '2025-01-02',
        },
      ],
    }).as('getSession');

    cy.intercept('GET', 'api/user/8', (req) => {
      req.reply({
        id: 8,
        email: 'test@test.com',
        lastName: 'User',
        firstName: 'Test',
        admin: false,
        createdAt: '2025-01-02T12:12:00',
        updatedAt: '2025-01-02T13:13:00',
      });
    }).as('getMe');

    cy.intercept('DELETE', '/api/user/8', (req) => {
      req.reply({
        statusCode: 200,
        body: null,
      });
    }).as('deleteUser');
  });

  it('should delete the account successfully', () => {
    const firstName = `${randomName(5, 2).toLowerCase()}`;
    const lastName = `${randomName(7, 3).toLowerCase()}`;
    const email = `${firstName}${lastName}@test.com`;
    const password = 'Test987!';

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type(firstName);
    cy.get('input[formControlName=lastName]').type(lastName);
    cy.get('input[formControlName=email]').type(email);
    cy.get('input[formControlName=password]').type(password);
    cy.get('button[type="submit"]').click();

    cy.wait('@createUser').then((interception) => {
      expect(interception.response.body.message).to.eq(
        'User registered successfully!'
      );
    });
    cy.url().should('include', '/login');

    cy.get('input[formControlName=email]').type(email);
    cy.get('input[formControlName=password]').type(password + '{enter}{enter}');

    cy.wait('@login').then((interception) => {
      userId = interception.response.body.id;
      authToken = interception.response.body.token;
    });
    cy.url().should('include', '/sessions');

    cy.wait('@getSession');
    cy.get('span.link[routerlink="me"]').contains('Account').click();

    cy.wait('@getMe');
    cy.url().should('include', '/me');

    const deleteButton = 'button.mat-warn';
    cy.get(deleteButton).click();

    cy.wait('@deleteUser').then((interception) => {
      expect(interception.response.body).to.be.null;
    });

    cy.get('simple-snack-bar')
      .should('be.visible')
      .and('contain', 'Your account has been deleted !');
    cy.wait(3001);
    cy.get('simple-snack-bar').should('not.exist');
    cy.url().should('include', '/');
  });
});
}
