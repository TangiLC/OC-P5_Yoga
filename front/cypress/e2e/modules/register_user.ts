export function registerUser_e2eTest() {
  describe('Register spec', () => {
    describe('Successful registration', () => {
      it('should register successfully with valid inputs', () => {
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
        cy.url().should('include', '/login');
      });
    });

    describe('Registration failure', () => {
      it('should fail if email already exists', () => {
        cy.visit('/register');

        interceptRegisterEmailExists();

        cy.get('input[formControlName=firstName]').type('John');
        cy.get('input[formControlName=lastName]').type('Doe');
        cy.get('input[formControlName=email]').type('jdoe@example.com');
        cy.get('input[formControlName=password]').type('Password123!');

        cy.get('button[type="submit"]').click();

        cy.wait('@registerEmailExists');
        cy.contains('span', 'An error occurred').should('be.visible');
        cy.url().should('include', '/register');
      });

      it('should fail for invalid email', () => {
        cy.visit('/register');

        interceptRegisterBadRequest();

        cy.get('input[formControlName=firstName]').type('Jack');
        cy.get('input[formControlName=lastName]').type('Smith');
        cy.get('input[formControlName=email]').type('invalid-email');
        cy.get('input[formControlName=password]').type('Test-987');

        cy.get('button[type="submit"]').should('be.disabled');
        cy.get('input[formcontrolName="email"]').should(
          'have.class',
          'ng-invalid'
        );
      });

      it('should fail for missing first name', () => {
        cy.visit('/register');

        cy.get('input[formControlName=lastName]').type('Smith');
        cy.get('input[formControlName=email]').type('jsmith@test.com');
        cy.get('input[formControlName=password]').type('Test-987');
        cy.get('button[type="submit"]').should('be.disabled');
      });
    });

    describe('Edge cases for invalid inputs', () => {
      it('should fail for too long first name', () => {
        cy.visit('/register');

        interceptRegisterBadRequest();

        cy.get('input[formControlName=firstName]').type(
          'ThisNameIsLongerThanTwenty'
        );
        cy.get('input[formControlName=lastName]').type('Smith');
        cy.get('input[formControlName=email]').type('jsmith@test.com');
        cy.get('input[formControlName=password]').type('Test-987');
        cy.get('button[type="submit"]').click();

        cy.wait('@badRequest');
        cy.contains('span', 'An error occurred').should('be.visible');
      });

      it('should fail for too long last name', () => {
        cy.visit('/register');

        interceptRegisterBadRequest();

        cy.get('input[formControlName=lastName]').type(
          'ThisNameIsLongerThanTwenty'
        );
        cy.get('input[formControlName=firstName]').type('Jack');
        cy.get('input[formControlName=email]').type('jsmith@test.com');
        cy.get('input[formControlName=password]').type('Test-987');
        cy.get('button[type="submit"]').click();

        cy.wait('@badRequest');
        cy.contains('span', 'An error occurred').should('be.visible');
      });

      it('should fail for too long email', () => {
        cy.visit('/register');

        interceptRegisterBadRequest();

        cy.get('input[formControlName=firstName]').type('Jack');
        cy.get('input[formControlName=lastName]').type('Smith');
        cy.get('input[formControlName=email]').type(
          'ThisMassiveEmailAddressIsLonger@thanFifty50.characters'
        );
        cy.get('input[formControlName=password]').type('Test-987');
        cy.get('button[type="submit"]').click();

        cy.wait('@badRequest');
        cy.contains('span', 'An error occurred').should('be.visible');
      });

      it('should fail for too long password', () => {
        cy.visit('/register');

        interceptRegisterBadRequest();

        cy.get('input[formControlName=firstName]').type('Jack');
        cy.get('input[formControlName=lastName]').type('Smith');
        cy.get('input[formControlName=email]').type('jsmith@test.com');
        cy.get('input[formControlName=password]').type(
          'ThisMassivePasswordIsLongerThanForty40Characters!'
        );
        cy.get('button[type="submit"]').click();

        cy.wait('@badRequest');
        cy.contains('span', 'An error occurred').should('be.visible');
      });
    });
  });

  const interceptRegisterSuccess = () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: { message: 'User created successfully' },
    }).as('registerSuccess');
  };

  const interceptRegisterEmailExists = () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' },
    }).as('registerEmailExists');
  };

  const interceptRegisterBadRequest = () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'An error occurred' },
    }).as('badRequest');
  };
}
