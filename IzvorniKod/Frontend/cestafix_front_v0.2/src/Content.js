import React, { useState } from 'react';

import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { useMapEvents } from 'react-leaflet/hooks';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import  {NovaPrijava} from './createReport.js';

const Content = () => {
    const markerIcon = new L.Icon({
        iconUrl: require("./images/R.png"),
        iconSize: [35, 35],
    });

    const [markers, setMarkers] = useState([]);

    const putMarker = (newMarker) => {
        setMarkers([...markers, newMarker]);
    };

    function AddMarker() {
        useMapEvents({
            click(e) {
                putMarker({ geocode: [e.latlng.lat, e.latlng.lng], popup: "Placeholder prijava" });
                NovaPrijava();
            }
        });
        return null;
    }

    return (
        <main className='flex-grow w-full'>
            <MapContainer center={[45.812915, 15.975522]} zoom={13} className='w-full h-full'>
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://api.maptiler.com/maps/basic-v2/256/{z}/{x}/{y}.png?key=jl7SF9AkX5d5T6Di7nm2"
                />
                <AddMarker />
                {markers.map((marker, index) => (
                    <Marker key={index} position={marker.geocode} icon={markerIcon}>
                        <Popup>{marker.popup}
                        </Popup>
                        
                    </Marker>
                ))}
            </MapContainer>
        </main>
    );
}

export default Content;
