console.log("Script loaded");

function getJwtFromLocalStorage() {
    const userObject = JSON.parse(localStorage.getItem('user'));
    return userObject ? userObject.accessToken : null;
}

function verifyJwt(jwtToken) {
    fetch('/verifyJwt', {  // Assume you've an endpoint named '/verifyJwt'
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + jwtToken,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.valid) {
                // JWT is valid; load or refresh content
                refreshPageWithJwt();
            } else {
                console.error("JWT is invalid or expired.");
                redirectToLogin();
            }
        });
}

function redirectToLogin() {
    window.location.href = "/index";
}

function refreshPageWithJwt() {
    const jwtToken = getJwtFromLocalStorage();

    if (!jwtToken) {
        console.error("No JWT token found in local storage.");
        redirectToLogin();
        return;
    }

    fetch('/admin_home', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + jwtToken,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.text();
        })
        .catch(error => {
            console.error('An error occurred:', error);
        });
}

// add event listener to page refresh event
document.addEventListener('DOMContentLoaded', refreshPage);

function refreshPage() {
    console.log("DOMContentLoaded fired");
    const jwtToken = getJwtFromLocalStorage();
    console.log("Extracted JWT token:", jwtToken);

    if (jwtToken) {
        console.log("JWT found on page load.");
        verifyJwt(jwtToken);
    } else {
        console.log("JWT not found on page load.")
        console.error("No JWT found on page load.");
        redirectToLogin();
    }
}

document.getElementById('refreshJwtLink').addEventListener('click', function (event) {
    event.preventDefault();
    refreshPageWithJwt();
});