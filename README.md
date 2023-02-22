# BookShelf App

BookShelf is an Android app that allows users to search for books, view book details, leave reviews,
add books to favorites, and view example categories of books. The app uses Firestore, a No-SQL
database service provided by Google, to store user and book data.

### Bugs and Issues

Please note that the app is currently under development and bugs may be present. Bug reports and
suggestions are welcome.

### Features
* Search for books using the Google Books API
* View book details, including the cover image, author, and description
* Leave reviews for books
* Add books to favorites for quick access later
* Browse example categories of books
* Authenticate using email/password or Google authentication
* Upload a profile image from the device gallery

### Database

The app utilizes Firestore, a document-based database, which allows for more flexibility and
scalability in terms of data organization and querying.

### JSON Data

In order to help with understanding the data structure and organization of the app, a sample of the
JSON data used in the app is provided below

**JSON data structure**

```json
{
  "users": [
    {
      "displayName": "",
      "user_id": "",
      "avatar": "",
      "shelves": [
        {
          "name": "Reading",
          "books": [
            {
              "book_id": 1,
              "title": "",
              "author": "",
              "summary": "",
              "cover_image": ""
            },
            {
              "book_id": 2,
              "title": "",
              "author": "",
              "summary": "",
              "cover_image": ""
            }
          ]
        },
        {
          "name": "Have Read",
          "books": [
            {
              "book_id": 1,
              "title": "",
              "author": "",
              "summary": "",
              "cover_image": ""
            },
            {
              "book_id": 2,
              "title": "",
              "author": "",
              "summary": "",
              "cover_image": ""
            }
          ]
        },
        {
          "name": "To Read",
          "books": [
            {
              "book_id": 1,
              "title": "",
              "author": "",
              "summary": "",
              "cover_image": ""
            },
            {
              "book_id": 2,
              "title": "",
              "author": "",
              "summary": "",
              "cover_image": ""
            }
          ]
        }
      ],
      "search_history": [
        "book_id1",
        "book_id2"
      ],
      "reviews": [
        {
          "book": {
            "book_id": 1,
            "title": "",
            "author": "",
            "summary": "",
            "cover_image": ""
          },
          "rating": 4.5,
          "review_text": ""
        },
        {
          "book": {
            "book_id": 2,
            "title": "",
            "author": "",
            "summary": "",
            "cover image": ""
          },
          "rating": 4.5,
          "review_text": ""
        }
      ],
      "favourites": [
        {
          "book_id": 1,
          "title": "",
          "author": "",
          "summary": "",
          "cover_image": ""
        },
        {
          "book_id": 2,
          "title": "",
          "author": "",
          "summary": "",
          "cover_image": ""
        }
      ]
    }
  ]
}
```

**Diagram Representation of the JSON Data Structure**

<img src="images/diagram.svg" alt="Diagram" width="100%">

### Authentication

The app uses Firebase Authentication to handle user authentication. The app uses email/password
authentication and Google authentication. 

### Technologies Used
Technologies Used:
* **Firebase:** The app utilizes Firebase's services, including Firebase Authentication, Firestore, and Firebase Analytics, to manage user authentication, store data, and track user behavior and usage.
* **Google Books API:** The app uses the Google Books API to search for books and retrieve book data.
* **Dagger-Hilt:** The app uses Dagger-Hilt, a dependency injection library, to manage dependencies and provide instances of objects throughout the app.
* **Kotlin Coroutines:** The app uses Kotlin Coroutines, a concurrency framework, to manage asynchronous operations and background tasks in a structured and organized way.
* **Retrofit:** The app uses Retrofit, a networking library, to make API requests and receive responses from external APIs.
* **GSON Converter:** The app uses GSON Converter, a data conversion library, to convert JSON data received from API responses into Kotlin objects for easier handling.
* **Coil:** The app uses Coil, an image loading and caching library, to load and display images in the app.
* **OkHttp:** The app uses OkHttp, a networking library, to handle HTTP requests and responses.
* **Preferences Datastore:** The app uses Preferences Datastore, a data storage library, to store user preferences and settings in a persistent and organized way.
* **LiveData:** The app uses LiveData, an Android Architecture Component, to handle data changes and updates in a lifecycle-aware way.
* **Jetpack Compose:** The app uses Jetpack Compose, a modern UI toolkit, to build the UI of the app.
