//README:   Line 57/88: složiti pravilnu komunikaciju sa serverom.
//          Ostvariti client-side spremanje ulogiranog korisnika i prilagodba prikaza.



import React, { useState, Component } from 'react';
import './Header.css' ;

import  {NovaPrijava} from './createReport.js';


const Header = () => {
  const handleNovaPrijava = () => {
    NovaPrijava();
    //////TODO: Promptati Korisnika za Šifru prijave i onda renderati deatalje prijave. 
  };

  const handleCheckStatus = () => {
    console.log("huh");
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
  // Funkciju triggera submit login forme. Salje podatke forme na /api/login
  const handleLogin = (e) => {
    e.preventDefault();

    const formData = {
      username: e.target.username.value,
      password: e.target.password.value,
      remember: e.target.remember.checked,
    };

    const formDataJSON = JSON.stringify(formData);

    fetch('/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: formDataJSON,
    }).then((response) => {
        console.log(response);
      })
      .catch((error) => {

      });
};
  // Funkciju triggera submit register forme. Salje podatke forme na /api/register
  const handleRegister = (e) => {
    e.preventDefault();

    const formData = {
      email: e.target.username.value,
      password: e.target.password.value,
      remember: e.target.remember.checked,
    };

    const formDataJSON = JSON.stringify(formData);

    fetch('/api/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: formDataJSON,
    }).then((response) => {
        console.log(response);
      })
      .catch((error) => {

      });
};


  //Elementi Login Popupa
  const loginForm = (
    <div>
      <span className="close" onClick={closePopup}>&times;</span>
      <h1>Prijavi se!</h1>

      {/*!!!!!VAŽNO!!!!   Login Form, potrebno dovršiti*/}
      {/*!!!!!VAŽNO!!!!   Login Form, potrebno dovršiti*/}
      <form onSubmit={handleLogin}>
        <div className="container">
          <label htmlFor="username"><b>Korisničko ime</b></label>
          <input type="text" placeholder="Enter Username" name="username" required />

          <label htmlFor="password"><b>Lozinka</b></label>
          
          <input type="password" placeholder="Enter Password" name="password" required />
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
      <form onSubmit={handleRegister} style={{ border: '1px solid #ccc' }}>
        <div className="container">
          <label htmlFor="username">
            <b>Korisničko ime</b>
          </label>
          <input type="text" placeholder="Korisničko ime" name="username" required />

          <label htmlFor="email">
            <b>E-mail</b>
          </label>
          <input type="text" placeholder="Upiši svoj E-mail" name="email" required />

          <label htmlFor="password">
            <b>Lozinka</b>
          </label>
          <input type="password" placeholder="Upiši lozinku" name="password" required />

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
      <nav className='flex-none grid grid-cols-4 gap-4 px-3 py-3 bg-zinc-700'>
        <div class="justify-start text-3xl font-bold items-center text-white">
          <a href="#">CestaFIX</a>
        </div>
        <ul >
          <li><button class="headerBTN1"
          onClick={handleNovaPrijava}>Prijavi štetu</button></li>
          <li><button class="headerBTN1"
          onClick={handleCheckStatus}>Provjeri status prijave</button></li>
          <li><button class="HeaderBTN2"
          onClick={handleAccount}>Prijavi se</button></li>
        </ul>

        {/*Popup za Login/Register*/}
        {showPopup && (
          <div className="popup">
            <div className="popup-content">
              {popupContent}
            </div>
          </div>
        )}

      </nav>
  );

};

export default Header;