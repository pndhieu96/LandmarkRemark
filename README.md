# Overview of my solution for Tigerspike Technical Challenge
This is the document where I will summarize my approach to the challenge presented by the company. I have successfully completed all the features outlined in the backlog, including:

1. As a user (of the application) I can see my current location on a map.
2. As a user, I can save a short note at my current location.
3. As a user, I can see notes that I have saved at the location they were saved on the map.
4. As a user, I can see the location, text, and username of notes other users have saved.
5. As a user, I have the ability to search for a note based on contained text or username.

Below, I will present the technology I used as well as the detailed features of each screen of the application:

## Technical stack
- The application is written following the MVVM pattern, including
  + The View layer, which comprises fragments for displaying and receiving user interactions.
  + The ViewModel sends data processing requests and receives results from the service through the repository.
  + The service handles requests and queries data through the Backend-as-a-Service (BaaS), which is a file store provided by Firebase.
- Using use Authentication of Firebase to manage the users by Email.
<img width="934" alt="Screenshot 2024-02-26 at 00 45 36" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/95b6c54e-613a-4dfa-9c11-4cef7c0d0ac8">

- Using Cloud Firestore of Firebase to save the notes that created by users.
<img width="1137" alt="Screenshot 2024-02-26 at 00 48 57" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/46dbd4f6-8663-44b5-8257-ed96bf16dea1">

- Using Google Maps SDK to display the map and mark the notes on the map.
- Otherwise, the application use some libraries from android sdk like:
  + Hilt for dependency management.
  + Navigation for managing navigation between different screens of the application.
  + Data Binding for data binding capabilities.
  + Coroutine for asynchronous programming.
  + ViewModel for managing UI-related data in a lifecycle-conscious manner.
 
## Visual Description of the Application

  
  

