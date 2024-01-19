import React, { useState, useEffect } from 'react';
import ReportListComponent from './ReportList.js'
import ReportPopupComponent from './forms/ReportForm.js';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { useMapEvents } from 'react-leaflet/hooks';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import './Content.css'
import { APIGetAllProblems, APIGetProblemIDFromBusinessId } from './API.js'
import { useParams } from 'react-router-dom';

//Rendera Kartu i handlea prikaz reporta ovisno o routu
const Content = ({ setPickMarkerLatLon, pickMarkerLatLon, markers, setMarkers }) => {
    
    //koordinate o parsanom reportu
    const [optionalCoordinates, setOptionalCoordinates] = useState();

        //Daj mi koordinate od parsanog reporta 
    function getProblemCoordinates(businessId, dbMarkers) {
        for (let problem of dbMarkers) {
            for (let report of problem.reports) {
                if (report.businessId === businessId) {
                    //console.log("Koordinate problema su>", { latitude: problem.latitude, longitude: problem.longitude });
                    return [problem.latitude,problem.longitude];
                }
            }
        }
        return null; // Return null ako ne postoji, nebi nikad trebalo 
    }
    const businessIdActive = useParams(); //dobi ID reporta iz routera
    //console.log(businessIdActive);

    //Otvaranje i zatvaranje ReportForma na ne-reaktovski nacin kako bi se ostvario form persistence
    function closeReport() {
        var div = document.getElementById("OverrideDiv");
        if (div) {
            console.log("hideam div,");
            div.style.display = "none";
            return null;
        }
    } function openReport() {
        var div = document.getElementById("OverrideDiv");
        if (div) {
            console.log("otvaram div,");
            div.style.display = "block";
            return null;
        }
    }

    //definiraj ponasanje klikanja na marker na mapi
    function handleMarkerClick(markerData) {
        setSelectedMarkerId(markerData.problemId);
        setInit(false);
    }
    const [selectedMarkerId, setSelectedMarkerId] = useState(-1);
    const [showReportList, setShowReportlist] = useState(false);
    const [init, setInit] = useState(null);

    const markerIcon = new L.Icon({
        iconUrl: require("./images/R.png"),
        iconSize: [35, 35]
    });
    const blueMarkerIcon = new L.Icon({
        iconUrl: require("./images/BlueMarker.png"),
        iconSize: [35, 35]
    });


    //pri prvom renderanju uhvati sve probleme i reportove i postavi opcionalne koordinate na koordinate parsanog problema od reporta
    useEffect(() => {
        const fetchAndPopulateMarkers = async () => {
            const dbMarkers = await APIGetAllProblems();
            setMarkers(dbMarkers.map((marker) => ({
                position: [marker.latitude, marker.longitude],
                popup:
                    <div className='markerPopup'>
                        <p>Problem ID: {marker.problemId}</p>
                        <p>Status: {marker.status}</p>
                        <p>Kategorija: {marker.category.categoryName || "Nije Dodijeljena!"}</p>
                        <p>Koordinate: {marker.latitude + " " + marker.longitude}</p>
                    </div>,
                icon: markerIcon,
                problemId: marker.problemId,
                markerJSON: marker
            })));
            if (businessIdActive) {
                //console.log("I think im going crazyyy--",businessIdActive.id);
            setOptionalCoordinates(getProblemCoordinates(businessIdActive.id, dbMarkers));
            }
        };
        fetchAndPopulateMarkers();
        loadSelectedReport();
    }, []); 



    const ClickEventComponent = ({ setPickMarkerLatLon }) => {
        useMapEvents({
            click(e) {
                setPickMarkerLatLon([e.latlng.lat, e.latlng.lng]);

                //Kliknuto je na mapu i postavljene su koordinate 
                //Iskomentirat cu i objasnik ovo smece kad se oporavim od fixanja ovog buga
                var div = document.getElementById("selectedMarker");
                //reportOnMap Div -> Logika za mijenjanje Gumba na Reportu u Tekst  
                //
                if (div) {
                    if (div.innerText === "flag") {    //ukoliko je doslo 
                        div.innerText = `[${e.latlng.lat}, ${e.latlng.lng}]`;
                        var div2 = document.getElementById("reportOnMap");
                        if (div2) {
                            div2.innerHTML = '<div>Odabrane Koodinate na mapi!</div>';
                        }
                        openReport();
                    }
                    div.innerText = `[${e.latlng.lat}, ${e.latlng.lng}]`;

                }
                var div2 = document.getElementById("reportOnMap");
                if (div2) {
                    div2.innerHTML = '<div>Odabrane Koodinate na mapi!</div>';
                }
                setShowReportlist(false);
                if (init) window.history.pushState({}, '', '/');
                setInit(false);

            }
        });

        return null; // This component does not render anything
    };

    //dobi problem od buisness reporta  i njegove koordinate
    async function loadSelectedReport() {
        if (businessIdActive.id) {
            let apiResponse = await APIGetProblemIDFromBusinessId(businessIdActive.id);
            //console.log("Trayeni ID:",apiResponse.problem.problemId);
            setSelectedMarkerId(apiResponse.problem.problemId);
            console.log("Inicilajiziran....", selectedMarkerId);
            setInit(apiResponse.problem.problemId);

        }
    }


//vraca kartu i listu reportova za odabrani problem, ako postoji parsani marker, centriraj mapu na njega
    return (
        <div className='main flex-grow w-full'>
            {/*console.log("JA VIDIM",optionalCoordinates)*/}
            <MapContainer key={optionalCoordinates}  center={optionalCoordinates ? [parseFloat(optionalCoordinates[0]), parseFloat(optionalCoordinates[1])] : [45.812915, 15.975522]} zoom={optionalCoordinates ? 20: 13} className='w-full h-full'>

                <ClickEventComponent setPickMarkerLatLon={setPickMarkerLatLon} />

                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://api.maptiler.com/maps/basic-v2/256/{z}/{x}/{y}.png?key=jl7SF9AkX5d5T6Di7nm2"
                />
                {markers.map((marker, index) => (
                    <Marker key={index}
                        position={marker.position}
                        icon={marker.icon}
                        eventHandlers={{ click: () => { handleMarkerClick(marker); setShowReportlist(true); } }}>
                        <Popup>{marker.popup}</Popup>
                    </Marker>
                ))}

                {pickMarkerLatLon && <Marker
                    key={-1}
                    position={pickMarkerLatLon}
                    icon={blueMarkerIcon}
                    eventHandlers={{ click: () => openReport() }}
                />}

            </MapContainer>

            {/*ovo omogucava saveanje nedovrsenog forma pri gasenju pregleda te komunikaciju o odabranom markeru izmedu mape i forme*/}
            <div id="OverrideDiv" style={{ display: 'none' }}>
                <div id='selectedMarker' style={{ display: 'none' }}>false</div>
                <ReportPopupComponent onClose={() => { closeReport(); }} pickMarkerLatLon={null} markers={markers} />
            </div>
             {/*Prikazuje listu reportova za ili parsani problem ili odabrani marker na karti*/}
            {selectedMarkerId !== -1 && (showReportList || init) && <ReportListComponent problemID={init || selectedMarkerId} />}
        </div>
    );
}



export default Content;