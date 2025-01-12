import {
  connectUser,
  extractNumberFromText,
  randomName,
} from '../utils/e2eUtils';

export function participateSession_e2eTest() {
  describe('Sessions User spec', () => {
    const sessionName1 = `Test Session ${randomName(3, 3)}`;
    const sessionName2 = `Test Session ${randomName(3, 3)}`;
    const mockToken = 'mock-authorization-token';
    let callDetail1Count = 0;
    let callDetail2Count = 0;

    beforeEach(() => {
      interceptLoginUser();
      interceptGetTeachers(mockToken);
      interceptGetSessions(mockToken);
      interceptParticipateSession();
      interceptUnparticipateSession();
      interceptSessionDetails(mockToken);
      connectUser();
    });

    it('should Participate to session', () => {
      let initialAttendeeNumber = 0;

      navigateToSessionDetails(sessionName1);

      cy.get('[data-testid="attendees"]')
        .invoke('text')
        .then((text) => {
          initialAttendeeNumber = extractNumberFromText(text);
          cy.log(`Initial Attendees: ${initialAttendeeNumber}`);
        });

      cy.get('button').contains('Participate').click();
      cy.wait('@getSessionDetails1');
      cy.wait(1000);
      cy.get('button').contains('Do not participate').should('exist');
      cy.get('[data-testid="attendees"]')
        .invoke('text')
        .then((text) => {
          const updatedAttendeeNumber = extractNumberFromText(text);
          expect(updatedAttendeeNumber).to.equal(initialAttendeeNumber + 1);
        });

      navigateToSessionsList();
    });

    it('should Unparticipate from session', () => {
      let initialAttendeeNumber = 0;

      navigateToSessionDetails(sessionName2);
      cy.get('[data-testid="attendees"]')
        .invoke('text')
        .then((text) => {
          initialAttendeeNumber = extractNumberFromText(text);
          cy.log(`Initial Attendees: ${initialAttendeeNumber}`);
        });
      cy.get('button').contains('Do not participate').click();
        cy.wait(900)
      cy.get('button').contains('Participate').should('exist');
      cy.get('[data-testid="attendees"]')
        .invoke('text')
        .then((text) => {
          const updatedAttendeeNumber = extractNumberFromText(text);
          expect(updatedAttendeeNumber).to.equal(initialAttendeeNumber - 1);
        });
    });

    const interceptLoginUser = () => {
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'mock-jwt-token-user',
          type: 'Bearer',
          id: 2,
          username: 'test@test.com',
          firstName: 'User',
          lastName: 'Test',
        },
      }).as('login');
    };

    const interceptGetTeachers = (token) => {
      cy.intercept('GET', '/api/teacher/1', (req) => {
        req.headers['Authorization'] = `Bearer ${token}`;
        req.reply({
          statusCode: 200,
          body: { id: 1, lastName: 'DELAHAYE', firstName: 'Margot' },
        });
      }).as('teacher1');
    };

    const interceptGetSessions = (token) => {
      cy.intercept('GET', '/api/session', (req) => {
        req.headers['Authorization'] = `Bearer ${token}`;
        req.reply({
          statusCode: 200,
          body: [
            {
              id: 1,
              name: sessionName1,
              date: '2025-01-01T00:00:00.000+00:00',
              teacher_id: 1,
              description: 'This is a participate Session',
              users: [],
            },
            {
              id: 2,
              name: sessionName2,
              date: '2025-01-01T00:00:00.000+00:00',
              teacher_id: 1,
              description: 'This is a unparticipate Session',
              users: [2],
            },
          ],
        });
      }).as('getSessions');
    };

    const interceptParticipateSession = () => {
      cy.intercept('POST', '/api/session/1/participate/2', {
        statusCode: 200,
      }).as('participateSession');
    };

    const interceptUnparticipateSession = () => {
      cy.intercept('DELETE', '/api/session/2/participate/2', {
        statusCode: 200,
      }).as('unparticipateSession');
    };

    const interceptSessionDetails = (token) => {
      cy.intercept('GET', '/api/session/1', (req) => {
        req.headers['Authorization'] = `Bearer ${token}`;
        callDetail1Count += 1;
        req.reply({
          statusCode: 200,
          body: {
            id: 1,
            name: sessionName1,
            date: '2025-01-01T00:00:00.000+00:00',
            teacher_id: 1,
            description: 'This is a test Session',
            users: callDetail1Count === 1 ? [] : [2],
          },
        });
      }).as('getSessionDetails1');

      cy.intercept('GET', '/api/session/2', (req) => {
        req.headers['Authorization'] = `Bearer ${token}`;
        callDetail2Count += 1;
        req.reply({
          statusCode: 200,
          body: {
            id: 2,
            name: sessionName2,
            date: '2025-01-01T00:00:00.000+00:00',
            teacher_id: 1,
            description: 'This is a test Session',
            users: callDetail2Count == 1 ? [2] : [],
          },
        });
      }).as('getSessionDetails2');
    };

    const navigateToSessionDetails = (name) => {
      cy.url().should('include', '/sessions');
      cy.get('.items mat-card')
        .filter(`:contains("${name}")`)
        .within(() => {
          cy.get('button').contains('Detail').click();
        });

      cy.url().should('include', '/sessions/detail');
    };

    const navigateToSessionsList = () => {
      cy.get('span.link[routerlink="sessions"]').contains('Sessions').click();
      cy.wait('@getSessions');
      cy.url().should('include', '/sessions');
    };
  });
}
