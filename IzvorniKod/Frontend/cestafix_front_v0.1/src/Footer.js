import React from 'react';

const Footer = () => {
    const today = new Date();

    return (
        <footer className='w-full flex justify-center px-3 py-2 bg-zinc-700'>
            <p className='text-white font-semibold'>CestaFIX Copyright Free &copy; {today.getFullYear()}</p>
        </footer>
    )
}

export default Footer