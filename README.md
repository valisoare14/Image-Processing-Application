# Distributed Software Application for Zooming In and Out of an Image

## Technology Stack:
- **Docker** - for loading and running the samples
- **Java**
  - **JMS (Java Message Service)** - for message-based communication
  - **Java RMI (Remote Method Invocation)** - for distributed image processing
  - **Java Servlet** - for handling REST API requests
- **Frontend Technologies:**
  - **JavaScript**
  - **HTML**
  - **CSS**
    
## Prerequisites:
- **Java**
- **Apache TomEE**
- **Docker** - for loading and running the samples

## Project Structure:

### - `c01` (Container 1):
- **`index.html`** - The front-end of the application where users upload the image and configure the zoom settings (zoom in/zoom out & percentage).
- **`ImageServlet.java`** - REST API handler for requests.
  - Serves the front-end and forwards the image payload to the JMS producer.
- **`JmsProducer.java`** - Publishes the image payload in a queue.

### - `c02` (Container 2):
- **`JmsBroker.java`** - Handles messaging communication through the queue.

### - `c03` (Container 3):
- **`JmsClient.java`** - The main consumer of image-related messages published in the queue.
  - Delegates image processing to remote objects.
  - One **RMI object** from container `c04` processes the first half of the image.
  - Another **RMI object** from container `c05` processes the second half of the image.
  - Gathers the processed pieces of the image and composes them into the final zoomed image.
- **`RmiServerObjInterfaces`** - Interface for the remote objects performing image processing.

### - `c04` & `c05` (Processing Containers):
- **`RmiServerObjInterfaces`** - Interface for remote objects performing image processing.
- **`RmiServer`** - The server-side implementation for **Remote Method Invocation (RMI)**.
- **`RmiServerObj.java`** - Remote objects class where the **zoom method** is implemented.
  - Processes the image based on the provided zoom configuration (**zoom in/zoom out**).
