const fs = require("fs");
const path = require("path");
const glob = require("glob");

const testFilesPattern = "./src/**/*.spec.ts";

const unitTestRegex = /\/\/\s*@unit-test/g;
const integrationTestRegex = /\/\/\s*@integrat-test/g;

function analyzeFile(filePath) {
  const content = fs.readFileSync(filePath, "utf-8");
  const unitTests = (content.match(unitTestRegex) || []).length;
  const integrationTests = (content.match(integrationTestRegex) || []).length;
  const totalTests = unitTests + integrationTests;

  return { unitTests, integrationTests, totalTests };
}

function analyzeFiles() {
  const files = glob.sync(testFilesPattern);
  const report = [];
  let totalUnitTests = 0;
  let totalIntegrationTests = 0;
  let totalTests = 0;

  files.forEach((file) => {
    const {
      unitTests,
      integrationTests,
      totalTests: fileTotalTests,
    } = analyzeFile(file);
    report.push({
      file,
      unitTests,
      integrationTests,
      fileTotalTests,
    });

    totalUnitTests += unitTests;
    totalIntegrationTests += integrationTests;
    totalTests += fileTotalTests;
  });

  const globalRatio = {
    unitTestRatio: (totalUnitTests / totalTests) * 100,
    integrationTestRatio: (totalIntegrationTests / totalTests) * 100,
  };

  return {
    report,
    globalRatio,
    totalUnitTests,
    totalIntegrationTests,
    totalTests,
  };
}

function displayReport() {
  const {
    report,
    globalRatio,
    totalUnitTests,
    totalIntegrationTests,
    totalTests,
  } = analyzeFiles();

  console.log("--- Rapport par fichier ---");
  report.forEach(({ file, unitTests, integrationTests, fileTotalTests }) => {
    console.log(`${file}:`);
    console.log(
      `  Ratio TU: ${unitTests}/${fileTotalTests}=${
        Math.round(1000*unitTests / fileTotalTests)/10 || 0
      }% `
    );
    console.log(
      `  Ratio TI: ${integrationTests}/${fileTotalTests}=${
        Math.round(1000*integrationTests / fileTotalTests)/10 || 0
      }% `
    );
  });

  console.log("\n--- Rapport global ---");

  console.log(
    `Ratio TU: ${totalUnitTests}/${totalTests}=${globalRatio.unitTestRatio.toFixed(
      2
    )}%`
  );
  console.log(
    `Ratio TI: ${totalIntegrationTests}/${totalTests}=${globalRatio.integrationTestRatio.toFixed(
      2
    )}%`
  );
}

displayReport();
