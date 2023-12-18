
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
async function APIRegister(username, email, password, remember) {
    const formData = {
        username: username,
        email: email,
        password: password,
        remember: remember,
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

export {APILogin, APILogout, APIRegister, APIGetAllProblems, APIGetAllReports};