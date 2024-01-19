import { APIGetAllProblems } from "../API";
import React, { useState, useEffect } from "react";
import Cookies from "js-cookie";
import './UserReportTable.css'
import loadingGif from "../images/loading.gif"


//prikazuje listu reportova odredenog korisnika 
const UserReportTable = () => {
  const [reports, setReports] = useState();


  //dohvaca reportove za odredenog korisnika
  async function getReports() {
    let loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
    const dbMarkers = await APIGetAllProblems();
    setReports(
      dbMarkers
        .flatMap((problem) => problem.reports) // pocisti array na reportove koji su bitni korisniku
        .filter((report) => report.user?.userId === loggedUser.userId)
    );
  }

  //pri prvom renderu dohvati reportove
  useEffect(() => {
    getReports();
  }, []);

//vrati listu reportova sa gumbom koji routa korisnika na kartu i report
  return (<>
    {reports && (
      <div className="updateTable">
        {reports.map((report) => (
          <div className="Report">
            <div>
              <h2>{report.title}</h2>
              <p>{report.description}</p>
              <div className="image-container">
                {report.base64Photos.map((photo, index) => (
                  <img key={index} src={`data:image/png;base64,${photo}`} alt={`Photo ${index + 1}`} />
                ))}
              </div>

              <br></br>
              <p>{report.address}</p>
              <button className="px-4 py-2 mb-4 bg-green-500 text-white rounded" onClick={() =>
                (window.location.href = `/prijava/${report.businessId}`)
              }>Prikaži na Karti</button>

              <p>{report.status}</p>
            </div>
          </div>
        ))}
      </div>)}
    {!reports && (<img src={loadingGif} alt="Loading..." style={{ width: '5%', height: 'auto' }} />)}</>
  );
};

export default UserReportTable;