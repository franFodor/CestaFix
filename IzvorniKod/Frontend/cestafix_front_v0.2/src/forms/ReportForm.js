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
    const [isClicked, setIsClicked] = useState(false);
    const handleClick = () => {
        console.log("handleam");
        //force 
        var submitButton = document.querySelector('button.login-button');
        submitButton.className = "login-button clicked";

        setIsClicked(!isClicked);
    };


    const handleSubmitReport = async (event) => {
        setIsClicked(true);
        event.preventDefault();
        handleClick();
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
        if (document.getElementById('selectedMarker')) {
            getFinalMapMarker = JSON.parse(document.getElementById('selectedMarker').innerText); //Fetchaj info odabranog markera sa mape
        }
        else { getFinalMapMarker = null; }

        try {
            let checkNearby = await APICheckNearbyReport(data.title,
                data.description,
                data.address,
                data.photo,
                "Čeka Obradu",
                "Čeka Obradu",
                getFinalMapMarker ? getFinalMapMarker[0] : null,
                getFinalMapMarker ? getFinalMapMarker[1] : null,
                data.categoryId,
                null);

            console.log("odčekiram>>>", checkNearby);
            //ukoliko postoji bliski marker, pitaj korisnika jel oce mergat inace samo prijavi bez mergea
            if (checkNearby > 0) {
                setClosestMarkerData(checkNearby);
                setShowMergeConfirm(true);
            } else {
                submitReport(null, data);
            }
        } catch (error) {
            if (error.message === 'Forbidden') {

                var submitButton = document.querySelector('button.login-button');
                submitButton.className = "login-button";
                setReportContent(
                    <>
                        {baseReport}
                        <div style={{ color: 'red' }}>
                            Došlo je do greške, provjerite unos adrese prijave!
                        </div>
                    </>
                );
            }
        }

    }
    function updateRecomendation(n) {
        switch (n) {
            case "1":
                document.getElementById("reccomend").innerText = "Predložen Ured Za Cestovni Promet";
                break;
            case "2":
                document.getElementById("reccomend").innerText = "Predložen Ured Za Komunalije i Vodovodnu";
                break;
            case "3":
                document.getElementById("reccomend").innerText = "Predložen Gradski Ured za Prostorno Uređenje ";
                break;
            case "4":
                document.getElementById("reccomend").innerText = "Predložena Hrvatska Elektroprivreda";
                break;
            case "0":
                document.getElementById("reccomend").innerText = "Predloženo Ministarstvo Rada i\n Mirovinskog Sustava,\n Obitelji i Socijalne Politike";
                break;
            default:
                document.getElementById("reccomend").innerText = "";
        }
    }



    const baseReport = (
        <div className="reportContent" key={isClicked}>
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
                    <button className="signupbtn" onClick={() => { document.getElementById("selectedMarker").innerText = "flag"; onClose(); }}>Odaberite lokaciju na karti!</button>
                </div>

                <div>
                    <b><label htmlFor="dropdown">Odaberite Kategoriju štete</label></b>
                    <select id="dropdown" name="dropdown" onChange={e => updateRecomendation(e.target.value)}>
                        <option value="1">Oštećenje na cesti</option>
                        <option value="2">Oštećenje na vodovodnoj infrastrukturi</option>
                        <option value="3">Oštećenje na zelenim površinama</option>
                        <option value="4">Oštećenje na eletroenergetskoj infrastrukturi</option>
                        <option value="0">Ostalo</option>
                    </select>
                    <div id='reccomend' className='recommendation'>Predložen Ured Za Cestovni Promet</div>
                </div>
                <button id="submitSpecial" type="submit" className={`login-button ${isClicked ? 'clicked' : ''}`}  >Prijavi Štetu!</button>
            </form>
        </div>
    );

    const [reportContent, setReportContent] = useState(
        <div className="reportContent" key={isClicked}>
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
                    <button type="button" className="signupbtn" onClick={() => { document.getElementById("selectedMarker").innerText = "flag"; onClose(); }}>Odaberite lokaciju na karti!</button>
                </div>

                <div>
                    <b><label htmlFor="dropdown">Odaberite Kategoriju štete</label></b>
                    <select id="dropdown" name="dropdown" onChange={e => updateRecomendation(e.target.value)}>
                        <option value="1">Oštećenje na cesti</option>
                        <option value="2">Oštećenje na vodovodnoj infrastrukturi</option>
                        <option value="3">Oštećenje na zelenim površinama</option>
                        <option value="4">Oštećenje na eletroenergetskoj infrastrukturi</option>
                        <option value="0">Ostalo</option>
                    </select>
                    <div id='reccomend'>Predložen Ured Za Cestovni Promet</div>
                </div>
                <button type="submit" id='submit' className={`login-button`} onClick={handleClick}>Prijavi Štetu!</button>
            </form>
        </div>);


    const submitReport = (closest_problem_id, data) => {

        let reportData;
        if (data) {
            reportData = data;
        }
        let getFinalMapMarker = JSON.parse(document.getElementById('selectedMarker').innerText); //Fetchaj info odabranog markera sa mape
        let token;
        if (Cookies.get('sessionToken')) token = Cookies.get('sessionToken');
        else { token = null; }
        if (reportData) {
            APICreateReport(token,
                reportData.title,
                reportData.description,
                reportData.address,
                reportData.photo,
                "Čeka Obradu",
                "Čeka Obradu",
                getFinalMapMarker ? getFinalMapMarker[0] : null,
                getFinalMapMarker ? getFinalMapMarker[1] : null,
                reportData.categoryId,
                closest_problem_id).then(response => {
                    if (response.status === 200) {
                        return response.json(); // Parse the response body as JSON
                    } else {
                        handleClick();
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
                        console.log(apiResponse);
                        setReportContent(
                            <div>
                                <h2>Prijava je uspješno prijavljena!</h2>
                                <p>Id vaše prijave je:</p>
                                <p>{apiResponse.businessId}</p>
                                <br></br>
                                <button className={`login-button ${isClicked ? 'clicked' : ''}`} onClick={() => window.location.reload()}>Potvrdi</button>
                            </div>
                        );

                    }
                });

        }
    };


    const mergeConfirmDialog = showMergeConfirm && (
        <div className="mergeConfirmDialog">
            <h3>Već postoji bliska prijava. Želite li da se:</h3>
            <button id="spojiid" onClick={() => { setShowMergeConfirm(false); submitReport(closestMarkerData, reportData); }}>spoji s postojećom</button>
            <button onClick={() => { setShowMergeConfirm(false); submitReport(null, reportData); }}>Stvori novu prijavu</button>
            <button onClick={() => { setShowMergeConfirm(false); onClose(); }}>Odustani</button>
        </div>
    );

    return (
        <PopupComponent onClose={onClose}>
            {mergeConfirmDialog || reportContent}
        </PopupComponent>
    );
}

export default ReportPopupComponent;