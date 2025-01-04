describe('Login spec', () => {
  it('Login user successfully', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );

    cy.url().should('include', '/sessions');
    cy.contains('Sessions available').should('be.visible');
  });


  it('Login fail : wrong password ', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid password' },
    }).as('loginFail');

    cy.get('input[formControlName=email]').type('jdoe@example.com');
    cy.get('input[formControlName=password]').type('wrongPass00*');

    cy.get('button[type="submit"]').click();

    cy.wait('@loginFail', { timeout: 5000 });

    cy.contains('p', 'An error occurred').should('be.visible');

    cy.url().should('eq', Cypress.config().baseUrl + '/login');
  });


  it('Login fail : unknown user ', () => {
    cy.visit('/login');

    cy.get('input[formControlName=email]').type('unknown@example.com');
    cy.get('input[formControlName=password]').type('password123*');

    cy.get('button[type="submit"]').click();

    cy.contains('p', 'An error occurred').should('be.visible');

    cy.url().should('eq', Cypress.config().baseUrl + '/login');
  });



});
