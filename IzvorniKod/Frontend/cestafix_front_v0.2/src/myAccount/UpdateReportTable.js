import React, { useState, useEffect } from "react";
import { APIGetStaffProblems, APIUpdateProblemStatus } from "../API";
import Cookies from "js-cookie";
import loadingGif from "../images/loading.gif"


const UpdateReportTable = () => {
    const [problems, setProblems] = useState();
    const [selectedStatuses, setSelectedStatuses] = useState({});

    async function fetchData() {
        const loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
        const token = Cookies.get("sessionToken");
        try {
            const problemsData = await APIGetStaffProblems(loggedUser.cityDept.cityDeptId, token);
            setProblems(problemsData);
        } catch (error) {
            console.error('Error fetching problems:', error);
        }
    }

    useEffect(() => {
        fetchData();
    }, []);

    const handleStatusChange = (problemId, newStatus) => {
        setSelectedStatuses(prevStatuses => ({
            ...prevStatuses,
            [problemId]: newStatus
        }));
    };

    async function commitChanges() {
        const token = Cookies.get('sessionToken');
        for (const problemId in selectedStatuses) {
            if (selectedStatuses.hasOwnProperty(problemId)) {
                const newStatus = selectedStatuses[problemId];

                try {
                    await APIUpdateProblemStatus(token, problemId, newStatus);
                } catch (error) {
                    console.error(`Error updating status for problem ${problemId}:`, error);
                }
            }
        }
    }

    return (
        <>
          {problems && (
            <div className="w-full overflow-auto flex">
              <div className="flex flex-col flex-grow overflow-auto pl-4">
                {problems.map((problem) => (
                  <div className="relative bg-white shadow-lg rounded-lg p-4 mb-4 w-full overflow-visible" key={problem.problemId}>
                    <h2 className="text-2xl mb-2">{problem.reports[0].title}</h2>
                    {problem.reports.map((report) => (
                      <div className="mb-2 word-break-all" key={report.reportId}>
                       <p>Opis Prijave:   <b>{report.description}</b></p> 
                       <p>Adresa: {report.address}</p>
                       <p>Jedinstveni Identifikator Prijave:   {report.businessId}</p>
                       <button className="px-4 py-2 bg-green-500 text-white rounded"onClick={()=>{if(report.businessId !=="")window.location.href = `/prijava/${report.businessId}`}}>Prikaži prijavu na Karti</button>                      
                        <br></br> <br></br>
                      </div>
                    ))}
                    <select className="mt-2" style={{zIndex: 1}} value={selectedStatuses[problem.problemId] || problem.status} onChange={e => handleStatusChange(problem.problemId, e.target.value)}>
                      <option value="Čeka Obradu">Čeka Obradu</option>
                      <option value="U Obradi">U obradi</option>
                      <option value="Obrađeno">Obrađeno</option>
                    </select>
                  </div>
                ))}
              </div>
              <div className="flex items-center justify-center mr-4">
                <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mt-4 inline-block" onClick={commitChanges}>Potvrdi Odabrane Promjene!</button>
              </div>
            </div>
          )}
          {!problems && (<img src={loadingGif} alt="Loading..." style={{ width: '5%', height: 'auto' }} />)}
        </>
       );
       
       
       
       
       
       
       
       
       
}

export default UpdateReportTable;
