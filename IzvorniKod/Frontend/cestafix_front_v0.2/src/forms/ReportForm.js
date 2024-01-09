import React, { useState } from 'react';
import PopupComponent from "../PopupComponent.js";
import "./Forms.css";
import { APICreateReport } from "../API.js";
import Cookies from 'js-cookie';

function haversineDistance(latlon1, latlon2) {
    const toRad = x => x * Math.PI / 180;
    const dLat = toRad(latlon2[0] - latlon1[0]);
    const dLon = toRad(latlon2[1] - latlon1[1]);
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(toRad(latlon1[0])) * Math.cos(toRad(latlon2[0])) *
              Math.sin(dLon / 2) * Math.sin(dLon / 2);
    return 6371e3 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

const ReportPopupComponent = ({ onClose, pickMarkerLatLon, markers }) => {
    const [showMergeConfirm, setShowMergeConfirm] = useState(false);
    const [closestMarkerData, setClosestMarkerData] = useState(null);
    const [reportData, setReportData] = useState(null);

    const getNearbyMarker = (latlon, categoryId) => {
        let closestMarker = null;
        let minDistance = 100; // Distance in meters
        for (const marker of markers) {
            const distance = haversineDistance(latlon, marker.position);
            if (distance < minDistance && marker.markerJSON.category.categoryId === parseInt(categoryId)) {
                closestMarker = marker;
                minDistance = distance;
            }
        }
        return closestMarker;
    };

    const handleSubmitReport = (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);

        const data = {
            title: formData.get("name"),
            description: formData.get("description"),
            address: formData.get("address"),
            categoryId: formData.get("dropdown"),
            photo: formData.get("photo").size === 0 ? null : formData.get("photo"),
            token: Cookies.get("sessionToken") || null
        };

        setReportData(data);

        let closestMarker = getNearbyMarker(pickMarkerLatLon, data.categoryId);
        if (closestMarker !== null) {
            setClosestMarkerData(closestMarker.markerJSON);
            setShowMergeConfirm(true);
        } else {
            APICreateReport(data.token, data.title, data.description, data.address, data.photo, "U obradi", "U obradi", pickMarkerLatLon[0], pickMarkerLatLon[1], data.categoryId, null);
            onClose();
        }
    };

    const submitReport = (closest_problem_id) => {
        if (reportData) {
            APICreateReport(reportData.token, reportData.title, reportData.description, reportData.address, reportData.photo, "U obradi", "U obradi", pickMarkerLatLon[0], pickMarkerLatLon[1], reportData.categoryId, closest_problem_id);
        }
        onClose();
    };

    const handleMergeDecision = (merge) => {
        setShowMergeConfirm(false);
        if (merge) {
            submitReport(closestMarkerData.problemId);
        } else {
            submitReport(null);
        }
    };

    const mergeConfirmDialog = showMergeConfirm && (
        <div className="mergeConfirmDialog">
            <h3>Već postoji bliska prijava. Detalji prijave:</h3>
            <p>{`Naslov: ${closestMarkerData.reports[0].title}`}</p>
            <p>{`Opis: ${closestMarkerData.reports[0].description}`}</p>
            <p>{`Adresa: ${closestMarkerData.reports[0].address}`}</p>
            <button onClick={() => handleMergeDecision(true)}>Spoji s postojećom</button>
            <button onClick={() => handleMergeDecision(false)}>Stvori novu</button>
        </div>
    );

    const reportContent = (
        <div className="reportContent" >
            <form className="form" onSubmit={handleSubmitReport}>
                <div>
                    <label htmlFor="name">Name</label>
                    <input id="name" type="text" name="name" required />
                </div>
                <div>
                    <label htmlFor="description">Kratki Opis</label>
                    <textarea id="description" name="description" required />
                </div>
                <div>
                    <label htmlFor="photo">Dodaj Slike</label>
                    <input id="photo" type="file" name="photo" accept="image/*" multiple />
                </div>
                <div>
                    <label htmlFor="address">Geografske Koordinate ili Adresa</label>
                    <input id="address" type="text" name="address" value={pickMarkerLatLon || ''} required />{pickMarkerLatLon || ''}
                </div>
                <div>
                    <label htmlFor="dropdown">Odaberite Kategoriju štete</label>
                    <select id="dropdown" name="dropdown">
                    <option value="1">Oštećenje na cesti</option>
                    <option value="2">Nepropisno parkiranje</option>
                    <option value="3">Oštećenje na rasvjetnom stupu</option>
                    <option value="0">Ostalo</option>

                    {/* --------------------POPRAVIT-------------------- */}
                    </select>
                </div>
                <input type="submit" className="confirmButton" value="Submit" />
            </form>
        </div>
);

    return (
        <PopupComponent onClose={onClose}>
            {!mergeConfirmDialog && reportContent}
            {mergeConfirmDialog}
        </PopupComponent>
    );
}

export default ReportPopupComponent;
