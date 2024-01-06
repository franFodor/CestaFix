import React, { useState, useEffect } from 'react';
import ReportListComponent from './ReportList.js'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { useMapEvents } from 'react-leaflet/hooks';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import './Content.css'
import {APIGetAllProblems} from './API.js'

const Content = ({setPickMarkerLatLon, pickMarkerLatLon}) => {

    function handleMarkerClick(markerData) {
        setSelectedMarkerId(markerData.problemId);
    }

    const [selectedMarkerId, setSelectedMarkerId] = useState(-1);
    const [markers, setMarkers] = useState([]);

    const markerIcon = new L.Icon({
        iconUrl: require("./images/R.png"),
        iconSize: [35, 35],
    });
    const blueMarkerIcon = new L.Icon({
        iconUrl: require("./images/BlueMarker.png"),
        iconSize: [35, 35],
    });

    useEffect(() => {
        const fetchAndPopulateMarkers = async () => {
            const dbMarkers = await APIGetAllProblems();
            setMarkers(dbMarkers.map((marker) => ({
                position: [marker.latitude, marker.longitude],
                popup:

                    <div className='markerPopup'>
                        <p>Problem ID: {marker.problemId}</p>
                        <p>Status: {marker.status}</p>
                        <p>Kategorija: {marker.category || "Nije Dodijeljena!"}</p>
                        <p>Koordinate: {marker.latitude + " " + marker.longitude}</p>
                    </div>,
                icon: markerIcon,
                problemId: marker.problemId
            })));
        };

        fetchAndPopulateMarkers();
    }, []);


    const ClickEventComponent = ({ setPickMarkerLatLon }) => {
        useMapEvents({
            click(e) {
                setPickMarkerLatLon([e.latlng.lat, e.latlng.lng]);
            },
        });
    
        return null; // This component does not render anything
    };
    


    return (
        <div className='main flex-grow w-full'>
            <MapContainer center={[45.812915, 15.975522]} zoom={13} className='w-full h-full'>
                <ClickEventComponent setPickMarkerLatLon={setPickMarkerLatLon} />

                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://api.maptiler.com/maps/basic-v2/256/{z}/{x}/{y}.png?key=jl7SF9AkX5d5T6Di7nm2"
                />
                {markers.map((marker, index) => (
                    <Marker key={index} position={marker.position} icon={marker.icon} eventHandlers={{click: () => handleMarkerClick(marker),}}>
                        <Popup>{marker.popup}</Popup>
                    </Marker>
                ))}
                
                {pickMarkerLatLon &&
                    <Marker key={-1} position={pickMarkerLatLon} icon={blueMarkerIcon}></Marker>
                }




            </MapContainer>
            {selectedMarkerId != -1 && <ReportListComponent problemID={selectedMarkerId} />}
        </div>
    );
}

export default Content;
