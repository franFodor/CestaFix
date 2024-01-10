import './Forms.css'
import {APIRegister} from '../API.js'

const RegisterFormComponent = () => {
    const validatePassword = (password) => {
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
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
            return response.json()
        }).then(() => {
            window.location.reload();
        });
        
    }

    return (
        <div>
            <h1>Registracija</h1>
                <form className="form" onSubmit={handleRegister}>
                    <label htmlFor="firstname"><b>Ime</b></label>
                    <input type="text" placeholder="Ime" name="firstname" required />
                    <label htmlFor="lastname"><b>Prezime</b></label>
                    <input type="text" placeholder="Prezime" name="lastname" required />
                    <label htmlFor="email"><b>E-mail</b></label>
                    <input type="text" placeholder="Email" name="email" required />
                    <label htmlFor="password"><b>Lozinka</b></label>
                    <input type="password" placeholder="Lozinka" name="password" required />
                    <label htmlFor="password-repeat"><b>Ponovi Lozinku</b></label>
                    <input type="password" placeholder="Ponovi lozinku" name="password-repeat" required />
                    <label htmlFor="remember">Zapamti me</label>
                    <input type="checkbox" defaultChecked="checked" name="remember" />
                    <button type="submit" className="signupbtn">Registriraj se</button>
                </form>
            <div className="registerFail"></div>
        </div>
        );
    }

export default RegisterFormComponent;