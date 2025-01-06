import { capitalize, connectAdmin, randomName } from './utils/e2eUtils';

describe('Manage Session', () => {
  it('Création, Modification, suppression de session par administrateur', () => {
    const sessionName = `${randomName(5, 5)}`;
    const updatedName = `${randomName(5, 5)}`;
    const sessionDescription = `${randomName(2, 2)}  ${randomName(10, 9)}.`;
    const updatedDescription = `${randomName(2, 2)}  ${randomName(10, 9)}.`;
    const sessionDate = '2025-01-01';
    const updatedDate = '2025-01-10';
    let initialNumberOfSession;

    connectAdmin()
    cy.url().should('include', '/sessions');
    cy.wait(1000);

    cy.get('mat-card').then((cards) => {
      initialNumberOfSession = cards.length;
      cy.log(`Nombre initial de sessions : ${initialNumberOfSession}`);
    });
    cy.get('button[routerlink="create"]').click();
    cy.url().should('include', '/sessions/create');
    cy.get('h1').should('contain','Create session')

    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="name"]').type(sessionName);
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('textarea[formControlName="description"]').type(sessionDescription);
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('input[formControlName="date"]').type(sessionDate);
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').should('be.visible').first().click();
    cy.get('button[type="submit"]').should('not.be.disabled');
    cy.get('button[type="submit"]').click();

    cy.get('simple-snack-bar')
      .should('be.visible')
      .and('contain', 'Session created !');

    cy.wait(3001);
    cy.get('simple-snack-bar').should('not.exist');

    cy.url().should('include', '/sessions');

    cy.get('mat-card').then((cards) => {
      let newNumberOfSession = cards.length;
      cy.log(`Nombre de sessions (create) : ${newNumberOfSession}`);
      expect(newNumberOfSession).to.equal(initialNumberOfSession + 1);
    });


    //Edit
    cy.get('mat-card')
      .contains('mat-card-title', sessionName)
      .parents('mat-card')
      .find('button')
      .contains('Edit')
      .click();

    cy.url().should('include', '/sessions/update');
    cy.get('h1').should('contain','Update session')

    cy.get('input[formControlName="name"]').clear();
    cy.get('input[formControlName="name"]').type(updatedName);
    cy.get('textarea[formControlName="description"]').clear();
    cy.get('textarea[formControlName="description"]').type(updatedDescription);
    cy.get('input[formControlName="date"]').clear();
    cy.get('input[formControlName="date"]').type(updatedDate);
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').should('be.visible').last().click();

    cy.get('button[type="submit"]').click();

    cy.get('simple-snack-bar')
      .should('be.visible')
      .and('contain', 'Session updated !');

    cy.wait(3001);
    cy.get('simple-snack-bar').should('not.exist');

    cy.url().should('include', '/sessions');
    cy.get('mat-card').then((cards) => {
      let newNumberOfSession = cards.length;
      cy.log(`Nombre de sessions (update) : ${newNumberOfSession}`);
      expect(newNumberOfSession).to.equal(initialNumberOfSession + 1);
    });

    //DELETE
    cy.get('mat-card')
      .contains('mat-card-title', updatedName)
      .parents('mat-card')
      .find('button')
      .contains('Detail')
      .click();

    cy.url().should('include', '/sessions/detail');
    cy.get('h1').should('contain',capitalize(updatedName))

    const deleteButton = 'button.mat-warn';
    cy.get(deleteButton).click();

    cy.get('simple-snack-bar')
      .should('be.visible')
      .and('contain', 'Session deleted !');

    cy.wait(3001);
    cy.get('simple-snack-bar').should('not.exist');

    cy.url().should('include', '/sessions');

    cy.get('mat-card').then((cards) => {
      let newNumberOfSession = cards.length;
      cy.log(`Nombre de sessions (delete) : ${newNumberOfSession}`);
      expect(newNumberOfSession).to.equal(initialNumberOfSession);
    });
  });
});
