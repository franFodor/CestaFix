import './Forms.css'
import {APIRegister} from '../API.js'

const RegisterFormComponent = () => {
    const handleRegister = (event) => {
        event.preventDefault();
        const form = event.target;
        const lastname = form.lastname.value;
        const firstname = form.firstname.value;
        const email = form.email.value;
        const password = form.password.value;
        const passwordRepeat = form["password-repeat"].value;

        if (password !== passwordRepeat) {
            alert("Loznike se ne podudaraju!");
            return;
        }

        APIRegister(firstname, lastname, email, password);
        window.location.reload();
    }

    return (
        <div>
            <h1>Registracija</h1>
            <div>* Za službene račune stupiti u kontakt naveden pri dnu stranice.</div>
                <form className="form" onSubmit={handleRegister}>
                    <label htmlFor="firstname"><b>Ime</b></label>
                    <input type="text" placeholder="Ime" name="firstname" required />
                    <label htmlFor="lastname"><b>Prezime</b></label>
                    <input type="text" placeholder="Ime" name="lastname" required />
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