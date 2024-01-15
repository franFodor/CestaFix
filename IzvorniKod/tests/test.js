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

async function register() {

    let driver = await new Builder().forBrowser("chrome").build();

  try {
    await driver.get("https://cestafix-fe.onrender.com/");

    const buttonElement = await driver.findElement(By.css('#login'));
    await buttonElement.click();

    const registerElement = await driver.findElement(By.css('#nemaracun'));
    await registerElement.click();

    const imeField = await driver.findElement(By.css('#imeid'));
    await imeField.sendKeys('Da');

    const prezimeField = await driver.findElement(By.css('#prezimeid'));
    await prezimeField.sendKeys('Vinki');

    const emailField = await driver.findElement(By.css('#emailid'));
    await emailField.sendKeys('da.vinki@gmail.com');

    const passwordField = await driver.findElement(By.css('#passwordid'));
    await passwordField.sendKeys('Vinki.5');

    const passwordrepeatField = await driver.findElement(By.css('#repatpasswordid'));
    await passwordrepeatField.sendKeys('Vinki.5');

    const submitField = await driver.findElement(By.css('#signupid'));
    await submitField.click();


    await driver.sleep(10000);
  } catch (error) {
    console.error("Test failed:", error);
  } finally {
    // Quit the driver
    console.log("Test je prosao uspjesno.");
    await driver.quit();
  }

}

//prijava();
register();