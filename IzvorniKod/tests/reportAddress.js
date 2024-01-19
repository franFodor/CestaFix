const { By, Key, Builder, until } = require("selenium-webdriver");

async function prijava() {
  let driver = await new Builder().forBrowser("chrome").build();

  try {
    await driver.get("https://cestafix-fe.onrender.com/");

    const buttonElement = await driver.findElement(By.css('#prijava'));
    await buttonElement.click();

    const nameField = await driver.findElement(By.css('#name'));
    await nameField.sendKeys('Palo stup na kolnik');

    const descriptionField = await driver.findElement(By.css('#description'));
    await descriptionField.sendKeys('Pao STOP znak tamo di hodaju ljudi');

    const addressField = await driver.findElement(By.css('#address'));
    await addressField.sendKeys('Unska 4');

    const submitButton = await driver.findElement(By.css('.confirmButton'));
    await submitButton.click();

    await driver.sleep(40000);

    const confirmButton = await driver.findElement(By.css('.loginbtn'));
    await confirmButton.click();

  } catch (error) {
    console.error("Test failed:", error);
  } finally {
    // Quit the driver
    console.log("Test je prosao uspjesno.");
    await driver.quit();
  }
}

prijava();