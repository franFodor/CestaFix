import {APILogin} from '../API.js'
import './Forms.css'
import Cookies from 'js-cookie'

const LoginFormComponent = () => {
    const handleLogin = (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        APILogin(formData.get("email"), formData.get("password")).then(response => {
            const respJson = response.json();
            const myData = {
                name: respJson.name,
                email: respJson.email,
                citydep: respJson.citydep,
                role: respJson.role,
                userid: respJson.userid
            } // Ispravan format??
            Cookies.set('loginData', JSON.stringify(myData));
            document.querySelector('.loginFail').innerText = '';
            window.location.reload();

            }).catch((error) => {
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
            <input type="text" name="email" required />
            <label htmlFor="password"><b>Lozinka</b></label>
            <input type="password" name="password" required />
            <label htmlFor="remember">Zapamti me</label>
            <input type="checkbox" defaultChecked="checked" name="remember" style={{ marginBottom: '15px' }} />
            <button type="submit" className="login-button">Prijava</button>
        </form>
        <div className="loginFail"></div>
        <div style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
        Zaboravljena lozinka?
        </div>
        </>
    );
    }

export default LoginFormComponent;