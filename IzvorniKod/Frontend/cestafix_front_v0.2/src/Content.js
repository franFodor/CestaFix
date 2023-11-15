import React, { useState, useEffect } from 'react';

import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { useMapEvents } from 'react-leaflet/hooks';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import { NovaPrijava } from './createReport.js';

const Content = () => {
    
    const [markers, setMarkers] = useState(initialiseMark());


     async function initialiseMark(){
        const dbMarkers = await fetchMarkers();
        return dbMarkers.map((marker) => (
            <Marker key={marker.problemId} position={marker.geocode} icon={markerIcon}>
                <Popup>{marker.popup}</Popup>
            </Marker>
        ))
    };



    useEffect(() => {
        const fetchAndPopulateMarkers = async () => {
            const dbMarkers = await fetchMarkers();
            dbMarkers.forEach((marker) => {
                console.log("Dodajem Marker>", marker);
            //putMarker({ geocode: [marker.latitude, marker.longitude], popup: "Placeholder prijava" });
            });
        };
        fetchAndPopulateMarkers();
    }, []);


    async function fetchMarkers() {
        return fetch('/api/problems/getAllProblems', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not OK');
                }
                return response.json();
            })
            .catch(error => {
                console.error('There has been a problem with your fetch operation:', error);
            });

    }

    const markerIcon = new L.Icon({
        iconUrl: require("./images/R.png"),
        iconSize: [35, 35],
    });


    const putMarker = (newMarker) => {
        setMarkers([...markers, newMarker]);
        console.log("Markeri u PutMarker> ", markers);
        NovaPrijava();
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
                
            </MapContainer>
        </main>
    );
}

export default Content;
