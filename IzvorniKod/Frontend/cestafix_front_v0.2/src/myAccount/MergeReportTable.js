import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie'; 
import { APIGetStaffProblems, APIMergeReports } from '../API.js'; 
import './MergeReportTable.css'

const MergeReportTable = () => {
  const [problems, setProblems] = useState([]);
  const [selectedReports, setSelectedReports] = useState(new Set());
  const token = Cookies.get("sessionToken");

  async function fetchData() {
    const loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
    try {
      
      const problemsData = await APIGetStaffProblems(loggedUser.cityDept.cityDeptId,token);
      setProblems(problemsData);
    } catch (error) {
      console.error('Error fetching problems:', error);
    }
  }

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line
  }, []);

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

  const handleMerge = (problemId) => {
    APIMergeReports(token, problemId, Array.from(selectedReports)).then(() => {
        setSelectedReports(new Set());
        fetchData();
      }
    );

  };

  return (
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
    </div>
  );
};

export default MergeReportTable;