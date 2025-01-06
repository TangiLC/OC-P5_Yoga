import * as registerCodeCoverageTasks from '@cypress/code-coverage/task';
require('source-map-support').install();

export default (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  try {
    return registerCodeCoverageTasks(on, config);
  } catch (error) {
    console.error('Coverage error:', error);
    return config;
  }
};
