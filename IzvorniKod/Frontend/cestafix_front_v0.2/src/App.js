import React, { useState } from 'react';
import Header from './Header';
import Content from './Content';
import Footer from './Footer';
import myAccount from './myAccount';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
  const [pickMarkerLatLon, setPickMarkerLatLon] = useState(null);
  const [markers, setMarkers] = useState([]);

  return (
    <Router>
    <div className='bg-red-900 flex flex-col h-screen'>
      <Header pickMarkerLatLon={pickMarkerLatLon} markers={markers}/ >
      <Routes>
        <Route path="/myAccount" element={<myAccount />} />
        <Route path="/" element={<Content setPickMarkerLatLon={setPickMarkerLatLon} pickMarkerLatLon={pickMarkerLatLon}
                                          markers={markers} setMarkers={setMarkers}/>} />
      </Routes>
      <Footer />
    </div>
    </Router>
  );
}

export default App;