import { APIGetAllProblems } from "../API";
import React, { useState, useEffect } from "react";
import Cookies from "js-cookie";
import './UserReportTable.css'

const UserReportTable = () => {
  const [reports, setReports] = useState([]);
  async function getReports() {
    let loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
    const dbMarkers = await APIGetAllProblems();
    setReports(
      dbMarkers
        .flatMap((problem) => problem.reports) // Flatten the array of reports
        .filter((report) => report.user?.userId === loggedUser.userId)
    );
  }
  useEffect(() => {
    getReports();
  }, []);

  return (
    <div className="updateTable">
      {reports.map((report) => (
        <div className="Report">
          <div>
            <h2>{report.title}</h2>
            <p>{report.description}</p>
            
            <br></br>
            <p>{report.address}</p>
            <button className= "px-4 py-2 mb-4 bg-green-500 text-white rounded"onClick={() =>
              (window.location.href = `/prijava/${report.businessId}`)
            }>Prikaži na Karti</button>
            
            <p>{report.status}</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default UserReportTable;