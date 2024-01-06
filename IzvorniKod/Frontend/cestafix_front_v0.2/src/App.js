import React, { useState } from 'react';
import Header from './Header';
import Content from './Content';
import Footer from './Footer';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

function App() {
  const [pickMarkerLatLon, setPickMarkerLatLon] = useState(null);

  return (
    <div className='bg-red-900 flex flex-col h-screen'>
      <Header pickMarkerLatLon={pickMarkerLatLon}/>
      <Content setPickMarkerLatLon={setPickMarkerLatLon} pickMarkerLatLon={pickMarkerLatLon}/>
      <Footer />
    </div>
  );
}

export default App;
