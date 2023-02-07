package com.grayseal.bookshelf.model

/**
 * Class to convert a BooksResource object to a List of Book objects.
 */
class ResourceConverter {
    /**
     * Converts a BooksResource object into a List of Book objects.
     *
     * @param resource a BooksResource object that contains information about books
     * @return a List of Book objects
     */
    fun toBook(resource: BooksResource): List<Book> {
        // Code to convert BooksResource to List<Book>
        val listOfBooks = mutableListOf<Book>()
        resource.items
            ?.forEach { bookResource ->
                if (bookResource?.volumeInfo != null) {
                    var id = ""
                    if (bookResource.id != null) {
                        id = bookResource.id
                    }
                    var authors: List<String> = listOf("")
                    if (bookResource.volumeInfo.authors != null) {
                        authors = bookResource.volumeInfo.authors as List<String>
                    }

                    var averageRating = 0.0
                    if (bookResource.volumeInfo.averageRating != null) {
                        averageRating = bookResource.volumeInfo.averageRating
                    }

                    var categories: List<String> = listOf("")
                    if (bookResource.volumeInfo.categories != null) {
                        categories = bookResource.volumeInfo.categories as List<String>
                    }

                    var description = ""
                    if (bookResource.volumeInfo.description != null) {
                        description = bookResource.volumeInfo.description
                    }

                    var imageLinks: ImageLinks = ImageLinks(smallThumbnail = "", thumbnail = "")
                    if (bookResource.volumeInfo.imageLinks != null) {
                        imageLinks = bookResource.volumeInfo.imageLinks
                    }

                    var language = ""
                    if (bookResource.volumeInfo.language != null) {
                        language = bookResource.volumeInfo.language
                    }

                    var pageCount = 0
                    if (bookResource.volumeInfo.pageCount != null) {
                        pageCount = bookResource.volumeInfo.pageCount
                    }

                    var publishedDate = ""
                    if (bookResource.volumeInfo.publishedDate != null) {
                        publishedDate = bookResource.volumeInfo.publishedDate
                    }

                    var publisher = ""
                    if (bookResource.volumeInfo.publisher != null) {
                        publisher = bookResource.volumeInfo.publisher
                    }

                    var ratingsCount = 0
                    if (bookResource.volumeInfo.ratingsCount != null) {
                        ratingsCount = bookResource.volumeInfo.ratingsCount
                    }

                    var subtitle = ""
                    if (bookResource.volumeInfo.subtitle != null) {
                        subtitle = bookResource.volumeInfo.subtitle
                    }

                    var title = ""
                    if (bookResource.volumeInfo.title != null) {
                        title = bookResource.volumeInfo.title
                    }

                    var textSnippet = ""
                    if(bookResource.searchInfo != null) {
                        if (bookResource.searchInfo.textSnippet != null) {
                            textSnippet = bookResource.searchInfo.textSnippet
                        }
                    }

                    var industryIdentifiers: List<IndustryIdentifier> =
                        listOf(IndustryIdentifier(identifier = "", type = ""))
                    if (bookResource.volumeInfo.industryIdentifiers != null) {
                        industryIdentifiers =
                            bookResource.volumeInfo.industryIdentifiers as List<IndustryIdentifier>
                    }

                    val book = Book(
                        bookID = id,
                        authors = authors,
                        averageRating = averageRating,
                        categories = categories,
                        description = description,
                        imageLinks = imageLinks,
                        language = language,
                        pageCount = pageCount,
                        industryIdentifiers = industryIdentifiers,
                        publishedDate = publishedDate,
                        publisher = publisher,
                        ratingsCount = ratingsCount,
                        subtitle = subtitle,
                        title = title,
                        searchInfo = textSnippet
                    )
                    listOfBooks.add(book)
                }
            }
        return listOfBooks
    }
}


