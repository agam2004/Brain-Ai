import logging
from flask import Flask, Response, request
from flask_cors import CORS
from flask_socketio import SocketIO
from GAN import Generate
import requests
import base64

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

latent_dim = 100
img_channels = 3
img_size = 256

SPRING_BOOT_URL = "http://localhost:8080"

app = Flask(__name__)
CORS(app, origins=[SPRING_BOOT_URL])
socketio = SocketIO(app)

model = Generate(latent_dim, img_channels, img_size)


def convert_image_to_bytes(image_path):
    with open(image_path, "rb") as image_file:
        return base64.b64encode(image_file.read())


@app.route('/generate_images')
def generate_images():
    logger.info("Got some message")

    uuid = request.headers.get('UUID')

    # Assuming model.generate_images(num_images=1) returns a list of file paths
    image_paths = model.generate_images(num_images=1)

    try:
        for image_path in image_paths:
            # Convert image to bytes using the provided function
            byte_data = convert_image_to_bytes(image_path)

            headers = {
                'Content-Type': 'application/octet-stream',
                'uuid': uuid
            }
            # Send the byte data to the Spring Boot endpoint
            response = requests.post(f"{SPRING_BOOT_URL}/save_image", data=byte_data, headers=headers)
            if response.status_code == 200:
                logger.info("Image saved successfully")
            else:
                logger.error("Failed to save image to Spring Boot")
                return Response("Failed to save image to Spring Boot", status=500)

        return Response("Images saved successfully", status=200)
    except Exception as e:
        logger.exception(f"Error: {str(e)}")
        return Response("Internal Server Error", status=500)


if __name__ == '__main__':
    socketio.run(app, debug=True, use_reloader=False, allow_unsafe_werkzeug=True, port=5001)
