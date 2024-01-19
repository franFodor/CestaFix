import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie';
import { APIGetStaffProblems, APIMergeReports } from '../API.js';
import './MergeReportTable.css'
import loadingGif from "../images/loading.gif"


//komponenta koja prikazuje tablicu za mergeanje reportova u probleme
const MergeReportTable = () => {
  const [problems, setProblems] = useState();
  const [selectedReports, setSelectedReports] = useState(new Set());
  const token = Cookies.get("sessionToken");

  //povlaci probleme i reportove bitne samo ovom staff korisniku
  async function fetchData() {
    const loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
    try {

      const problemsData = await APIGetStaffProblems(loggedUser.cityDept.cityDeptId, token);
      setProblems(problemsData);
    } catch (error) {
      console.error('Error fetching problems:', error);
    }
  }


//na renderu fetchaj podatke
  useEffect(() => {
    fetchData();
  }, []);

  //je li taj report odabran ili nije?
  const toggleReportSelection = (reportId) => {
    setSelectedReports(prevSelected => {
      const newSelected = new Set(prevSelected);
      if (newSelected.has(reportId)) {
        newSelected.delete(reportId);
      } else {
        newSelected.add(reportId);
      }
      return newSelected;
    });
  };


  //zove se pri predaji forme, te nakon predaje updatea podatke
  const handleMerge = (problemId) => {
    APIMergeReports(token, problemId, Array.from(selectedReports)).then(() => {
      setSelectedReports(new Set());
      fetchData();
    }
    );

  };
//vraca tablicu
  return (<>
    {problems && (
      <div className="problems-container">
        <table>
          <thead>
            <tr>
              <th></th>
              <th>ID Problema</th>
              <th>Kategorija</th>
              <th>Status</th>
              <th>Naslov prijave</th>
              <th>Opis prijave</th>
              <th>Adresa prijave</th>
              <th>Status prijave</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {problems.map(problem => (
              <>
                <tr key={problem.problemId} className="problem-row">
                  <td>
                    <button onClick={() => handleMerge(problem.problemId)}>Spoji izabrano</button>
                  </td>
                  <td><b>{problem.problemId}</b></td>
                  <td><b>{problem.category.categoryName}</b></td>
                  <td><b>{problem.status}</b></td>
                  <td colSpan="4"></td>
                </tr>
                {problem.reports.map(report => (
                  <tr key={report.reportId} className={`report-row ${selectedReports.has(report.reportId) ? 'selected' : ''}`}>
                    <td className="noborders" colSpan="4"></td>
                    <td>{report.title}</td>
                    <td>{report.description}</td>
                    <td>{report.address}</td>
                    <td>{report.status}</td>
                    <td>
                      <button onClick={() => toggleReportSelection(report.reportId)}>
                        {selectedReports.has(report.reportId) ? 'Briši' : 'Označi'}
                      </button>
                    </td>
                  </tr>
                ))}
              </>
            ))}
          </tbody>
        </table>
      </div>)}
    {!problems && (<img src={loadingGif} alt="Loading..." style={{ width: '5%', height: 'auto' }} />)}


  </>
  );
};

export default MergeReportTable;
