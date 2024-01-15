import './Forms.css'
import {APIRegister,APIWhoAmI} from '../API.js'
import Cookies from 'js-cookie'

const RegisterFormComponent = () => {
    const validatePassword = (password) => {
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\W).{8,}$/;
        return passwordRegex.test(password);
    };

    const validateEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };


    const handleRegister = (event) => {
        event.preventDefault();
        const form = event.target;
        const lastname = form.lastname.value;
        const firstname = form.firstname.value;
        const email = form.email.value;
        const password = form.password.value;
        const passwordRepeat = form["password-repeat"].value;
        let divElement = document.querySelector('.registerFail');
        
        if (password !== passwordRepeat) {
            divElement.innerText = 'Greška: lozinke se ne podudaraju';
            divElement.style.color = 'red';
            return;
        }

        if (!validateEmail(email)) {
            divElement.innerText = 'Greška: neispravan email format';
            divElement.style.color = 'red';
            return;
        }


        if (!validatePassword(password)) {
            divElement.innerText = 'Greška: lozinka mora sadržavati barem 8 znakova, 1 veliko slovo, 1 malo slovo i specijalni znak.';
            divElement.style.color = 'red';
            return;
        }

        APIRegister(firstname, lastname, email, password).then(response => {
            if (response.status === 403) {
                divElement.innerText = 'Korsnik sa ovim E-Mailom vec postoji!';
                divElement.style.color = 'red';
                return;
            }
            return response.json();
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
                window.location.reload();
            }
        });
        
    }

    return (
        <div>
            <h1>Registracija</h1>
                <form className="form" onSubmit={handleRegister}>
                    <label htmlFor="firstname"><b>Ime</b></label>
                    <input type="text" placeholder="Ime" name="firstname" id="imeid" required />
                    <label htmlFor="lastname"><b>Prezime</b></label>
                    <input type="text" placeholder="Prezime" name="lastname" id="prezimeid" required />
                    <label htmlFor="email"><b>E-mail</b></label>
                    <input type="text" placeholder="Email" name="email" id="emailid" required />
                    <label htmlFor="password"><b>Lozinka</b></label>
                    <input type="password" placeholder="Lozinka" name="password" id="passwordid" required />
                    <label htmlFor="password-repeat"><b>Ponovi Lozinku</b></label>
                    <input type="password" placeholder="Ponovi lozinku" name="password-repeat" id="repatpasswordid" required />
                    <button type="submit" className="signupbtn" id="signupid">Registriraj se</button>
                </form>
            <div className="registerFail"></div>
        </div>
        );
    }

export default RegisterFormComponent;