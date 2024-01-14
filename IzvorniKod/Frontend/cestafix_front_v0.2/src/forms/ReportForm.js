import React, { useRef, useState, useEffect } from 'react';
import PopupComponent from "../PopupComponent.js";
import "./Forms.css";
import { APICreateReport, APICheckNearbyReport } from "../API.js";
import Cookies from 'js-cookie';

function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => {
            const base64String = reader.result.split(',')[1];
            resolve(base64String);

        }
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
}

const ReportPopupComponent = ({ onClose, pickMarkerLatLon, markers }) => {
    const [showMergeConfirm, setShowMergeConfirm] = useState(false);
    const [closestMarkerData, setClosestMarkerData] = useState(null);
    const [reportData, setReportData] = useState(null);


    const handleSubmitReport = async (event) => {
        event.preventDefault();
        //Parsanje Report Objekta iz Forma
        const formData = new FormData(event.target);
        let photos = [];
        const photoFiles = formData.getAll("photo");
        for (let file of photoFiles) {
            if (file.size > 0) {
                const base64String = await fileToBase64(file);
                photos.push(base64String);
            }

        }
        const data = {
            title: formData.get("name"),
            description: formData.get("description"),
            address: formData.get("address") !== "" ? formData.get("address") : null,
            categoryId: formData.get("dropdown"),
            photo: photos.length > 0 ? photos : null,
            token: Cookies.get("sessionToken") || null
        };
        //Postavi Parsani objekt u Hook
        setReportData(data);

        //neispravna implementacija!! let closestMarker = getNearbyMarker(pickMarkerLatLon, data.categoryId);
        let getFinalMapMarker;
        if(document.getElementById('selectedMarker')){
             getFinalMapMarker= JSON.parse(document.getElementById('selectedMarker').innerText); //Fetchaj info odabranog markera sa mape
        }
        else { getFinalMapMarker = null;}
        
        let checkNearby = await APICheckNearbyReport(data.title,
            data.description,
            data.address,
            data.photo,
            "U obradi",
            "U obradi",
            getFinalMapMarker ? getFinalMapMarker[0] : null,
            getFinalMapMarker ? getFinalMapMarker[1] : null,
            data.categoryId,
            null);
        console.log("odčekiram>>>",checkNearby);

        //ukoliko postoji bliski marker, pitaj korisnika jel oce mergat inace samo prijavi bez mergea
        if (checkNearby) {
            setClosestMarkerData(checkNearby);
            setShowMergeConfirm(true);
        } else {
            submitReport(null);
        }
    };

    const baseReport = (
        <div className="reportContent" >
            <form className="form" onSubmit={handleSubmitReport}>
                <div>
                    <b><label htmlFor="name">Naslov</label></b>
                    <input id="name" type="text" name="name" required />
                </div>
                <div>
                    <b><label htmlFor="description">Kratki Opis</label></b>
                    <textarea id="description" name="description" required />
                </div>
                <div>
                    <b><label htmlFor="photo">Dodaj Slike</label></b>
                    <input id="photo" type="file" name="photo" accept="image/*" multiple />
                </div>
                <div>
                    <b><label htmlFor="address">Adresa Prijave:</label></b>
                    <input
                        id="address"
                        type="text"
                        name="address"
                    />
                </div>
                <div id='reportOnMap'>
                    <button className="signupbtn" onClick={() => {document.getElementById("selectedMarker").innerText = "flag";onClose(); }}>Odaberite lokaciju na karti!</button>
                </div>


                <div>
                    <b><label htmlFor="dropdown">Odaberite Kategoriju štete</label></b>
                    <select id="dropdown" name="dropdown">
                        <option value="1">Oštećenje na cesti</option>
                        <option value="2">Oštećenje na vodovodnoj infrastrukturi</option>
                        <option value="3">Oštećenje na zelenim površinama</option>
                        <option value="4">Oštećenje na eletroenergetskoj infrastrukturi</option>
                        <option value="0">Ostalo</option>

                    </select>
                </div>
                <input type="submit" className="confirmButton" value="Submit" />
            </form>
        </div>
    );

    const [reportContent, setReportContent] = useState(baseReport);


    const submitReport = (closest_problem_id) => {
        let getFinalMapMarker = JSON.parse(document.getElementById('selectedMarker').innerText); //Fetchaj info odabranog markera sa mape
        APICreateReport(reportData.token,
            reportData.title,
            reportData.description,
            reportData.address,
            reportData.photo,
            "U obradi",
            "U obradi",
            getFinalMapMarker ? getFinalMapMarker[0] : null,
            getFinalMapMarker ? getFinalMapMarker[1] : null,
            reportData.categoryId,
            closest_problem_id).then(response => {
                if (response.status === 200) {
                    return response.json(); // Parse the response body as JSON
                } else {
                    setReportContent(
                        <>
                            {baseReport}
                            <div style={{ color: 'red' }}>
                                Došlo je do greške, provjerite unos adrese prijave!
                            </div>
                        </>

                    );
                    console.log("kaj si napravil");
                }
            })
            .then(apiResponse => {
                if (apiResponse) {
                    setReportContent(
                        <div>
                            <h2>Prijava je uspješno prijavljena!</h2>
                            <p>Id vaše prijave je:</p>
                            <p>{apiResponse.businessId}</p>
                            <br></br>
                            <button className='loginbtn' onClick={() => window.location.reload()}>Potvrdi</button>
                        </div>
                    );

                }
            });
    };


    const mergeConfirmDialog = showMergeConfirm && (
        <div className="mergeConfirmDialog">
            <h3>Već postoji bliska prijava. Želite li da se:</h3>
            <button onClick={() => { setShowMergeConfirm(false); submitReport(closestMarkerData); }}>spoji s postojećom</button>
            <button onClick={() => { setShowMergeConfirm(false); submitReport(null); }}>Stvori novu prijavu</button>
            <button onClick={() => { setShowMergeConfirm(false);onClose();}}>Odustani</button>
        </div>
    );



    return (
        <PopupComponent onClose={onClose}>
            {!mergeConfirmDialog && reportContent} {/*mergeConfirmDialog ? reportConent : MergeReportDialog*/}
            {mergeConfirmDialog}
        </PopupComponent>
    );
}

export default ReportPopupComponent;