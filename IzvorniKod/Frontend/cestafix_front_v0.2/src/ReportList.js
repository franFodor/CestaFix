import './ReportList.css';
import './API.js'
import React, { useState, useEffect } from 'react';
import {APIGetAllReports } from './API.js';

function ReportListComponent({problemID}) {
    let [reports, setReports] = useState([]);
    let [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        const receiveReports = async () => {
            const newReports = await APIGetAllReports(problemID);
        setReports(newReports);
        if (newReports){ setIsLoaded(true);}
        else {setIsLoaded(false);}
        }
        receiveReports();
       
    }, []);

    return (
        <div className="report-list">
            <ul>
                {reports.map((report, index) => (
                    <li key={index} className="report-element">
                        <div className="content">
                            <h3 className="title">{report.title}</h3>
                            <p>{report.description}</p>
                            <p>Author: {report.author}</p>
                            <p>Address: {report.address}</p>
                            <p className="time-date">{report.time}, {report.date}</p>
                        </div>
                        <div className="image-container">
                            <img src={report.imageUrl} alt={report.title} />
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );

    }


export default ReportListComponent;