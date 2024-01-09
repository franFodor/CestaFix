import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie';
import './MyAccountMain.css'
import userImage from './images/User.png';
import EditAccountForm from './forms/EditAccountForm';
import ReportList from './ReportList';

const MyAccount = () => {
    let loggedUser;
if(Cookies.get('userInfo'))loggedUser = JSON.parse(decodeURIComponent(Cookies.get('userInfo')));
    const [buttonClicked, setButtonClicked] = useState('');

    const handleClick = (buttonLabel) => {
      setButtonClicked(buttonLabel);
    };

    const loggedRoleAccountContent = () => {
        switch(loggedUser ? loggedUser.role : null){
            case 'USER':
                return( 
                <div className='container min-w-full'>
                    <div className='myAccLeft'>
                <img src={userImage} alt="User" />
                <div className='Details'>{loggedUser.firstname +' '+ loggedUser.lastname}</div>
                <button className='confirmButton' style={{display: 'inline-block', width: 'fit-content'}} onClick={() => handleClick('a')}>Uredi Profil</button>
                <button className='confirmButton' style={{display: 'inline-block', width: 'fit-content'}} onClick={() => handleClick('b')}>Pregled Mojih Prijava</button>
                <button className='confirmButton' style={{display: 'inline-block', width: 'fit-content', background: 'red'}} onClick={() => handleClick('c')}>Pobriši Račun!!!</button>
                <button className='confirmButton' style={{display: 'inline-block', width: 'fit-content'}} onClick={() => window.location.href = '/'}>Povratak</button>
              </div>
    
              <div className='myAccRight'>
                {buttonClicked === 'a' && <EditAccountForm />}
                {buttonClicked === 'b' && <ReportList />}
                {buttonClicked === 'c' && <p>Zahtjev za Brisanjem računa odabran.</p>}
              </div></div>);

            case 'ADMIN':
                return <><div>Dodati implementaciju</div></>

            default:
                return <div>Doslo je do greske: Nepostojeci ili neulogirani korisnik.</div>

        }
    }




    return (<>{loggedRoleAccountContent()}</>);
     };

export default MyAccount;
