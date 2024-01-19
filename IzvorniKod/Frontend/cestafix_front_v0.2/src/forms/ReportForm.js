import React, { useRef, useState, useEffect } from 'react';
import PopupComponent from "../PopupComponent.js";
import "./Forms.css";
import { APICreateReport, APICheckNearbyReport } from "../API.js";
import Cookies from 'js-cookie';
//Konvertira sliku u Base64 kako bi se slala u jednom JSON objektu bez vise api callova
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
//Vraca Report Form i implementira potrebno ponasanje pri predaji
const ReportPopupComponent = ({ onClose, pickMarkerLatLon, markers }) => {
    const [showMergeConfirm, setShowMergeConfirm] = useState(false);
    const [closestMarkerData, setClosestMarkerData] = useState(null);
    const [reportData, setReportData] = useState(null);

    //loading Button
    const [isClicked, setIsClicked] = useState(false);
    //Postavi da se gumb loada pri kliku
    const handleClick = () => {
        console.log("handleam");
        //Force ga postavi da se loada, reaktovsko rjesenje ne radi zbog form persistence patcha koji sprijecava rerenderanje forme
        var submitButton = document.querySelector('button.login-button');
        submitButton.className = "login-button clicked";
        setIsClicked(!isClicked);
    };

    //zove se pri predaji forme
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

        //NEISPRAVNO>>>>let closestMarker = getNearbyMarker(pickMarkerLatLon, data.categoryId);

        //zbog ne-renderanja forme, komunikacija izmedu mape i forme se ostvaruje preko skrivenog diva <selectedMarker> ciji innerText sadrzi informacije o lokaciji  
        let getFinalMapMarker;
        if (document.getElementById('selectedMarker')) {
            getFinalMapMarker = JSON.parse(document.getElementById('selectedMarker').innerText); //Fetchaj info odabranog markera sa mape
        }
        else { getFinalMapMarker = null; } //inace nije odabran
        //Provjeri postoji li vremensko-prostorno blizak report
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
            if (error.message === 'Forbidden') { //nemoguce odrediti adresu iz trenutne prijave, sad se moze rerenderati forma

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
    //Funkcija koja nasilno mijenja Div za prijedlog ureda ovisno o kategoriji; nije reaktovski zbog nererenderanja forma
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


    //HTML reporta u varijabli kako se nebi povlacio kroz kod
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

    //malo kontradiktorno, ali Initial reportContent je baseReport
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

    //Funkcija za Stvaranje reporta u bazi
    const submitReport = (closest_problem_id, data) => {

        let reportData;
        if (data) {
            reportData = data;
        }
        let getFinalMapMarker = JSON.parse(document.getElementById('selectedMarker').innerText); //Fetchaj info odabranog markera sa mape
        let token;
        if (Cookies.get('sessionToken')) token = Cookies.get('sessionToken'); //fetchaj sessionToken ako postoji inace null
        else { token = null; }
        if (reportData) {//zovi API za stvaranje reporta sa potrebnim parametrima
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
                        return response.json(); 
                    } else {//ako nije uspio API call, znaci da nije pronadena adresa 
                        handleClick();
                        setReportContent(
                            <>
                                {baseReport}
                                <div style={{ color: 'red' }}>
                                    Došlo je do greške, provjerite unos adrese prijave!
                                </div>
                            </>

                        );
                    }
                })
                .then(apiResponse => {//ako je, prikazi BusinessID i refreshaj stranicu pri confirmationu kako bi se prikazao report na karti
                    if (apiResponse) {
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

    //element koji se prikazuje kad postoji bliski report i pita korisnika da li da radi novi ili da mergea u postojeci problem
    const mergeConfirmDialog = showMergeConfirm && (
        <div className="mergeConfirmDialog">
            <h3>Već postoji bliska prijava. Želite li da se:</h3>
            <button id="spojiid" onClick={() => { setShowMergeConfirm(false); submitReport(closestMarkerData, reportData); }}>spoji s postojećom</button>
            <button onClick={() => { setShowMergeConfirm(false); submitReport(null, reportData); }}>Stvori novu prijavu</button>
            <button onClick={() => { setShowMergeConfirm(false); onClose(); }}>Odustani</button>
        </div>
    );

    //vrati popup sa reportContentom ili Mergeom
    return (
        <PopupComponent onClose={onClose}>
            {mergeConfirmDialog || reportContent}
        </PopupComponent>
    );
}

export default ReportPopupComponent;