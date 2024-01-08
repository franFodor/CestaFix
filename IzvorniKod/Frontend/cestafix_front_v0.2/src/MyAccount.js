import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie';
import './MyAccountMain.css'
import userImage from './images/User.png';
import EditAccountForm from './forms/EditAccountForm';
import ReportList from './ReportList';

const MyAccount = () => {
    let loggedUser = JSON.parse(decodeURIComponent(Cookies.get('userInfo')));
    const [buttonClicked, setButtonClicked] = useState('');

    const handleClick = (buttonLabel) => {
      setButtonClicked(buttonLabel);
    };

    const loggedRoleAccountContent = () => {
        switch(loggedUser.role){
            case 'USER':
                return( 
                <div className='container'>
                    <div className='myAccLeft'>
                <img src={userImage} alt="User" />
                <div className='Details'>{loggedUser.firstname +' '+ loggedUser.lastname}</div>
                <button className='headerBTNSUBMIT' onClick={() => handleClick('a')}>Uredi Profil</button>
                <button className='headerBTNSUBMIT' onClick={() => handleClick('b')}>Pregled Mojih Prijava</button>
                <button className='headerBTNSUBMIT' onClick={() => handleClick('c')}>Pobriši Račun!!!</button>
                <button className='headerBTNSUBMIT' onClick={() => window.location.href = '/'}>Povratak</button>
              </div>
    
              <div className='myAccRight'>
                {buttonClicked === 'a' && <EditAccountForm />}
                {buttonClicked === 'b' && <ReportList />}
                {buttonClicked === 'c' && <p>Skoro sam ovo implementirao</p>}
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
