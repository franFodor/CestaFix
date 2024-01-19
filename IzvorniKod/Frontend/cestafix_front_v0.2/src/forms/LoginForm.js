import { APILogin, APIWhoAmI } from '../API.js'
import './Forms.css'
import Cookies from 'js-cookie'
import { useState } from 'react'
//Upis i handleanje logina, te settanja session i userinfo cookiea
const LoginFormComponent = () => {
    //za prikaz loading circlea na gumbu
    const [isClicked, setIsClicked] = useState(false);
    //togglea stanje izmedu loading/not loading
    const handleClick = () => {
        setIsClicked(!isClicked);
    };

    //zove se pri predaji login forma
    const handleLogin = (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);

        //Zove se Login API sa potrebnim args i hendla se ovisno o responsu
        APILogin(formData.get("email"), formData.get("password"))
            .then(response => {
                if (response.status === 403) {
                    let divElement = document.querySelector('.loginFail');
                    divElement.innerText = 'Prijava nije uspjela.';
                    divElement.style.color = 'red';
                    handleClick();
                } else {
                    return response.json();
                }
            })
            .then(respJson => {
                if (respJson && respJson.token) {
                    //uspjesna prijava, postavi sessionToken i zatrazi user info
                    Cookies.set('sessionToken', respJson.token);
                    return APIWhoAmI(respJson.token);
                }
            })
            .then(userInfo => {
                if (userInfo) {
                    //uspjesno dobiven userinfo, postavi ga u cookie
                    Cookies.set('userInfo', JSON.stringify(userInfo));
                    document.querySelector('.loginFail').innerText = '';
                    //refreshaj stranicu da se vide promjene
                    window.location.reload();
                }
            })
            .catch((error) => {
                //nesto je poslo po zlu
                let divElement = document.querySelector('.loginFail');
                divElement.innerText = 'Upisani neispravni podatci! Pokušajte ponovo.';
                divElement.style.color = 'red';
                console.log("uhvacen>", error);
            });
    }
    //return form html
    return (<>
        <h1>Prijavi se!</h1>

        <form className="form" onSubmit={handleLogin}>
            <label htmlFor="email"><b>Email</b></label>
            <input type="text" name="email" id="username" required />
            <label htmlFor="password"><b>Lozinka</b></label>
            <input type="password" name="password" id="password" required />
            <button type="submit" className={`login-button ${isClicked ? 'clicked' : ''}`} id="submit" onClick={handleClick}>Prijava</button>
        </form>
        <div className="loginFail"></div>
    </>
    );
}

export default LoginFormComponent;