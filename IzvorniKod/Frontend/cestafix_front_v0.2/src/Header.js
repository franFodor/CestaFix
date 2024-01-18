import React, { useState } from 'react';
import Cookies from 'js-cookie';
import './Header.css';
import AccountPopupComponent from './forms/AccountForm.js'
import CheckReportComponent from './forms/CheckReportForm.js'
import { useNavigate } from 'react-router-dom';
import StatisticsComponent from './StatisticsComponent.js';

const Header = ({ pickMarkerLatLon, markers }) => {
  const [isAccountPopupShown, setIsAccountPopupShown] = useState(false);
  const [isStatPopupShown, setIsStatPopupShown] = useState(false);

  const handleReportBtn = () => {
    var div = document.getElementById("OverrideDiv");
    if (div) {
      console.log("showam div,");
      div.style.display = "block";
    }
  }; // Switcha stanje izmedu true i false



  const handleAccountBtn = () => { setIsAccountPopupShown(!isAccountPopupShown); };
  const handleStatBtn = () => { setIsStatPopupShown(!isStatPopupShown); };

  const navigate = useNavigate();
  const handleLogout = () => {
    navigate("/");

    Cookies.remove('sessionToken', { path: '/' });
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
            <button className="headerBTN1" id="prijava" onClick={handleReportBtn}>Prijavi Štetu!</button>
            <button className="headerBTN1" id="statistika" onClick={handleStatBtn}>Statistika Dosadašnjih prijava</button>
            <div className="dropdown reportDropdown">
              <button className="headerBTN1 dropbtn" id="status">Provjeri Status Prijave!</button>
              <div className="dropdown-content">
                <div>
                  {CheckReportComponent()}
                </div>
              </div>
            </div>


            <div className="dropdown">
              <button className="headerBTN1 dropbtn" id="account" onClick={() => window.location.href = '/myAccount'}>{getUsername()}</button>
              <div className="dropdown-content">
                <button className="headerBTNLOGOUT" id="logout" onClick={() => { window.location.href = '/'; handleLogout(); }}>Logout</button>
              </div>
            </div>
          </>
        ) : (
          <>
            <button className="headerBTN1" id="prijava" onClick={handleReportBtn} >Prijavi Štetu!</button>
            <button className="headerBTN1" id="statistika" onClick={handleStatBtn}>Statistika Dosadašnjih prijava</button>
            <div className="dropdown reportDropdown">
              <button className="headerBTN1 dropbtn" id="status">Provjeri Status Prijave!</button>
              <div className="dropdown-content">
                <div>
                  {CheckReportComponent()}
                </div>
              </div>
            </div>
            <button className="headerBTN1" id="login" onClick={handleAccountBtn}>Login/Register</button>
          </>
        )
        }

      </div >
      <div className="left">
        <h1 className="text-2xl font-bold"><a href="/">CestaFIX</a></h1>
      </div>

      {/*Popup za Login/Register*/}
      {isAccountPopupShown && (
        <AccountPopupComponent onClose={handleAccountBtn} />
      )}

     
      {/*Popup za Statistiku*/}
      {isStatPopupShown && (
        <StatisticsComponent onClose={handleStatBtn} />
      )}
    </header>
  );

};

export default Header;