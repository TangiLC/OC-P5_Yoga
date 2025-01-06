import {
  connectUser,
  createTestSession,
  deleteTestSession,
  extractNumberFromText,
  randomName,
} from './utils/e2eUtils';

describe('Gestion de la participation aux sessions', () => {
  const sessionName = `Test Session ${randomName(3, 3)}`;
  beforeEach(() => {
    createTestSession(sessionName);
    connectUser();
  });
  afterEach(() => {
    deleteTestSession(sessionName);
    connectUser();
  });

  it('Participation et annulation à une session', () => {
    let initialAttendeeNumber: number = 0;

    cy.url().should('include', '/sessions');

    cy.get('.items mat-card')
      .filter(`:contains("${sessionName}")`)
      .within(() => {
        cy.get('mat-card-title')
          .invoke('text')
          .then((text) => {
            cy.get('button').contains('Detail').click();
          });
      });
    cy.url().should('include', '/sessions/detail');

    cy.get('[data-testid="attendees"]').should('exist');
    cy.get('[data-testid="attendees"]')
      .invoke('text')
      .then((text) => {
        initialAttendeeNumber = extractNumberFromText(text);
        cy.log(`Initial Attendees : ${initialAttendeeNumber}`);
      });

    cy.get('button').contains('Do not participate').should('not.exist');
    cy.get('button').contains('Participate').should('exist');
    cy.get('button').contains('Participate').click();
    cy.wait(1000);
    cy.get('button').contains('Do not participate').should('exist');
    cy.get('button').contains('Participate').should('not.exist');
    cy.get('[data-testid="attendees"]')
      .invoke('text')
      .then((text) => {
        let updatedAttendeeNumber = extractNumberFromText(text);
        cy.log(`Updated Attendees : ${updatedAttendeeNumber}`);
        expect(updatedAttendeeNumber).to.equal(initialAttendeeNumber + 1);
      });

    cy.get('span.link[routerlink="sessions"]').contains('Sessions').click();

    //NOT PARTICIPATE
    cy.get('.items mat-card')
      .filter(`:contains("${sessionName}")`)
      .within(() => {
        cy.get('button').contains('Detail').click();
      });

    cy.url().should('include', '/sessions/detail');
    cy.wait(1000);
    cy.get('button').contains('Do not participate').should('exist');
    cy.get('button').contains('Participate').should('not.exist');
    cy.get('button').contains('Do not participate').click();
    cy.wait(1000);
    cy.get('button').contains('Participate').should('exist');
    cy.get('button').contains('Do not participate').should('not.exist');
    cy.get('[data-testid="attendees"]')
      .invoke('text')
      .then((text) => {
        let updatedAttendeeNumber = extractNumberFromText(text);
        cy.log(`Updated Attendees : ${updatedAttendeeNumber}`);
        expect(updatedAttendeeNumber).to.equal(initialAttendeeNumber);
      });
  });
});
