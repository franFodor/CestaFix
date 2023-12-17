import ReportPopupComponent from "./ReportForm";

function CheckReportComponent() {
  const handleSubmit = event => {
    event.preventDefault();
    // Prikaži Taj Report na Mapi
    console.log("Prijavljen Form..");
  }

  return (
    <form onSubmit={handleSubmit}>
      <label className="reportText">
        <input type="text" name="textbox" className="inputBox" placeholder="Unesite ID Prijave:"/>
      </label>
      <button type="submit" className="headerBTNSUBMIT">
        Provjeri!</button>
    </form>
  );
}


export default CheckReportComponent;