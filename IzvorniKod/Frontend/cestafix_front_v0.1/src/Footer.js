import React from 'react';
import './Footer.css';

const Footer = () => {
    const today = new Date();

    return (
        <footer>
            <p>CestaFIX Copyright Free &copy; {today.getFullYear()}</p>
        </footer>
    )
}

export default Footer