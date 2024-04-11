// Function to connect to the server
function connectToServer() {
    const net = require('net');
    const client = new net.Socket();

    client.connect(11000, '127.0.0.1', () => {
        console.log('Connected to server');
        
        // Send data to the server
        client.write('Hello from JavaScript client!');
    });

    // Receive data from the server
    client.on('data', (data) => {
        console.log('Received from server: ' + data);
        
        // Close the connection after receiving the response
        client.destroy();
    });

    // Handle connection closed
    client.on('close', () => {
        console.log('Connection closed');
    });

    // Handle connection error
    client.on('error', (error) => {
        console.error('Error connecting to server:', error.message);
    });
}

// Event listener for the button click
document.getElementById('connectBtn').addEventListener('click', connectToServer);
