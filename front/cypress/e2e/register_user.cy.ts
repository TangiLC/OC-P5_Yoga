describe('Register spec', () => {
  it('register success', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: { message: 'User created successfully' },
    }).as('registerSuccess');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('jdoe@example.com');
    cy.get('input[formControlName=password]').type('Password123!');

    cy.get('button[type="submit"]').click();

    cy.wait('@registerSuccess');
    cy.get('@registerSuccess').its('response.statusCode').should('eq', 201);

    cy.url().should('include', '/login');
  });

  it('register fail : email already exists', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' },
    }).as('registerEmailExists');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('jdoe@example.com');
    cy.get('input[formControlName=password]').type('Password123!');

    cy.get('button[type="submit"]').click();

    cy.wait('@registerEmailExists', { timeout: 5000 });
    cy.contains('span', 'An error occurred').should('be.visible');

    cy.url().should('include', '/register');
  });

  it('submit fail for invalid inputs', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'An error occured' },
    }).as('badRequest');

    const submitButton = 'button[type="submit"]';

    cy.get('input[formControlName=firstName]').type('Jack');
    cy.get('input[formControlName=lastName]').type('Smith');
    cy.get('input[formControlName=email]').type('jsmith');
    cy.get('input[formControlName=password]').type('Test-987');
    cy.get(submitButton).should('be.disabled');
    cy.get('input[formControlName=email]').type('@test.com');
    cy.get(submitButton).should('not.be.disabled');
    cy.get('input[formControlName=firstName]').clear();
    cy.get(submitButton).should('be.disabled');
    cy.get('input[formControlName=firstName]').type('J');

    cy.get(submitButton).click();
    cy.wait('@badRequest', { timeout: 5000 });
    cy.contains('span', 'An error occurred').should('be.visible');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('Jack');
    cy.get('input[formControlName=lastName]').type('S');
    cy.get('input[formControlName=email]').type('jsmith@test.com');
    cy.get('input[formControlName=password]').type('Test-987');

    cy.get(submitButton).click();
    cy.wait('@badRequest', { timeout: 5000 });
    cy.contains('span', 'An error occurred').should('be.visible');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('Jack');
    cy.get('input[formControlName=lastName]').type('Smith');
    cy.get('input[formControlName=email]').type('jsmith@test.com');
    cy.get('input[formControlName=password]').type('t*');

    cy.get(submitButton).click();
    cy.wait('@badRequest', { timeout: 5000 });
    cy.contains('span', 'An error occurred').should('be.visible');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type(
      'ThisNameIsLongerThanTwenty'
    );
    cy.get('input[formControlName=lastName]').type('Smith');
    cy.get('input[formControlName=email]').type('jsmith@test.com');
    cy.get('input[formControlName=password]').type('Test-987');

    cy.get(submitButton).click();
    cy.wait('@badRequest', { timeout: 5000 });
    cy.contains('span', 'An error occurred').should('be.visible');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('Jack');
    cy.get('input[formControlName=lastName]').type('Smith');
    cy.get('input[formControlName=password]').type('Test-987');
    cy.get('input[formControlName=email]').type(
      'ThisMassiveEmailAddressIsLonger@thanFifty50.characters'
    );

    cy.get(submitButton).click();
    cy.wait('@badRequest', { timeout: 5000 });
    cy.contains('span', 'An error occurred').should('be.visible');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('Jack');
    cy.get('input[formControlName=lastName]').type('Smith');
    cy.get('input[formControlName=email]').type('jsmith@test.com');
    cy.get('input[formControlName=password]').type(
      'ThisMassivePasswordIsLongerThanForty40char!'
    );

    cy.get(submitButton).click();
    cy.wait('@badRequest', { timeout: 5000 });
    cy.contains('span', 'An error occurred').should('be.visible');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('Jack');
    cy.get('input[formControlName=lastName]').type('Smith');
    cy.get('input[formControlName=email]').type('jsmith@test.com');
    cy.get('input[formControlName=password]').type('Test-987');
    //cy.get(submitButton).click();



  });
});
