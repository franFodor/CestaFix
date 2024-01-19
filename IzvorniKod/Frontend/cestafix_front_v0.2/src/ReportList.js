import './ReportList.css';
import './API.js'
import React, { useState, useEffect } from 'react';
import { APIGetAllReports } from './API.js';

//Lista reportova za odabran problem na karti
function ReportListComponent({ problemID }) {
    let [reports, setReports] = useState([]);
    let [isLoaded, setIsLoaded] = useState(false);

    //svaki put kad se odabrani problemId promijeni prikazi sve reportove za taj problem
    useEffect(() => {
        const receiveReports = async () => {
            const newReports = await APIGetAllReports(problemID);
            setReports(newReports);
            if (newReports) { setIsLoaded(true); }
            else { setIsLoaded(false); }
        }
        receiveReports();

    }, [problemID]);

    //vrati listu reportova i detalje o reportovima u njoj
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
                                <p><b>Opis prijave: </b>{report.description}</p>
                                <p><b>Autor:</b> {author} </p>
                                <p><b>Adresa:</b> {report.address}</p>
                                <div className="image-container">
                                {report.base64Photos.map((photo, index) => (
                                    <img key={index} src={`data:image/png;base64,${photo}`} alt={`Photo ${index + 1}`} />
                                ))}
                            </div>

                            </div>
                        </li>
                    )
                })}
            </ul>
        </div>
    );

}


export default ReportListComponent;