export function randomName(minLength: number, randomAdd: number): String {
  const alphabet = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const totalLength = Math.floor(minLength + Math.random() * randomAdd);
  return Array.from(
    { length: totalLength },
    () => alphabet[Math.floor(Math.random() * alphabet.length)]
  ).join('');
}

export function capitalize(str: String): String {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function extractNumberFromText(str: String): number {
  const numberMatch = str.match(/\d+/);
  return numberMatch ? parseInt(numberMatch[0], 10) : 0;
}

export function connectAdmin(): void {
  cy.visit('/login');
  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');
  cy.wait(500)
}

export function connectUser(): void {
  cy.visit('/login');
  cy.get('input[formControlName=email]').type('test@test.com');
  cy.get('input[formControlName=password]').type('test1234!{enter}{enter}');
  cy.wait
}

export function createTestSession(sessionName: string): void {
  connectAdmin();
  cy.url().should('include', '/sessions');
  cy.get('button[routerlink="create"]').click();
  cy.url().should('include', '/sessions/create');
  cy.get('h1').should('contain', 'Create session');

  cy.get('input[formControlName="name"]').type(sessionName);
  cy.get('textarea[formControlName="description"]').type('Test Session');
  cy.get('input[formControlName="date"]').type('2025-01-01');
  cy.get('mat-select[formControlName="teacher_id"]').click();
  cy.get('mat-option').should('be.visible').first().click();
  cy.get('button[type="submit"]').click();

  cy.url().should('include', '/sessions');
  cy.get('span.link').contains('Logout').click();
}

export function deleteTestSession(sessionName: string): void {
  connectAdmin();
  cy.url().should('include', '/sessions');

  cy.get('.items mat-card')
    .filter(`:contains("${sessionName}")`)
    .within(() => {
      cy.get('button').contains('Detail').click();
    });

  const deleteButton = 'button.mat-warn';
  cy.get(deleteButton).click();

  cy.url().should('include', '/sessions');
  cy.get('span.link').contains('Logout').click();
}
