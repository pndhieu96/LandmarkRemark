<img width="934" alt="Screenshot 2024-02-26 at 00 45 36" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/95b6c54e-613a-4dfa-9c11-4cef7c0d0ac8"># Overview of my solution for Tigerspike Technical Challenge
This is the document where I will summarize my approach to the challenge presented by the company. I have successfully completed all the features outlined in the backlog, including:

1. As a user (of the application) I can see my current location on a map.
2. As a user, I can save a short note at my current location.
3. As a user, I can see notes that I have saved at the location they were saved on the map.
4. As a user, I can see the location, text, and username of notes other users have saved.
5. As a user, I have the ability to search for a note based on contained text or username.

Below, I will present the technology I used as well as the detailed features of each screen of the application:

## Technical stack
- The application is written following the MVVM pattern, including
  + The View layer, which comprises fragments for displaying and receiving user interactions
  + The ViewModel sends data processing requests and receives results from the service through the repository.
  + The service handles requests and queries data through the Backend-as-a-Service (BaaS), which is a file store provided by Firebase.
- The application use Authentication of Firebase to manage the users by Email
  

