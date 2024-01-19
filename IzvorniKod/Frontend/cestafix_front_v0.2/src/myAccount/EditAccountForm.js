import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie';
import './EditAccountForm.css'
import { APIUpdateUser } from '../API';


//Prikazuje Formu za mijenjanje podataka o korisniku
const EditAccountForm = () => {
    const [accountInfo, setAccountInfo] = useState({
        firstname: '',
        lastname: '',
        email: '',
        username: '',
        role: '',
        citydept: { citydeptName: '' }
    });


    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [editing, setEditing] = useState({ firstName: false, lastName: false, email: false });
    
    useEffect(() => { //svaki put kad se nesto od nize navedenih dependacya promijeni onda ponovo postavi accountInfo
        setAccountInfo(prevState => ({
            ...prevState,
            firstname: firstName,
            lastname: lastName,
            email: email,
        }));
    }, [firstName, lastName, email]);
    
    // gettaj usera iz cookiea kad se prvi put rendera forma
    useEffect(() => {
        const userInfo = Cookies.get('userInfo');
        if (userInfo) {
            const parsedInfo = JSON.parse(userInfo);
            setAccountInfo(parsedInfo);
            setFirstName(parsedInfo.firstname);
            setLastName(parsedInfo.lastname);
            setEmail(parsedInfo.email);
        }
    }, []);

    // edita se field polje
    const handleEdit = (field) => {
        setEditing({ ...editing, [field]: true });
    };

    // mijenja se odabrano polje
    const handleChange = (e, field) => {
        const { value } = e.target;
        if (field === 'firstName') setFirstName(value);
        if (field === 'lastName') setLastName(value);
        if (field === 'email') setEmail(value);
    };

    // zove se pri predaji forme
    const handleSubmit = async (e) => {
        e.preventDefault();
        console.log('Updated Info:', { firstName, lastName, email});
        //gettaj Usera za UserId
        let userInfo = Cookies.get('userInfo');
        if(userInfo)userInfo = JSON.parse(userInfo);
        let token = Cookies.get("sessionToken");
        //Updateuser vraca novog usera

        let response =await APIUpdateUser(token,
                      userInfo.userId,
                      firstName,
                      lastName
        );

        //postavi novog usera u cookie i refreshaj stranicu
        Cookies.set("userInfo", JSON.stringify(response));
        window.location.href = '/myAccount';
        setEditing({ firstName: false, lastName: false, email: false });
    };

    //vrati formu
    return (
        <div className="main">
            <div className="account-container">
                <h1 className="centered">Moj račun</h1>
                <form onSubmit={handleSubmit}>
                    <div className="account-details">
                        <p>
                            <strong>Ime:</strong>
                            {editing.firstName ? (
                                <input type="text" value={firstName} onChange={(e) => handleChange(e, 'firstName')} />
                            ) : (
                                firstName
                            )}
                            <button type="button" onClick={() => handleEdit('firstName')}>Edit</button>
                        </p>
                        <p>
                            <strong>Prezime:</strong>
                            {editing.lastName ? (
                                <input type="text" value={lastName} onChange={(e) => handleChange(e, 'lastName')} />
                            ) : (
                                lastName
                            )}
                            <button type="button" onClick={() => handleEdit('lastName')}>Edit</button>
                        </p>
                        {/*<p>
                            <strong>Email:</strong>
                            {editing.email ? (
                                <input type="email" value={email} onChange={(e) => handleChange(e, 'email')} />
                            ) : (
                                email
                            )}
                            <button type="button" onClick={() => handleEdit('email')}>Edit</button>
                        </p>*/}

                        <p>
                        <strong>Uloga:</strong>
                            {accountInfo.role}
                        </p>
                    </div>
                    <button type="submit">Spremi promjene</button>
                </form>
            </div>
        </div>
    );
}

export default EditAccountForm;
