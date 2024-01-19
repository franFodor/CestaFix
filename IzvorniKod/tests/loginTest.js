const { By, Key, Builder, until } = require("selenium-webdriver");

async function prijava() {
  let driver = await new Builder().forBrowser("chrome").build();

  try {
    await driver.get("https://cestafix-fe.onrender.com/");

    const buttonElement = await driver.findElement(By.css('#login'));
    await buttonElement.click();

    const usernameField = await driver.findElement(By.css('#username'));
    await usernameField.sendKeys('filip.simunovic@gmail.com');

    const passwordField = await driver.findElement(By.css('#password'));
    await passwordField.sendKeys('Simiklimi.5');

    const submitButton = await driver.findElement(By.css('#submit'));
    await submitButton.click();

    await driver.sleep(10000);
  } catch (error) {
    console.error("Test failed:", error);
  } finally {
    // Quit the driver
    console.log("Test je prosao uspjesno.");
    await driver.quit();
  }
}

prijava();