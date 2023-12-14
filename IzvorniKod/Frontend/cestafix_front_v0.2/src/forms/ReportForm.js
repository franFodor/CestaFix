import PopupComponent from "../PopupComponent.js"
import "./Forms.css"

const ReportPopupComponent = ({onClose}) => {
    const handleSubmitReport = (event) => {
        event.preventDefault();
        console.log("REGISTER")
    }

    const reportContent = (
        <div className="reportContent" >
            <form className="form" onSubmit={handleSubmitReport}>
                <div>
                    <label htmlFor="name">Name</label>
                    <input id="name" type="text" name="name" required />
                </div>
                <div>
                    <label htmlFor="description">Kratki Opis</label>
                    <textarea id="description" name="explanation" required />
                </div>
                <div>
                    <label htmlFor="photo">Dodaj Slike</label>
                    <input id="photo" type="file" name="photo" accept="image/*" multiple />
                </div>
                <div>
                    <label htmlFor="coordinates">Geografske Koordinate ili Adresa</label>
                    <input id="coordinates" type="text" name="coordinates" required />
                </div>
                <div>
                    <label htmlFor="dropdown">Odaberite Kategoriju štete</label>
                    <select id="dropdown" name="dropdown">
                    <option value="option1">Šteta Na Cesti</option>
                    <option value="option2">Sve Ostalo</option>
                    {/* --------------------POPRAVIT-------------------- */}
                    </select>
                </div>
                <input type="submit" class="confirmButton" value="Submit" />
            </form>
        </div>
    );

  return <PopupComponent onClose={onClose} children={reportContent}/>
}

export default ReportPopupComponent;