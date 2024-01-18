
async function APILogin(email, password) {
    const formData = {
        email: email,
        password: password,
    };

    return fetch('/api/auth/login',
        {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });
}
async function APIUpdateUser(token,
    userId,
    firstName,
    lastName) {
    const formData = {
        userId: userId,
        firstname: firstName,
        lastname: lastName
    };

    const response =await fetch(`/api/advanced/user/${userId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(formData)
    });
    
    if (!response.ok) {
        throw new Error('Network response was not OK');
    }

    const data = await response.json();
    return data;
}





async function APIDeleteUser(id, token) {
    return fetch(`api/advanced/user/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
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
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    });
}

async function APIMergeReports(token, problem_id, reportlist) {
    return fetch('/api/advanced/report/group/' + problem_id, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json',
                   'Authorization': 'Bearer ' + token},
        body: JSON.stringify(reportlist)
    });
}


async function APIGetAllProblems() {
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

async function APIGetAllReports(problem_id) {
    const response = await fetch('/api/public/problem/' + problem_id, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    const data = await response.json();
    return data.reports;
}

async function APIGetStaffProblems(citydeptId, token) {
    const response = await fetch(`/api/advanced/problem/${citydeptId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
    });

    const data = await response.json();
    return data;
}
async function APIUpdateProblemStatus(token, problemId, newStatus) {
    const response = await fetch(`/api/advanced/problem/${problemId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify({ "status": newStatus })
    });

    const data = await response.json();
    return data;
}
async function APIGetProblemIDFromBusinessId(businessId, token) {
    const response = await fetch('/api/public/lookup/' + businessId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    });

    const data = await response.json();
    //console.log("Api Repns>",data);
    return data;
}

async function APIWhoAmI(token) {
    const response = await fetch('/api/normal/user/whoAmI', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
    });

    const data = await response.json();
    return data;
}

async function APICreateReport(token,
    title,
    description,
    address,
    base64Photos,
    reportStatus,
    problemStatus,
    latitude,
    longitude,
    categoryId,
    closest_problem_id) {
    const formData = {
        "longitude": longitude,
        "latitude": latitude,
        "title": title,
        "description": description,
        "address": address,
        "base64Photos": base64Photos,
        "reportStatus": reportStatus,
        "problemStatus": problemStatus,
        "categoryId": categoryId,
        "mergeProblemId": closest_problem_id
    };
    //console.log("Report Data:>>",formData);

    if (token === null) {
        return fetch('/api/public/report', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });
    } else {
        return fetch('/api/public/report', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(formData)
        });
    }
}
async function APICheckNearbyReport(title,
    description,
    address,
    base64Photos,
    reportStatus,
    problemStatus,
    latitude,
    longitude,
    categoryId,
    closest_problem_id) {
    const formData = {
        "longitude": longitude,
        "latitude": latitude,
        "title": title,
        "description": description,
        "address": address,
        "base64Photos": base64Photos,
        "reportStatus": reportStatus,
        "problemStatus": problemStatus,
        "categoryId": categoryId,
        "mergeProblemId": closest_problem_id
    };
    //console.log("Report Data:>>",formData);
    const response = await fetch('/api/public/nearbyReport', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    });
    if (response.status === 403) {
        throw new Error('Forbidden');
    }
    console.log(response);
    const data = await response.json();
    return data;


}

async function APIGetStats() {
    const response = await fetch('/api/public/statistics', {
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
export {
    APILogin,
    APIRegister,
    APIUpdateUser,
    APIDeleteUser,
    APIGetAllProblems,
    APIGetAllReports,
    APIWhoAmI,
    APICreateReport,
    APIGetStaffProblems,
    APIUpdateProblemStatus,
    APIGetProblemIDFromBusinessId,
    APICheckNearbyReport,
    APIGetStats,
    APIMergeReports
};