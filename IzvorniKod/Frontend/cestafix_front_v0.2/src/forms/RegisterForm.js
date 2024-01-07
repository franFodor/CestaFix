import './Forms.css'

const RegisterFormComponent = () => {
    const handleRegister = (event) => {
        event.preventDefault();
        console.log("REGISTER")
    }

    return (
        <div>
            <h1>Registracija</h1>
                <form className="form" onSubmit={handleRegister}>
                    <label htmlFor="username"><b>Korisničko ime</b></label>
                    <input type="text" placeholder="Korisničko ime" name="username" required />
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