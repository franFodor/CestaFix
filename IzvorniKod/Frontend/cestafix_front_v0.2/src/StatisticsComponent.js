import React, { useState , useEffect} from 'react';
import './StatisticsComponent.css';
import PopupComponent from './PopupComponent';
import { APIGetStats } from './API';


//popup koji prikazuje statistiku dobivenu API pozivom
const StatisticsComponent = ({onClose}) => {
    const [stats, setStats] = useState(null);

    //zove API
    const fetchStats = async () => {
        try {
            const response = await APIGetStats();
            setStats(response);
        } catch (error) {
            console.error('Failed to fetch statistics:', error);
        }
    };
//zove funkciju za dobijanje podataka svakih 5 sekundi kako bi se dobili što sviježiji podatci
    useEffect(() => {
        fetchStats(); // Fetch stats initially
        const interval = setInterval(fetchStats, 5000);

        return () => clearInterval(interval);
    }, []);

//vraca popup i prikazuje podatke
    const popupContent = (
        <div className="stats-container">
            <div className="title">Statistika Dosadašnjih Prijava</div>
            {console.log(stats)}
            <div className="section">
                <h3>Raščlamba statistike:</h3>
                {stats && Object.entries(stats).map(([key, value], index) => (
                    <p key={index}><span className="label">{key}:</span> {value} Prijava</p>
                ))}
            </div>
        </div>
       );
    return <PopupComponent onClose={onClose} children={stats ? popupContent:<div>Učitavanje podataka...</div>}/>
};

export default StatisticsComponent;