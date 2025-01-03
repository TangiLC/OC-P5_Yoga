export function randomName(minLength: number, randomAdd: number): String {
  const alphabet = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const totalLength = Math.floor(minLength + Math.random() * randomAdd);
  return Array.from(
    { length: totalLength },
    () => alphabet[Math.floor(Math.random() * alphabet.length)]
  ).join('');
}

export function capitalize(str: String): String {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function extractNumberFromText(str: String): number {
  const numberMatch = str.match(/\d+/);
  return numberMatch ? parseInt(numberMatch[0]) : 0;
}
