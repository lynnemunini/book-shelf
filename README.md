# BookShelf App

### Database

The app utilizes Firestore, a No-SQL database service provided by Google, to store user and book data. Firestore is a document-based database, which means that it stores data in a collection of documents rather than in tables. This allows for more flexibility and scalability in terms of data organization and querying.

### JSON Data

In order to help with understanding the data structure and organization of the app, a sample of the JSON data used in the app is provided below

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

<img src="images/diagram.svg" alt="Diagram" width="80%">

### Authentication
The app uses Firebase Authentication to handle user authentication. The app uses email/password authentication and Google authentication. 

