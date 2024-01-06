


import React, { useState, Component } from 'react';
import Cookies from 'js-cookie';
import './Header.css';
import AccountPopupComponent from './forms/AccountForm.js'
import ReportPopupComponent from './forms/ReportForm.js'
import CheckReportComponent from './forms/CheckReportForm.js'
import { useNavigate } from 'react-router-dom';

const Header = ({pickMarkerLatLon, markers}) => {
  const [isAccountPopupShown, setIsAccountPopupShown] = useState(false);
  const [isReportPopupShown, setIsReportPopupShown] = useState(false);

  const handleReportBtn = () => { setIsReportPopupShown(!isReportPopupShown); }; // Switcha stanje izmedu true i false
  const handleAccountBtn = () => { setIsAccountPopupShown(!isAccountPopupShown); };


  const handleLogout = () => {
    Cookies.remove('userInfo', { path: '/' });
    window.location.reload();
  }


  function getUsername() {
    console.log(">>>", Cookies.get('userInfo'));
    let loggedUser = JSON.parse(decodeURIComponent(Cookies.get('userInfo')));
    return loggedUser.firstname;
  }

  return (

    <header className="header">
      <div className="right">
        {(Cookies.get('userInfo')) ? (
          <>
            <button className="headerBTN1" onClick={handleReportBtn}>Prijavi Štetu!</button>
            <div className="dropdown reportDropdown">
              <button className="headerBTN1 dropbtn">Provjeri Status Prijave!</button>
              <div className="dropdown-content">
                <div>
                  {CheckReportComponent()}
                </div>
              </div>
            </div>


            <div className="dropdown">
              <button className="headerBTN1 dropbtn" onClick={() => window.location.href = '/myAccount'}>{getUsername()}</button>
              <div className="dropdown-content">
                <button className="headerBTNLOGOUT" onClick={handleLogout}>Logout</button>
              </div>
            </div>
          </>
        ) : (
          <>
            <button className="headerBTN1" onClick={handleReportBtn}>Prijavi Štetu!</button>
            <div className="dropdown reportDropdown">
              <button className="headerBTN1 dropbtn">Provjeri Status Prijave!</button>
              <div className="dropdown-content">
                <div>
                  {CheckReportComponent()}
                </div>
              </div>
            </div>
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
        <AccountPopupComponent onClose={handleAccountBtn} />
      )}

      {/*Popup za Reportanje*/}
      {isReportPopupShown && (
        <ReportPopupComponent onClose={handleReportBtn} pickMarkerLatLon={pickMarkerLatLon} markers={markers} />
      )}
    </header>
  );

};

export default Header;