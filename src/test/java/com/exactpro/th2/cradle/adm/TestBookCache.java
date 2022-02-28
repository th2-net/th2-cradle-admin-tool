package com.exactpro.th2.cradle.adm;

import com.exactpro.cradle.BookCache;
import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.utils.CradleStorageException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TestBookCache implements BookCache {

    Map<BookId, BookInfo> books;

    TestBookCache () {
        books = new ConcurrentHashMap<>();
    }

    @Override
    public BookInfo getBook(BookId bookId) throws CradleStorageException {
        if (!books.containsKey(bookId)) {
            throw new CradleStorageException(String.format("Book %s is unknown", bookId.getName()));
        }
        return books.get(bookId);
    }

    @Override
    public boolean checkBook(BookId bookId) {
        return books.containsKey(bookId);
    }

    @Override
    public Collection<PageInfo> loadPageInfo(BookId bookId) throws CradleStorageException {
        throw new CradleStorageException("Method not supported");
    }

    @Override
    public BookInfo loadBook(BookId bookId) throws CradleStorageException {
        throw new CradleStorageException("Method not supported");
    }

    @Override
    public Collection<BookInfo> loadBooks() throws CradleStorageException {
        return Collections.unmodifiableCollection(books.values());
    }

    @Override
    public void updateCachedBook(BookInfo bookInfo) {
        books.put(bookInfo.getId(), bookInfo);
    }

    @Override
    public Collection<BookInfo> getCachedBooks() {
        return Collections.unmodifiableCollection(books.values());
    }
}

