document.getElementById('loginForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent traditional form submission

    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    fetch('/Login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
        .then(response => {
            if(response.status === 401) { // Assuming 401 status code for wrong credentials
                throw new Error('Invalid login credentials.');
            }
            if (!response.ok) { // Any other error
                throw new Error('Network response was not ok');
            }
            return response.json(); // Parse JSON response
        })
        .then(data => {
            if (data.accessToken) {
                localStorage.setItem('user', JSON.stringify(data));
                var token = data.accessToken;

                fetch('/', {
                    method: 'GET',
                    headers: {
                        'Authorization': 'Bearer ' + token,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => response.text())
                    .then(html => {
                        document.open();
                        document.write(html);
                        document.close();
                    });
            } else {
                console.log('Error:', data);
            }
        })
        .catch((error) => {
            if (error.message === 'Invalid login credentials.') {
                document.getElementById('errorMessage').innerText = 'Invalid login credentials.';
            } else {
                document.getElementById('errorMessage').innerText = 'Server not available. Please try again later.';
            }
            console.error('Error:', error);
        });
});