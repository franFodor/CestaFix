
const createReport = () => {
  return (
    <div className="createReport" >
      <span className="close" onClick={null}>&times;</span>
      <form id="createReport" action="/api/newReport" method="post" encType="multipart/form-data">
        <div>
          <label htmlFor="name">Name</label>
          <input id="name" type="text" name="name" required />
        </div>
        <div>
          <label htmlFor="explanation">Kratki Opis</label>
          <textarea id="explanation" name="explanation" required />
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
        <div>
          <input type="submit" value="Submit" />
        </div>
      </form>
    </div>

  );
}


export default createReport;