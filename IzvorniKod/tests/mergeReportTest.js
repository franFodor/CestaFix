const { By, Key, Builder, until } = require("selenium-webdriver");

async function prijava() {
  let driver = await new Builder().forBrowser("chrome").build();

  try {
    await driver.get("https://cestafix-fe.onrender.com/");

    const button2Element = await driver.findElement(By.css('#prijava'));
    await button2Element.click();

    const name2Field = await driver.findElement(By.css('#name'));
    await name2Field.sendKeys('Palo stup na kolnik');

    const description2Field = await driver.findElement(By.css('#description'));
    await description2Field.sendKeys('Pao STOP znak tamo di hodaju ljudi');

    const address2Field = await driver.findElement(By.css('#address'));
    await address2Field.sendKeys('Cvjetna 20');

    const submit2Button = await driver.findElement(By.css('.confirmButton'));
    await submit2Button.click();

    await driver.sleep(50000);

    const submit3Button = await driver.findElement(By.css('.loginbtn'));
    await submit3Button.click();

    const buttonElement = await driver.findElement(By.css('#prijava'));
    await buttonElement.click();

    const nameField = await driver.findElement(By.css('#name'));
    await nameField.sendKeys('Palo stup na kolnik');

    const descriptionField = await driver.findElement(By.css('#description'));
    await descriptionField.sendKeys('Pao opet STOP znak tamo di hodaju ljudi jos ga nisu digli');

    const addressField = await driver.findElement(By.css('#address'));
    await addressField.sendKeys('Cvjetna 21');

    const submitButton = await driver.findElement(By.css('.confirmButton'));
    await submitButton.click();

    await driver.sleep(50000);


    const mergeButton = await driver.findElement(By.css('#spojiid'));
    await mergeButton.click();

    await driver.sleep(1000);


    const submit4Button = await driver.findElement(By.css('.loginbtn'));
    await submit4Button.click();

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