import PopupComponent from "../PopupComponent.js"
import "./Forms.css"
import {APICreateReport} from "../API.js"
import Cookies from 'js-cookie'

const ReportPopupComponent = ({onClose, pickMarkerLatLon}) => {
    const handleSubmitReport = (event) => {
        event.preventDefault();
        
        const formData = new FormData(event.target);

        const title = formData.get("name");
        const description = formData.get("description");
        const address = formData.get("address");
        const categoryId = formData.get("dropdown");
        let photo = formData.get("photo");
        if (photo.size === 0) {
            photo = null;
        }

        const logged_user_cookie = Cookies.get("userInfo");
        const session_token = Cookies.get("sessionToken");
        let token = null;
        if (logged_user_cookie !== undefined){
            token = session_token;
        }
        APICreateReport(token, title, description, address, photo, "U obradi", "U obradi", pickMarkerLatLon[0], pickMarkerLatLon[1], categoryId);
        onClose();
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
                    <textarea id="description" name="description" required />
                </div>
                <div>
                    <label htmlFor="photo">Dodaj Slike</label>
                    <input id="photo" type="file" name="photo" accept="image/*" multiple />
                </div>
                <div>
                    <label htmlFor="address">Geografske Koordinate ili Adresa</label>
                    <input id="address" type="text" name="address" required />
                </div>
                <div>
                    <label htmlFor="dropdown">Odaberite Kategoriju štete</label>
                    <select id="dropdown" name="dropdown">
                    <option value="1">Šteta Na Cesti</option>
                    <option value="2">Sve Ostalo</option>
                    {/* --------------------POPRAVIT-------------------- */}
                    </select>
                </div>
                <input type="submit" className="confirmButton" value="Submit" />
            </form>
        </div>
    );

  return <PopupComponent onClose={onClose} children={reportContent}/>
}

export default ReportPopupComponent;