import React, { useState, useEffect } from "react";
import { APIGetStaffProblems } from "../API";
import Cookies from "js-cookie";


const UpdateReportTable = () => {
    const [problems, setProblems] = useState([]);
    const [selectedStatuses, setSelectedStatuses] = useState({});
    async function fetchData() {
        const loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
        const token = Cookies.get("sessionToken");
        try {
            const problemsData = await APIGetStaffProblems(token, loggedUser.userId);
            setProblems(problemsData);
        } catch (error) {
            console.error('Error fetching problems:', error);
        }
    }
    useEffect(() => {
        fetchData();
    }, []);

    const handleStatusChange = (reportId, newStatus) => {
        setSelectedStatuses(prevStatuses => ({
            ...prevStatuses,
            [reportId]: newStatus
        }));
    };
    function commitChanges(){
        return null;
    }

    return (
        <div className="updateTable">
            <div className="naslovni">
                <h2>Naziv Prijave</h2>
                <p>Opis Prijave</p>
                <p>Adresa</p>
                <p>Status</p>
            </div>
            {problems.map((problem) => problem.reports.map((report) => (
                <div className="Report">
                    <div
                        onClick={() =>
                            (window.location.href = `/prijava/${report.businessId}`)
                        }
                    >
                        <h2>{report.title}</h2>
                        <p>{report.desc}</p>
                        <p>{report.address}</p>
                        <select value={selectedStatuses[report.id]} onChange={e => handleStatusChange(report.id, e.target.value)}>
                            <option value="U obradi">U obradi</option>
                            <option value="Obrada">Obrada</option>
                        </select>
                    </div>
                </div>
            )))}
            <button onClick={()=>commitChanges()}>Potvrdi Odabrane Promjene!</button>
        </div>
    );

}

export default UpdateReportTable;
