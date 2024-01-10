import "../API";
import { APIGetStaffProblems } from "../API";
import React, { useState, useEffect } from "react";
import Cookies from "js-cookie";


const MergeReportTable = () => {
    let loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
    const [selectedProblem, setSelectedProblem] = useState(null);
    const [selectedReports, setSelectedReports] = useState([]);
    const [problems, setProblems] = useState([]);
   
    const handleProblemClick = (problem) => {
      setSelectedProblem(problem);
    };
   
    const handleReportClick = (report) => {
      setSelectedReports((prevReports) =>
        prevReports.includes(report) ? prevReports.filter((r) => r !== report) : [...prevReports, report]
      );
    };
   
    useEffect(() => {
      async function fetchData() {
        const problems = await APIGetStaffProblems(loggedUser.userId);
        return problems;
      }
      fetchData().then((problems) => {
        setProblems(problems);
      });
    }, []);
   
    return (
      <div className="mergeTable">
        {problems.map((problem) => (
          <div
            key={problem.problemId}
            className={`problem ${selectedProblem === problem ? "Selected" : ""}`}
            onClick={() => handleProblemClick(problem)}
          >
            <p>{problem.problemId}</p>
            {selectedProblem === problem &&
              problem.reports.map((report) => (
                <div
                  key={report.reportId}
                  className={`report ${selectedReports.includes(report) ? "Selected" : ""}`}
                  onClick={() => handleReportClick(report)}
                >
                  <p>{report.reportId}</p>
                </div>
              ))}
          </div>
        ))}
      </div>
    );
   };
   
   export default MergeReportTable;
   