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
- The application has 4 Screen as Fragments including SplashFragment, LoginFragment, MapFragment, NoteFragment.
  
- Below is the navigation graph of the application:
<img width="500" alt="Screenshot 2024-02-26 at 01 03 59" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/9ebcad77-d73e-4693-96d3-96e1c6d26145">

- <b>The splash fragment</b> is responsible for requesting necessary app permissions from the user and checking the user's login status to navigate to the login screen if the user is not logged in, and to the map screen if the user is already logged in.
<img width="500" alt="Screenshot 2024-02-26 at 00 48 57" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/184e4ee2-f114-4360-8cc2-bac0377465f7">

- <b>The login fragment</b> is responsible for user logging in by email and password
<img width="220" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/3ba9f177-c0da-4c9e-8cb2-264f1139d7cb">

- <b>The map fragment</b> is responsible for showing the current location of the user on the map, displaying the notes of both the user and other users on the map, adding a note for the current location, and searching for notes by email or content.
<img width="220" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/500deb46-8450-4f3d-bb29-be225e623645">

- <b>The Note fragment</b> is responsible for displaying the details of a note created by oneself and others, including the user's email, creation date, and content. Additionally, users can create new notes at the current location, edit, and delete notes created by themselves.
<img width="541" alt="Screenshot 2024-02-26 at 02 19 13" src="https://github.com/pndhieu96/LandmarkRemark/assets/24983645/df507b79-5291-43b0-a72c-fd4f9b122f78">

- Link application demo video: https://drive.google.com/drive/folders/1RKn3phLVbt5CTQb1VzshK_CNdkcdQ3JV
- Link apk file: https://drive.google.com/drive/folders/1mMZPZ03GN9vWj37AqX5Hi1ffSZV3RUbJ

## Conclusion
- I completed the assignment within a total time of approximately 24 hours, including:
  + Reading and analyzing the problem, sketching screens and app flows on paper.
  + Researching technologies to address technical issues.
  + Coding the application.
  + Finally, writing a document to summarize the application.
- In this process, I encountered some difficulties due to the relatively tight time frame. If given more time, I would:
  - Conduct further research on relevant technologies to ensure a smoother and more optimized application.
  - Dedicate time to building a more visually appealing app with a better user experience.
  - Address issues related to the interaction between the list of notes and their display on the map.





