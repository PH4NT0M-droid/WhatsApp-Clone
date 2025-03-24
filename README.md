# WhatsApp Clone 
# MA 104 Project 1

## Project Overview
**WhatsApp Clone** is a full-stack messaging application that replicates the core functionalities of WhatsApp. It supports real-time one-to-one messaging, media sharing via relative URLs. The project leverages both HTTP and WebSocket (STOMP) protocols to provide a robust, secure, and interactive chat experience.

## Features
- **Authentication Using jwt**
   - User login and logout are taken care by JSON Web Tokens.
- **Real-Time Messaging:**  
  - One-to-one and group chats implemented with WebSockets (STOMP protocol) for instant communication.
- **Media Sharing:**  
  - Users can upload media from their computer. The media is stored on the server via HTTP Protocol (e.g., in `C:/uploads/chat`) and is shared via relative URLs.
- **User Blocking & Management:**  
  - Manage unwanted messages and chat groups.
- **User Profile and Staus**
  - Users can add profile picture, status and bio.

## Technologies & Libraries Used
- **Backend:**  
  - Java, Spring Boot
  - Spring Data JPA, Hibernate
  - Spring WebSocket (STOMP), Spring Security, JWT  
  - PostgreSQL  
- **Frontend:**  
  - Vite, React.js  
  - HTML, CSS, JavaScript  
  - SockJS, STOMP.js  
- **Encryption:**  
  - Custom end-to-end encryption using Diffie-Hellman key exchange and AES  
- **Build Tools:**  
  - Maven for backend, Vite for frontend

## Installation & Setup

### Prerequisites
- Java 11 or higher
- PostgreSQL Database
- Maven
- React.js (for frontend using Vite)

### Steps

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/s4kr3d-w0r1d/WhatsApp-Clone.git

### Team Members

1. Saksham Madan (s4kr3d-w0r1d) 
2. Vaishnavi (Celestial-glitch)
3. Tarun Agrawal (PH4NT0M-droid)
