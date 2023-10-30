//README:   Line 57/88: složiti pravilnu komunikaciju sa serverom.
//          Ostvariti client-side spremanje ulogiranog korisnika i prilagodba prikaza.



import React, { useState } from 'react';
import './Header.css';


const Header = () => {
  const handleNovaPrijava = () => {
    //////TODO: Implementirati Unos Nove prijave; ovisi o login/non-login
    console.log("tvoja")
  };

  const handleCheckStatus = () => {
    //////TODO: Promptati Korisnika za Šifru prijave i onda renderati deatalje prijave. 
  };

  //-----------------------------------------------------------
  //-------------------LOGIN/REGISTER--------------------------
  const handleAccount = () => {
    //Postavlja Popup u Login stanje i Prikazuje ga.
    setPopupContent(loginForm);
    setShowPopup(true);
  };

  //Za toggle vidljivosti popupa
  const [showPopup, setShowPopup] = useState(false);

  //Za toggle vrste popupa
  const [popupContent, setPopupContent] = useState('');
  let currentForm = 1;


  //Zatvara Popup.
  const closePopup = () => { setShowPopup(false); };

  //Mijenja Popup između Login i Register verzije.
  const toggleForm = () => {
    1 - currentForm ? setPopupContent(loginForm) : setPopupContent(registerForm);
    currentForm = 1 - currentForm;

  }

  //Elementi Login Popupa
  const loginForm = (
    <div>
      <span className="close" onClick={closePopup}>&times;</span>
      <h1>Prijavi se!</h1>

      {/*!!!!!VAŽNO!!!!   Login Form, potrebno dovršiti*/}
      {/*!!!!!VAŽNO!!!!   Login Form, potrebno dovršiti*/}
      <form action="/loginUser" method="post">
        <div className="container">
          <label htmlFor="uname"><b>Korisničko ime</b></label>
          <input type="text" placeholder="Enter Username" name="uname" required />

          <label htmlFor="psw"><b>Lozinka</b></label>
          
          <input type="password" placeholder="Enter Password" name="psw" required />
          <label>
            <input type="checkbox" defaultChecked="checked" name="remember" style={{ marginBottom: '15px' }} /> Zapamti me!
          </label>
          
          <button type="submit" className="login-button">Login</button>
        </div>
      </form>
      <div onClick={toggleForm} style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
        Nemaš račun? Izradi ga!
      </div>
    </div>
  );


  //Elementi Register Popupa 
  const registerForm = (
    <div>
      <span className="close" onClick={closePopup}>&times;</span>
      <h1>Registriraj se!</h1>
      <div>* Za službene račune stupiti u kontakt naveden pri dnu stranice.</div>

      {/*!!!!!VAŽNO!!!!   Register Form, potrebno dovršiti*/}
      {/*!!!!!VAŽNO!!!!   Register Form, potrebno dovršiti*/}
      <form action="/registerUser" style={{ border: '1px solid #ccc' }}>
        <div className="container">
          <label htmlFor="uname">
            <b>Korisničko ime</b>
          </label>
          <input type="text" placeholder="Korisničko ime" name="uname" required />

          <label htmlFor="email">
            <b>E-mail</b>
          </label>
          <input type="text" placeholder="Upiši svoj E-mail" name="email" required />

          <label htmlFor="psw">
            <b>Lozinka</b>
          </label>
          <input type="password" placeholder="Upiši lozinku" name="psw" required />

          <label htmlFor="psw-repeat">
            <b>Ponovi Lozinku</b>
          </label>
          <input type="password" placeholder="Ponovi lozinku" name="psw-repeat" required />

          <label>
            <input type="checkbox" defaultChecked="checked" name="remember" style={{ marginBottom: '15px' }} /> Zapamti me!
          </label>

          <button type="submit" className="signupbtn">Registriraj se!</button>

        </div>
      </form>

      <div onClick={toggleForm} style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
        Već imaš račun? Ulogiraj se!
      </div>
    </div>
  );
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  return (
    <header className="header">

      <div className="left">
        <img src="logo.png" alt="Logo" className="logo" />
        <h1>CestaFIX</h1>
      </div>

      <div className="right">
        <button className="button" onClick={handleNovaPrijava}>Prijavi Štetu!</button>
        <button className="button" onClick={handleCheckStatus}>Provjeri Status Prijave!</button>
        <button className="button" onClick={handleAccount}>Prijavi se!</button>
      </div>


      {/*Popup za Login/Register*/}
      {showPopup && (
        <div className="popup">
          <div className="popup-content">
            {popupContent}
          </div>
        </div>
      )}

    </header>
  );
};

export default Header;
