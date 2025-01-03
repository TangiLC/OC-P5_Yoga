import { extractNumberFromText } from '../utils/e2eUtils';

describe('Gestion de la participation aux sessions', () => {
  it('Participation et annulation à une session', () => {
    let sessionTitle: String;
    let initialAttendees: number;

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('test@test.com');
    cy.get('input[formControlName=password]').type('test1234!{enter}{enter}');
    cy.url().should('include', '/sessions');


    cy.get('.items mat-card')
      .first()
      .within(() => {
        cy.get('mat-card-title')
          .invoke('text')
          .then((text) => {
            sessionTitle = text.trim();
            cy.log(`selected session title : ${sessionTitle}`);
            cy.get('button').contains('Detail').click();
          });
          cy.url().should('include', '/sessions/detail');

          cy.get('[data-testid="attendees"]').invoke('text')

        /*cy.contains('attende')
          .invoke('text')
          .then((text) => {
            initialAttendees = extractNumberFromText(text);
            cy.log(`Initial Attendees number: ${initialAttendees}`);
            expect(initialAttendees).to.not.be.null;
          });*/

        cy.get('button').contains('Do not participate').should('not.exist');
        cy.get('button').contains('Participate').should('exist');
        cy.get('button').contains('Participate').click();

        cy.get('button').contains('Participate').should('not.exist');
        cy.get('button').contains('Do not participate').should('exist');
        cy.get('span')
          .contains('attendees')
          .invoke('text')
          .then((text) => {
            const newAttendees = extractNumberFromText(text);
            expect(newAttendees).to.equal(initialAttendees + 1);
          });

        cy.visit('/sessions');

        //NOT PARTICIPATE
        cy.get('mat-card')
          .filter(`:contains("${sessionTitle}")`)
          .within(() => {
            cy.get('button').contains('Detail').click();
          });

        cy.url().should('include', '/sessions/detail');

        cy.get('button').contains('Do not participate').should('exist');
        cy.get('button').contains('Participate').should('not.exist');
        cy.get('button').contains('Do not participate').click();

        cy.get('button').contains('Do not participate').should('not.exist');
        cy.get('button').contains('Participate').should('exist');
        cy.get('span')
          .contains('attendees')
          .invoke('text')
          .then((text) => {
            const newAttendees = extractNumberFromText(text);
            expect(newAttendees).to.equal(initialAttendees);
          });
      });
  });
});
