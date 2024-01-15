/*
The code listens for a click event on an element (a link) with the ID userHomeLink.
When the link is clicked, it prevents the default behavior (navigating to a new page),
fetches the user's home page using a JWT token stored in the browser's local storage,
and then displays the fetched page.
 */
// Retrieve JWT token
// Attach an event listener to the element with the id "userHomeLink" to handle its click event.
document.getElementById('userHomeLink').addEventListener('click',
    function (event) {

        // Prevent the default action of the link (which would normally navigate to the link's href).
        event.preventDefault();

        // Retrieve the 'user' object from local storage and parse it as JSON.
        const userObject = JSON.parse(localStorage.getItem('user'));

        // Extract the JWT token from the parsed user object.
        const jwtToken = userObject.accessToken;

        // Make a GET request to the "/user_home" endpoint using the JWT token for authentication.
        fetch('/user_home', {
            method: 'GET',  // Define the request method as GET
            headers: {
                // Add the JWT token to the request's Authorization header.
                'Authorization': 'Bearer ' + jwtToken,

                // Set the content type of the request to JSON.
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                // Check if the response indicates a successful request.
                if (!response.ok) {
                    // If the response indicates an error, parse the response text and throw it as an error.
                    return response.text().then(text => { throw new Error(text) });
                }

                // If the response was successful, parse the response as text (HTML).
                return response.text();
            })
            .then(html => {
                // Replace the current document's content with the received HTML.
                document.open();
                document.write(html);
                document.close();
            })
            .catch(error => {
                // Handle any errors that occurred during the fetch and log them to the console.
                console.error('An error occurred:', error);
            });
    });