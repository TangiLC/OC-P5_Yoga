import { randomName } from "../utils/e2eUtils";

describe('Delete spec', () => {
  it('should create, login, and delete the account successfully', () => {
    const firstName = `${randomName(5,5)}`;
    const lastName = `${randomName(7,7)}`;
    const email = `${firstName}.${lastName}@test.com`;
    const password = 'Test987!';

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type(firstName);
    cy.get('input[formControlName=lastName]').type(lastName);
    cy.get('input[formControlName=email]').type(email);
    cy.get('input[formControlName=password]').type(password);
    cy.wait(900);
    const submitButton = 'button[type="submit"]';
    cy.get(submitButton).click();

    cy.url().should('include', '/login');

    cy.get('input[formControlName=email]').type(email);
    cy.get('input[formControlName=password]').type(password);
    cy.get(submitButton).click();

    cy.url().should('include', '/sessions');

    cy.get('span.link[routerlink="me"]').contains('Account').click();

    cy.url().should('include', '/me');

    const deleteButton = 'button.mat-warn';
    cy.get(deleteButton).click();

    cy.get('simple-snack-bar')
      .should('be.visible')
      .and('contain', 'Your account has been deleted !');

    cy.wait(3001);
    cy.get('simple-snack-bar').should('not.exist');

    cy.url().should('include', '/');
  });
});
