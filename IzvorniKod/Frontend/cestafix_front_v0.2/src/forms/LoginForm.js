import { APILogin, APIWhoAmI } from '../API.js'
import './Forms.css'
import Cookies from 'js-cookie'
import { useState } from 'react'

const LoginFormComponent = () => {
    const [isClicked, setIsClicked] = useState(false);
    const handleClick = () => {
        setIsClicked(!isClicked);
    };


    const handleLogin = (event) => {
        event.preventDefault();

        const formData = new FormData(event.target);
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
                    Cookies.set('sessionToken', respJson.token);


                    return APIWhoAmI(respJson.token);
                }
            })
            .then(userInfo => {
                if (userInfo) {
                    Cookies.set('userInfo', JSON.stringify(userInfo));

                    document.querySelector('.loginFail').innerText = '';
                    window.location.reload();
                }
            })
            .catch((error) => {
                let divElement = document.querySelector('.loginFail');
                divElement.innerText = 'Upisani neispravni podatci! Pokušajte ponovo.';
                divElement.style.color = 'red';
                console.log("uhvacen>", error);
            });
    }

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