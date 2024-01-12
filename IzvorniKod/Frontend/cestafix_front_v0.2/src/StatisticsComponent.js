import React, { useState , useEffect} from 'react';
import './StatisticsComponent.css';
import PopupComponent from './PopupComponent';

const StatisticsComponent = ({onClose}) => {
    const [stats, setStats] = useState(null);

    const fetchStats = async () => {
        try {
            const response = await fetch('/path/to/your/stats/api'); // Replace with your API endpoint
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setStats(data);
        } catch (error) {
            console.error('Failed to fetch statistics:', error);
        }
    };

    useEffect(() => {
        fetchStats(); // Fetch stats initially
        const interval = setInterval(fetchStats, 5000);

        return () => clearInterval(interval);
    }, []);

    if (!stats) {
        return <PopupComponent onClose={onClose} children={<div>Učitavanje podataka...</div>}/>
    }

    const popupContent = (
        <div className="stats-container">
            <div className="title">Road Issue Report Statistics</div>
            <div className="section">
                <h3>Status Breakdown</h3>
                {stats.status.map((item, index) => (
                    <p key={index}><span className="label">{item.status}:</span> {item.count} reports</p>
                ))}
            </div>
            <div className="section">
                <h3>Category Breakdown</h3>
                {stats.category.map((item, index) => (
                    <p key={index}><span className="label">{item.category}:</span> {item.count} reports</p>
                ))}
            </div>
        </div>
    );

    return <PopupComponent onClose={onClose} children={popupContent}/>
};

export default StatisticsComponent;