import React, { useState, useEffect } from "react";
import Cookies from "js-cookie";
import "./MyAccountMain.css";
import userImage from "../images/User.png";
import EditAccountForm from "./EditAccountForm";
import UpdateReportTable from "./UpdateReportTable";
import MergeReportTable from "./MergeReportTable";
import UserReportTable from "./UserReportTable";
import { APIDeleteUser } from "../API";

const MyAccount = () => {
  let loggedUser;
  if (Cookies.get("userInfo"))
    loggedUser = JSON.parse(decodeURIComponent(Cookies.get("userInfo")));
  const [buttonClicked, setButtonClicked] = useState("a");

  const handleClick = (buttonLabel) => {
    setButtonClicked(buttonLabel);
  };

  async function handleDeletion() {
    console.log("You have chosen death-");
    let token = Cookies.get("sessionToken");
    let holder = await APIDeleteUser(loggedUser.userId,token);
    Cookies.remove('sessionToken', { path: '/' });
    Cookies.remove('userInfo', { path: '/' });
    window.location.href = "/";


  }

  const loggedRoleAccountContent = () => {
    switch (loggedUser ? loggedUser.role : null) {
      case "USER":
        return (
          <div className="container min-w-full">
            <div className="myAccLeft">
              <img src={userImage} alt="User" />
              <div className="Details">{loggedUser.firstname + " " + loggedUser.lastname}</div>
              <button className="confirmButton" style={{ display: "inline-block", width: "fit-content" }} onClick={() => handleClick("a")}>Uredi Profil</button>
              <button className="confirmButton" style={{ display: "inline-block", width: "fit-content" }} onClick={() => handleClick("b")}>Pregled Mojih Prijava              </button>
              <button className="confirmButton" style={{ display: "inline-block", width: "fit-content", background: "red", }} onClick={() => handleClick("c")}>Pobriši Račun!!!</button>
              <button className="confirmButton" style={{ display: "inline-block", width: "fit-content" }} onClick={() => (window.location.href = "/")}>Povratak</button>
            </div>

            <div className="myAccRight">
              {buttonClicked === "a" && <EditAccountForm />}
              {buttonClicked === "b" && <UserReportTable />}
              {buttonClicked === "c" && (
                <div className="deletionDiv">
                  <p>
                    Jeste li sigurni da želite probrisati račun? Ova radnja ne
                    može biti poništena.
                  </p>
                  <br></br>
                  <button className="deleteButton" onClick={() => handleDeletion()}>POTVRDI</button>
                </div>
              )}
            </div>
          </div>
        );

      case "STAFF":
        return (
          <div className="container min-w-full">

            <div className="myAccLeft">
              <img src={userImage} alt="User" />
              <div>
                {loggedUser.firstname + " " + loggedUser.lastname}
              </div>
              <div className="Details">{loggedUser.citydept.citydeptName + ""}</div>
              <button className="confirmButton" style={{ display: "inline-block", width: "fit-content" }} onClick={() => handleClick("a")}>Uredi Profil</button>
              <button className="confirmButton" style={{ display: "inline-block", width: "fit-content" }}onClick={() => handleClick("b")}>Ažuriraj stanje Prijava!</button>
              <button className="confirmButton" style={{ display: "inline-block", width: "fit-content" }}onClick={() => handleClick("c")}>Spoji prijave!</button>
              <button className="confirmButton" style={{display: "inline-block",width: "fit-content",background: "red",}}onClick={() => handleClick("d")}>Pobriši Račun!!!</button>
              <button className="confirmButton"style={{ display: "inline-block", width: "fit-content" }}onClick={() => (window.location.href = "/")}>Povratak</button>
            </div>

            <div className="myAccRight">
              {buttonClicked === "a" && <EditAccountForm />}
              {buttonClicked === "b" && <UpdateReportTable />}
              {buttonClicked === "c" && <MergeReportTable />}
              {buttonClicked === "d" && (
                <div className="deletionDiv">
                  <p>
                    Jeste li sigurni da želite probrisati račun? Ova radnja ne
                    može biti poništena.
                  </p>
                  <br></br>
                  <button className="deleteButton" onClick={handleDeletion}>POTVRDI</button>
                </div>
              )}
            </div>
          </div>
        );

      default:
        return (
          <div>Doslo je do greske: Nepostojeci ili neulogirani korisnik.</div>
        );
    }
  };

  return <>{loggedRoleAccountContent()}</>;
};

export default MyAccount;
