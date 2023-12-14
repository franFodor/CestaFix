//README:   Line 57/88: složiti pravilnu komunikaciju sa serverom.
//          Ostvariti client-side spremanje ulogiranog korisnika i prilagodba prikaza.



import React, { useState, Component } from 'react';
import Cookies from 'js-cookie';
import './Header.css';
import AccountPopupComponent from './forms/AccountForm.js'
import ReportPopupComponent from './forms/ReportForm.js'

const Header = () => {
    const [isAccountPopupShown, setIsAccountPopupShown] = useState(false);
    const [isReportPopupShown, setIsReportPopupShown] = useState(false);

    const handleReportBtn = () => {setIsReportPopupShown(!isReportPopupShown);}; // Switcha stanje izmedu true i false
    const handleAccountBtn = () => {setIsAccountPopupShown(!isAccountPopupShown);};

    const handleCheckStatus = (event) => {
        console.log("huh");
        //////TODO: Promptati Korisnika za Šifru prijave i onda renderati deatalje prijave.
    };

    const handleNovaPrijava = (event) => {
        console.log("test");
    };

    const handleLogout = () => {
        Cookies.remove('loginData', { path: '/' });
    }

    const loginData = Cookies.get('loginData');

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
            <button className="headerBTN1" onClick={handleReportBtn}>Prijavi Štetu!</button>
            <button className="headerBTN1" onClick={handleCheckStatus}>Provjeri Status Prijave!</button>
            <button className="headerBTN1" onClick={handleAccountBtn}>Login/Register</button>
          </>
        )
        }

        </div >
        <div className="left">
        <h1 className="text-2xl font-bold">CestaFIX</h1>
        </div>

        {/*Popup za Login/Register*/}
        {isAccountPopupShown && (
        <AccountPopupComponent onClose={handleAccountBtn}/>
        )}

        {/*Popup za Reportanje*/}
        {isReportPopupShown && (
        <ReportPopupComponent onClose={handleReportBtn}/>
        )}
        </header>
        );

};

export default Header;