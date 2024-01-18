import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie';
import './EditAccountForm.css'
import { APIUpdateUser } from '../API';

const EditAccountForm = () => {
    // State for user account details
    const [accountInfo, setAccountInfo] = useState({
        firstname: '',
        lastname: '',
        email: '',
        username: '',
        role: '',
        citydept: { citydeptName: '' }
    });

    // States for form inputs and editing
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [editing, setEditing] = useState({ firstName: false, lastName: false, email: false });
    
    useEffect(() => {
        setAccountInfo(prevState => ({
            ...prevState,
            firstname: firstName,
            lastname: lastName,
            email: email,
            // Don't store password in accountInfo if it's not needed there
        }));
    }, [firstName, lastName, email]);
    
    // Load user info from cookie on component mount
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

    // Handle field edit
    const handleEdit = (field) => {
        setEditing({ ...editing, [field]: true });
    };

    // Handle change in form fields
    const handleChange = (e, field) => {
        const { value } = e.target;
        if (field === 'firstName') setFirstName(value);
        if (field === 'lastName') setLastName(value);
        if (field === 'email') setEmail(value);
    };

    // Handle form submit
    const handleSubmit = async (e) => {
        e.preventDefault();
        // Here you would typically make an API call to update the user details
        console.log('Updated Info:', { firstName, lastName, email});
        
        let userInfo = Cookies.get('userInfo');
        if(userInfo)userInfo = JSON.parse(userInfo);
        let token = Cookies.get("sessionToken");

        let response =await APIUpdateUser(token,
                      userInfo.userId,
                      firstName,
                      lastName
        );
        Cookies.set("userInfo", JSON.stringify(response));
        window.location.href = '/myAccount';
        setEditing({ firstName: false, lastName: false, email: false });
    };

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
