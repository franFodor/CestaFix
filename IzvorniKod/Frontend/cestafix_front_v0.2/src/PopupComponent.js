import './PopupComponent.css'


//Komponenta za popup
function PopupComponent({onClose, children}) {
    return (<div className="popup">
                <div className="popup-plate">
                    <span className="close" onClick={onClose}>&times;</span>
                    {children}
                </div>
            </div>
           );
}

export default PopupComponent;