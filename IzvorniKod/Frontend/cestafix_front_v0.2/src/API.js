
async function APILogin(email, password) {
    const formData = {
        email: email,
        password: password,
    };

    return fetch('/api/auth/login',
        {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(formData)
        });
}

async function APILogout() {
    return fetch('/api/auth/logout', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'}
    });
}

// Funkciju triggera submit register forme. Salje podatke forme na /api/register
async function APIRegister(firstname, lastname, email, password) {
    const formData = {
        firstname: firstname,
        lastname: lastname,
        email: email,
        password: password
    };

    return fetch('/api/auth/register', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(formData)
    });
}

async function APIGetAllProblems(){
    const response = await fetch('/api/public/problem/getAll', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (!response.ok) {
        throw new Error('Network response was not OK');
    }

    const data = await response.json();
    return data;
}

async function APIGetAllReports(problem_id){
    const response = await fetch('/api/public/problem/' + problem_id, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    const data = await response.json();
    return data.reports;
}

async function APIGetStaffProblems(user_ID){
    const response = await fetch('/api/advanced//' + user_ID, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    const data = await response.json();
    return data;
}

async function APIWhoAmI(token){
    const response = await fetch('/api/normal/user/whoAmI', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization' : 'Bearer ' + token
        },
    });

    const data = await response.json();
    return data;
}

async function APICreateReport(token, title, description, address, photo, reportStatus, problemStatus, latitude, longitude, categoryId, closest_problem_id) {
    const formData = {
        "longitude" : longitude,
        "latitude" : latitude,
        "title": title,
        "description": description,
        "address": address,
        "photo": photo,
        "reportStatus": reportStatus,
        "problemStatus" : problemStatus,
        "categoryId" : categoryId,
        "mergeProblemId" : closest_problem_id
    };
    console.log("Report Data:>>",formData);

    if (token === null){
        return fetch('/api/public/report', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formData)
        });
    } else {
        return fetch('/api/public/report', {
            method: 'POST',
            headers: {'Content-Type': 'application/json',
                      'Authorization' : 'Bearer ' + token
                     },
            body: JSON.stringify(formData)
        });
    }
}

export {APILogin, APILogout, APIRegister, APIGetAllProblems, APIGetAllReports, APIWhoAmI, APICreateReport, APIGetStaffProblems};