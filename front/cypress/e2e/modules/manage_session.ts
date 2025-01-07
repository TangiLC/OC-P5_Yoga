import { connectAdmin, randomName } from '../utils/e2eUtils';
export function manageSession_e2eTest() {
  describe('Sessions Admin spec', () => {
    const sessionName = `${randomName(5, 5)}`;
    const updatedName = `${randomName(5, 5)}`;
    const sessionDescription = `${randomName(2, 2)}  ${randomName(10, 9)}.`;
    const updatedDescription = `${randomName(2, 2)}  ${randomName(10, 9)}.`;
    const sessionDate = '2025-01-01';
    const updatedDate = '2025-01-10';
    const mockToken = 'mock-jwt-token';

    beforeEach(() => {
      interceptLoginAdmin();
      interceptGetTeachers(mockToken);
      interceptGetSessions(mockToken);
      interceptGetSessionDetails(mockToken);
      interceptGetUnknownSessionDetails(mockToken);
      interceptGetTeacherDetails(mockToken);
      interceptCreateSession();
    });

    it('should Display "Page not found" for invalid session', ()=>{
      connectAdmin();
      cy.wait(['@login', '@getSessions']);
      cy.url().should('include', '/sessions');
      cy.visit('/session/999')
      cy.url().should('include', '/404');
      cy.get('h1').should('contain', 'Page not found !');

    })

    it('should Create session by admin', () => {
      connectAdmin();
      cy.wait(['@login', '@getSessions']);
      cy.url().should('include', '/sessions');

      cy.get('button[routerlink="create"]').click();
      cy.wait('@getTeachers');
      cy.url().should('include', '/sessions/create');
      cy.get('h1').should('contain', 'Create session');

      cy.get('input[formControlName="name"]').type(sessionName);
      cy.get('textarea[formControlName="description"]').type(
        sessionDescription
      );
      cy.get('input[formControlName="date"]').type(sessionDate);
      cy.get('mat-select[formControlName="teacher_id"]').click();
      cy.get('mat-option').should('be.visible').first().click();

      cy.get('button[type="submit"]').click();
      cy.wait('@createSession');

      cy.get('simple-snack-bar')
        .should('be.visible')
        .and('contain', 'Session created !');
      cy.wait('@getSessions');
      cy.url().should('include', '/sessions');
    });

    it('should Update session by admin', () => {
      interceptUpdateSession();

      cy.get('mat-card')
        .contains('mat-card-title', 'Test Session 1')
        .parents('mat-card')
        .find('button')
        .contains('Edit')
        .click();
      cy.wait('@getSession1');
      cy.url().should('include', '/sessions/update');
      cy.get('h1').should('contain', 'Update session');

      cy.get('input[formControlName="name"]').clear().type(updatedName);
      cy.get('textarea[formControlName="description"]')
        .clear()
        .type(updatedDescription);
      cy.get('input[formControlName="date"]').clear().type(updatedDate);
      cy.get('mat-select[formControlName="teacher_id"]').click();
      cy.get('mat-option').should('be.visible').last().click();

      cy.get('button[type="submit"]').click();
      cy.wait('@updateSession');

      cy.get('simple-snack-bar')
        .should('be.visible')
        .and('contain', 'Session updated !');
      cy.wait('@getSessions');
      cy.url().should('include', '/sessions');
    });

    it('should Delete session by admin', () => {
      interceptDeleteSession();

      cy.get('mat-card')
        .contains('mat-card-title', 'Test Session 2')
        .parents('mat-card')
        .find('button')
        .contains('Detail')
        .click();

      cy.url().should('include', '/sessions/detail');
      cy.get('h1').should('contain', 'Test Session 2');

      cy.get('button.mat-warn').click();
      cy.wait('@deleteSession');

      cy.get('simple-snack-bar')
        .should('be.visible')
        .and('contain', 'Session deleted !');
      cy.wait('@getSessions');
      cy.url().should('include', '/sessions');
    });
  });

  const interceptLoginAdmin = () => {
    cy.intercept('POST', '/api/auth/login', {
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
    }).as('login');
  };

  const interceptGetTeachers = (token) => {
    cy.intercept('GET', '/api/teacher', (req) => {
      req.headers['Authorization'] = `Bearer ${token}`;
      req.reply({
        statusCode: 200,
        body: [
          { id: 1, lastName: 'DELAHAYE', firstName: 'Margot' },
          { id: 2, lastName: 'THIERCELIN', firstName: 'Hélène' },
        ],
      });
    }).as('getTeachers');
  };

  const interceptGetSessions = (token) => {
    cy.intercept('GET', '/api/session', (req) => {
      req.headers['Authorization'] = `Bearer ${token}`;
      req.reply({
        statusCode: 200,
        body: [
          {
            id: 1,
            name: 'Test Session 1',
            date: '2025-01-01T00:00:00.000+00:00',
            teacher_id: 1,
            description: 'This is a test Session',
          },
          {
            id: 2,
            name: 'Test Session 2',
            date: '2025-01-01T00:00:00.000+00:00',
            teacher_id: 1,
            description: 'This is another test Session',
          },
        ],
      });
    }).as('getSessions');
  };

  const interceptGetSessionDetails = (token) => {
    cy.intercept('GET', '/api/session/1', (req) => {
      req.headers['Authorization'] = `Bearer ${token}`;
      req.reply({
        statusCode: 200,
        body: {
          id: 1,
          name: 'Test Session 2',
          date: '2025-01-01T00:00:00.000+00:00',
          teacher_id: 1,
          description: 'This session will be deleted',
        },
      });
    }).as('getSession1');
  };

  const interceptGetUnknownSessionDetails = (token) => {
    cy.intercept('GET', '/api/session/999', (req) => {
      req.headers['Authorization'] = `Bearer ${token}`;
      req.reply({
        statusCode: 404,
        body: {
        },
      });
    }).as('getSession404');
  };

  const interceptGetTeacherDetails = (token) => {
    cy.intercept('GET', '/api/teacher/1', (req) => {
      req.headers['Authorization'] = `Bearer ${token}`;
      req.reply({
        statusCode: 200,
        body: { id: 1, lastName: 'DELAHAYE', firstName: 'Margot' },
      });
    }).as('getTeacher1');
  };

  const interceptCreateSession = () => {
    cy.intercept('POST', '/api/session', {
      statusCode: 201,
      body: { message: 'Session created !' },
    }).as('createSession');
  };

  const interceptUpdateSession = () => {
    cy.intercept('PUT', '/api/session/1', {
      statusCode: 200,
      body: { message: 'Session updated !' },
    }).as('updateSession');
  };

  const interceptDeleteSession = () => {
    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200,
      body: { message: 'Session deleted !' },
    }).as('deleteSession');
  };
}
