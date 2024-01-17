import './ReportList.css';
import './API.js'
import React, { useState, useEffect } from 'react';
import { APIGetAllReports } from './API.js';

function ReportListComponent({ problemID }) {
    let [reports, setReports] = useState([]);
    let [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        const receiveReports = async () => {
            const newReports = await APIGetAllReports(problemID);
            setReports(newReports);
            if (newReports) { setIsLoaded(true); }
            else { setIsLoaded(false); }
        }
        receiveReports();

    }, [problemID]);

    return (
        <div className="report-list">
            <ul>
                {reports.map((report, index) => {
                    let author = "Anonimni korisnik"
                    if (report.user !== null) {
                        author = report.user.firstname + " " + report.user.lastname;
                    }
                    const date = new Date(report.reportTime)
                    const dateString = date.toISOString()
                    let slicedPortion = dateString.slice(0, 16)
                    slicedPortion = slicedPortion.split("T")[0] + " " + slicedPortion.split("T")[1]
                    console.log(slicedPortion)
                    let datetime = new Date(report.reportTime)
                    datetime = datetime.toISOString().slice(0, 16)
                    datetime = datetime.split("T")[0] + " " + datetime.split("T")[1]
                    return (
                        <li key={index} className="report-element">
                            <div className="content">
                                <h3 className="title">{report.title}</h3>
                                <p className="time-date">{report.time} {datetime}</p>
                                <p>{report.description}</p>
                                <p><b>Autor:</b> {author} </p>
                                <p><b>Adresa:</b> {report.address}</p>

                            </div>
                            <div className="image-container">
                                <img src={report.imageUrl} alt={report.title} />
                            </div>
                        </li>
                    )
                })}
            </ul>
        </div>
    );

}


export default ReportListComponent;