const { By, Key, Builder, until } = require("selenium-webdriver");


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
    await emailField.sendKeys('da.vinkiiiiiiiii@gmail.com');

    const passwordField = await driver.findElement(By.css('#passwordid'));
    await passwordField.sendKeys('Vinkii.66');

    const passwordrepeatField = await driver.findElement(By.css('#repatpasswordid'));
    await passwordrepeatField.sendKeys('Vinkii.66');

    const submitField = await driver.findElement(By.css('#signupid'));
    await submitField.click();

    await driver.sleep(5000);

    const accountField = await driver.findElement(By.css("#account"));
    await accountField.click();

    await driver.sleep(2000);

    const deleteField = await driver.findElement(By.css("#brisiid"));
    await deleteField.click();

    await driver.sleep(2000);


    const delete2Field = await driver.findElement(By.css("#brisiaccid"));
    await delete2Field.click();

    await driver.sleep(2000);

    const loginButtonElement = await driver.findElement(By.css('#login'));
    await loginButtonElement.click();

    const username2Field = await driver.findElement(By.css('#username'));
    await username2Field.sendKeys('da.vinkiiiiiiiii@gmail.com');

    const password2Field = await driver.findElement(By.css('#password'));
    await password2Field.sendKeys('Vinkii.66');

    const submit2Button = await driver.findElement(By.css('#submit'));
    await submit2Button.click();


    await driver.sleep(5000);

  } catch (error) {
    console.error("Test failed:", error);
  } finally {
    // Quit the driver
    console.log("Test je prosao uspjesno.");
    await driver.quit();
  }

}

register();