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
  cy.wait(500);
}

export function connectUser(): void {
  cy.visit('/login');
  cy.get('input[formControlName=email]').type('test@test.com');
  cy.get('input[formControlName=password]').type('test1234!{enter}{enter}');
  cy.wait(500);
}
