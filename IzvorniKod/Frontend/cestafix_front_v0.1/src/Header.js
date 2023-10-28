import React from 'react';
import './Header.css';

const Header = () => {
  const handleNovaPrijava = () => {
    //TODO: Implementirati Unos Nove prijave; ovisi o login/non-login
  };

  const handleCheckStatus = () => {
    //TODO: Promptati Korisnika za Šifru prijave i onda renderati deatalje prijave. 
  };

  const handleAccount = () => {
    //TODO: Renderati Popup sa Formom Za upis detalja
    //      Popup ima opciju Register koja izmjeni formu
    //      Salje POST sa fajlovima tam di oces
  };

  return (
    <header className="header">
      <div className="right">
        <button className="button" onClick={handleNovaPrijava}>Prijavi Štetu!</button>
        <button className="button" onClick={handleCheckStatus}>Provjeri Status Prijave!</button>
        <button className="button" onClick={handleAccount}>Login/Register</button>
      </div>
      <div className="left">
        <img src="logo.png" alt="Logo" className="logo" />
        <h1>CestaFIX</h1>
      </div>
    </header>
  );
};

export default Header;
