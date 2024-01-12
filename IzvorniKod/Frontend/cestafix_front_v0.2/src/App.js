import React, { useState } from 'react';
import Header from './Header';
import Content from './Content';
import Footer from './Footer';
import MyAccount from './myAccount/MyAccount';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
  const [pickMarkerLatLon, setPickMarkerLatLon] = useState(null);
  const [markers, setMarkers] = useState([]);

  return (
    <Router>
      <div className='flex flex-col h-screen'>
        <Header pickMarkerLatLon={pickMarkerLatLon} markers={markers} />
        <Routes>
          <Route path="/myAccount" element={<MyAccount />} />
          <Route path="/" element={<Content setPickMarkerLatLon={setPickMarkerLatLon} pickMarkerLatLon={pickMarkerLatLon}
            markers={markers} setMarkers={setMarkers} />} />
          <Route path="/prijava/:id" component={Content} />

        </Routes>
        <Footer />
      </div>
    </Router>
  );
}

export default App;