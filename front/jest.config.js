module.exports = {
  moduleNameMapper: {
    '@core/(.*)': '<rootDir>/src/app/core/$1',
  },
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  bail: false,
  verbose: true,
  collectCoverage: true,
  reporters: [
    'default',
    ['jest-stare', {
      resultDir: './coverage/jest/jest-stare',
      reportTitle: 'Test Report',
      additionalResultsProcessors: [],
    }],
  ],
  coverageDirectory: './coverage/jest',
  coverageReporters: ['html', 'lcov', 'text'],
  testPathIgnorePatterns: ['<rootDir>/node_modules/'],
  coveragePathIgnorePatterns: ['<rootDir>/node_modules/'],
  coverageThreshold: {
    global: {
      statements: 85
    },
  },
  roots: [
    "<rootDir>"
  ],
  modulePaths: [
    "<rootDir>"
  ],
  moduleDirectories: [
    "node_modules"
  ],
  testMatch: ['**/*.spec.ts'],
};
