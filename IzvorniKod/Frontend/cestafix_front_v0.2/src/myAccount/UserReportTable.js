import {APIGetAllProblems} from "../API";
import React, { useState, useEffect } from "react";
import Cookies from "js-cookie";


const UserReportTable = () => {
  async function getReports() {
    let loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
    const dbMarkers = await APIGetAllProblems();
    let userReportList = dbMarkers
      .flatMap((problem) => problem.reports) // Flatten the array of reports
      .filter((report) => report.user.userId === loggedUser.userId);
  }

  return (
    <div className="updateTable">
        <div className="naslovni">
            <h2>Naziv Prijave</h2>
            <p>Opis Prijave</p>
            <p>Adresa</p>
            <p>Status</p>

        </div>
      {getReports().map((report) => (
        <div className="Report">
          <div
            onClick={() =>
              (window.location.href = `/report/id/${report.reportId}`)
            }
          >
            <h2>{report.title}</h2>
            <p>{report.desc}</p>
            <p>{report.address}</p>
            <p>{report.status}</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default UserReportTable;