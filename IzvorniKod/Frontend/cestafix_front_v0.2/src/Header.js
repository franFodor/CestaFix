//README:   Line 57/88: složiti pravilnu komunikaciju sa serverom.
//          Ostvariti client-side spremanje ulogiranog korisnika i prilagodba prikaza.



import React, { useState, Component } from 'react';
import Cookies from 'js-cookie';
import './Header.css';
import './createReport.css';



const Header = () => {
  const handleNovaPrijava = () => {
    setShowReport(true);
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
  const [showReport, setShowReport] = useState(false);

  //Za toggle vrste popupa
  const [popupContent, setPopupContent] = useState('');
  let currentForm = 1;

  //Zatvara Popup.
  const closePopup = () => { setShowPopup(false); };
  const closeReport = () => {setShowReport(false);};

  //Mijenja Popup između Login i Register verzije.
  const toggleForm = () => {
    1 - currentForm ? setPopupContent(loginForm) : setPopupContent(registerForm);
    currentForm = 1 - currentForm;

  }

  const handleLogout = (e) => {
    Cookies.remove('loginData', { path: '/' });
    fetch('/api/logout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(() => {
      window.location.reload();

      //If Logout API fails, refresh the page anyway
    }).catch(() => {
      window.location.reload();
    });
  }



  // Funkciju triggera submit login forme. Salje podatke forme na /api/login
  const handleLogin = (e) => {
    e.preventDefault();

    const formData = {
      email: e.target.username.value,
      password: e.target.password.value,
      //remember: e.target.remember.checked,
    };

    const formDataJSON = JSON.stringify(formData);

    fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: formDataJSON,
    }).then(response => {
      if (!response.ok) {
        throw new Error('Failed Login');
      }
      return response.text();
    }).then(text => {

      const myData = {
        name: formDataJSON.name,
        email: formDataJSON.email,
        citydep: formDataJSON.citydep,
        role: formDataJSON.role,
        userid: formDataJSON.userid
      }

      Cookies.set('loginData', JSON.stringify(myData));
      document.querySelector('.loginFail').innerText = '';
      window.location.reload();
    }).catch((error) => {
      let divElement = document.querySelector('.loginFail');
      divElement.innerText = 'Upisani neispravni podatci! Pokušajte ponovo.';
      divElement.style.color = 'red';
      console.log("uhvacen>", error);
    });

  };
  // Funkciju triggera submit register forme. Salje podatke forme na /api/register
  const handleRegister = (e) => {
    e.preventDefault();

    const formData = {
      username: e.target.username.value,
      email: e.target.email.value,
      password: e.target.password.value,
      remember: e.target.remember.checked,
    };

    const formDataJSON = JSON.stringify(formData);

    fetch('/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: formDataJSON,
    }).then((response) => {
      if (!response.ok) { throw new Error('Failed Register'); }
      Cookies.set('loginData', JSON.stringify("d"));
      window.location.reload();
    })
      .catch((error) => {
        let divElement = document.querySelector('.registerFail');
        divElement.innerText = 'Upisani neispravni podatci! Pokušajte ponovo.';
        divElement.style.color = 'red';
      });
  };


  //Elementi Login Popupa
  let loginForm = (
    <div>
      <span className="close" onClick={closePopup}>&times;</span>
      <h1>Prijavi se!</h1>

      <form onSubmit={handleLogin}>
        <div className="container">
          <label htmlFor="username"><b>E-Mail:</b></label>
          <input type="text" placeholder="Upiši svoj E-mail" name="username" required />

          <label htmlFor="password"><b>Lozinka</b></label>

          <input type="password" placeholder="Upiši svoju lozinku" name="password" required />

          <label>
            <input type="checkbox" defaultChecked="checked" name="remember" style={{ marginBottom: '15px' }} /> Zapamti me!
          </label>

          <button type="submit" className="login-button">Prijavi se!</button>
        </div>
      </form>
      <div className="loginFail"></div>

      <div onClick={toggleForm} style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
        Nemaš račun? Izradi ga!
      </div>
      <div style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
        Zaboravljena lozinka?
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

      <div className="registerFail"></div>
      <div onClick={toggleForm} style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
        Već imaš račun? Ulogiraj se!
      </div>
      <div style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
        Zaboravljena lozinka?
      </div>
    </div>
  );
  //-----------------------------------------------------------
  //-----------------------------------------------------------


  const loginData = Cookies.get('loginData');





  let reportFormHTML = (<div>
    <span className="closeReport" onClick={closeReport}>&times;</span>
    <h1 className="text-2xl font-bold">Prijavi Štetu!</h1>
    <form id="createReport" action="NEEDTOCOMPLETE" method="post" encType="multipart/form-data">
      <div>
        <label htmlFor="name" >Naziv Štete:</label>
        <input id="name" type="text" name="name" required />
      </div>
      <div>
        <label htmlFor="explanation">Kratki Opis: </label>
        <textarea id="explanation" name="explanation" required />
      </div>
      <div>
        <label htmlFor="photo">Dodaj Slike! </label>
        <input id="photo" type="file" name="photo" accept="image/*" multiple />
      </div>
      <div>
        <label htmlFor="coordinates">Geografske Koordinate ili Adresa:</label>
        <input id="coordinates" type="text" name="coordinates" required />
      </div>
      <div>
        <label htmlFor="dropdown">Odaberite Kategoriju štete:       </label>
        <select id="dropdown" name="dropdown">
          <option value="option1">Šteta Na Cesti</option>
          <option value="option2">Sve Ostalo</option>
          {/* --------------------POPRAVIT-------------------- */}
        </select>
      </div>
      <div>
        <input type="submit" value="Submit" className='confirmButton' />
      </div>
    </form>
  </div>
  );

  function getUsername(){
    console.log(">>>",Cookies.get('loginData'));
    return false || "Placeholder";
  }

  return (

    <header className="header">
      <div className="right">
        {(loginData) ? (
          <>
          <button className="headerBTN1" onClick={handleNovaPrijava}>Prijavi Štetu!</button>
          
 <button className="headerBTN1" onClick={handleCheckStatus}>Provjeri Status Prijave!</button>
<div className="dropdown">
 <button className="headerBTN1 dropbtn">{getUsername()}</button>
 <div className="dropdown-content">
   <button className="headerBTNLOGOUT" onClick={handleLogout}>Logout</button>
 </div>
</div>
          </>
        ) : (
          <>
            <button className="headerBTN1" onClick={handleNovaPrijava}>Prijavi Štetu!</button>
            <button className="headerBTN1" onClick={handleCheckStatus}>Provjeri Status Prijave!</button>
            <button className="headerBTN1" onClick={handleAccount}>Login/Register</button>
          </>
        )
        }


      </div >
      <div className="left">
        <h1 className="text-2xl font-bold">CestaFIX</h1>
      </div>

      {/*Popup za Login/Register*/}
      {showPopup && (
        <div className="popup">
          <div className="popup-content">
            {popupContent}
          </div>
        </div>
      )}

      {/*Popup za Reportanje*/}
      {showReport && (
        <div className="newReport" >
          <div className='newReport-content'>
            {reportFormHTML}
          </div></div>
      )}
    </header>







  );

};

export default Header;