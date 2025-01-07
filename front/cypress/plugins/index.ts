import * as registerCodeCoverageTasks from '@cypress/code-coverage/task';
require('source-map-support').install();

export default (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  try {
    config.env.codeCoverage={
      exlude:['**/cypress/**/*',
        '**/node_modules/**',
        '**/*.html',
        '**/*.test.js',
        '**/*.spec.ts',
        '**/*.config.ts',
        '**/*.interface.ts',
        '**/environments/**',
        '**/src/**/*.mock.ts',
        '**/src/app/interfaces/**',
        '**/src/app/guards/**',
        '**/src/app/features/auth/interfaces/**',
        '', ],
    }
    return registerCodeCoverageTasks(on, config);
  } catch (error) {
    console.error('Coverage error:', error);
    return config;
  }
};
