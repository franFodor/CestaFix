import React from 'react'
import Header from './Header';
import Content from './Content';
import Footer from './Footer';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

function App() {
  return (
    <div className='bg-red-900 flex flex-col h-screen'>
      <Header />
      <Content />
      <Footer />
    </div>
  );
}

export default App;