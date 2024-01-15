<!-- logout -->
document.getElementById('logoutLink').addEventListener('click', function (event) {
    event.preventDefault(); // prevent the default link behavior

    const userObject = JSON.parse(localStorage.getItem('user'));
    const jwtToken = userObject.accessToken;

    fetch('/logout', { // Correct endpoint for logout
        method: 'POST', // Correct method for logout
        headers: {
            'Authorization': 'Bearer ' + jwtToken,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ accessToken: jwtToken }) // Passing the token in the body
    })
        .then(response => {
            if (response.ok) {
                localStorage.removeItem('user'); // remove the user token from the local storage
                window.location.href = '/index'; // redirect to the login page or wherever you want to go after logout
            } else {
                // handle error
            }
        })
        .catch(error => {
            console.error('An error occurred:', error);
        });
});