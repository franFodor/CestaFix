const { By, Key, Builder, until } = require("selenium-webdriver");

// Test sa neispravnim emmailom
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
    await emailField.sendKeys('da.vinkinagmailcom');

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

register();