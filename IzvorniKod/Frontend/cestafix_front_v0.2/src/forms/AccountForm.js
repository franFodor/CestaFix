import LoginFormComponent from './LoginForm.js'
import RegisterFormComponent from './RegisterForm.js'
import PopupComponent from '../PopupComponent.js'
import './Forms.css'
import React, { useState } from 'react';

const formTypeEnum = {
  LOGIN: 0,
  REGISTER: 1,
};

const AccountPopupComponent = ({onClose}) => {
    const [formType, setFormType] = useState(formTypeEnum.LOGIN);
    const handleFormSwitch = (event) => {
        if (formType === formTypeEnum.LOGIN){
            setFormType(formTypeEnum.REGISTER);
        } else {
            setFormType(formTypeEnum.LOGIN);
        }
    }

    let popupContent = null;
    if (formType === formTypeEnum.LOGIN) {
        popupContent = <div className="loginContent">
                           <LoginFormComponent />
                           <div onClick={handleFormSwitch} id="nemaracun" style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
                               Nemaš račun? Registriraj se!
                           </div>
                       </div>
    } else if (formType === formTypeEnum.REGISTER) {
        popupContent = <div className="registerContent">
                           <RegisterFormComponent />
                           <div onClick={handleFormSwitch} id="imaracunlogin" style={{ textDecoration: 'underline', cursor: 'pointer', color: 'blue' }}>
                               Već imaš račun? Ulogiraj se!
                           </div>
                       </div>
    }

    return <PopupComponent onClose={onClose} children={popupContent}/>
}

export default AccountPopupComponent;