/*******************************************************************************
 * Copyright 2022 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.exactpro.th2.cradle.adm;

import com.exactpro.cradle.*;
import com.exactpro.cradle.counters.Counter;
import com.exactpro.cradle.counters.CounterSample;
import com.exactpro.cradle.counters.Interval;
import com.exactpro.cradle.intervals.IntervalsWorker;
import com.exactpro.cradle.messages.*;
import com.exactpro.cradle.resultset.CradleResultSet;
import com.exactpro.cradle.testevents.StoredTestEvent;
import com.exactpro.cradle.testevents.StoredTestEventId;
import com.exactpro.cradle.testevents.TestEventFilter;
import com.exactpro.cradle.testevents.TestEventToStore;
import com.exactpro.cradle.utils.CradleStorageException;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class TestCradleStorage extends CradleStorage {
    private BookCache bookCache;
    private List<BookListEntry> bookList;
    private Map<BookId, List<PageInfo>> pages;
    private Instant nextRemovedTime;

    public TestCradleStorage(ExecutorService composingService, int maxMessageBatchSize, int maxMessageBatchDuration, int maxTestEventBatchSize) throws CradleStorageException {
        super(composingService, maxMessageBatchSize, maxTestEventBatchSize);
        bookCache = new TestBookCache();
    }

    public TestCradleStorage() throws CradleStorageException {
        super();
        bookCache = new TestBookCache();
        bookList = new ArrayList<>();
        pages = new HashMap<>();
    }

    public void setNextRemovedTime(Instant nextRemovedTime) {
        this.nextRemovedTime = nextRemovedTime;
    }

    public BookInfo getBook(BookId bookId) throws CradleStorageException {
        return bookCache.getBook(bookId);
    }

    public BookInfo getBook(String bookId) throws CradleStorageException {
        return getBook(new BookId(bookId));
    }

    public int getBooksCount() {
        return this.getBooks().size();
    }

    public PageInfo getPage(PageId pageId) throws CradleStorageException {
        return getBook(pageId.getBookId()).getPage(pageId);
    }

    @Override
    protected void doInit(boolean prepareStorage) throws CradleStorageException {

    }

    @Override
    protected BookCache getBookCache() {
        return bookCache;
    }

    @Override
    protected void doDispose() throws CradleStorageException {

    }

    @Override
    protected Collection<BookListEntry> doListBooks() {
        return bookList;
    }

    @Override
    protected void doAddBook(BookToAdd newBook, BookId bookId) throws IOException {
        bookList.add(new BookListEntry(bookId.getName(), null));
    }

    @Override
    protected void doAddPages(BookId bookId, List<PageInfo> pages, PageInfo lastPage) throws CradleStorageException, IOException {
        this.pages.putIfAbsent(bookId, new ArrayList<>());

        if (lastPage != null) {
            this.pages.get(bookId).removeIf(page -> page.getId().equals(lastPage.getId()));
            this.pages.get(bookId).add(lastPage);
        }
        this.pages.get(bookId).addAll(pages);
    }

    @Override
    protected Collection<PageInfo> doLoadPages(BookId bookId) throws CradleStorageException, IOException {
        return getBookCache().getBook(bookId).getPages();
    }

    @Override
    protected Collection<PageInfo> doGetAllPages(BookId bookId) throws CradleStorageException {
        List<PageInfo> book = new ArrayList<>();
        if(pages.containsKey(bookId)){
            book = pages.get(bookId);
        }
        return book.stream().sorted(Comparator.comparing(PageInfo::getStarted)).collect(Collectors.toList());
    }

    @Override
    protected void doRemovePage(PageInfo page) throws CradleStorageException, IOException {
        this.pages.get(page.getId().getBookId()).remove(page);
        this.pages.get(page.getId().getBookId()).add(new PageInfo(
                page.getId(),
                page.getStarted(),
                page.getEnded(),
                page.getComment(),
                page.getUpdated(),
                nextRemovedTime == null ? Instant.now() : nextRemovedTime
        ));
    }

    @Override
    protected void doStoreMessageBatch(MessageBatchToStore batch, PageInfo page) throws IOException, CradleStorageException {

    }

    @Override
    protected void doStoreGroupedMessageBatch(GroupedMessageBatchToStore batch, PageInfo page) throws IOException {

    }

    @Override
    protected CompletableFuture<Void> doStoreMessageBatchAsync(MessageBatchToStore batch, PageInfo page) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<Void> doStoreGroupedMessageBatchAsync(GroupedMessageBatchToStore batch, PageInfo page) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected void doStoreTestEvent(TestEventToStore event, PageInfo page) throws IOException, CradleStorageException {

    }

    @Override
    protected CompletableFuture<Void> doStoreTestEventAsync(TestEventToStore event, PageInfo page) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected void doUpdateParentTestEvents(TestEventToStore event) throws IOException {

    }

    @Override
    protected CompletableFuture<Void> doUpdateParentTestEventsAsync(TestEventToStore event) {
        return null;
    }

    @Override
    protected void doUpdateEventStatus(StoredTestEvent event, boolean success) throws IOException {

    }

    @Override
    protected CompletableFuture<Void> doUpdateEventStatusAsync(StoredTestEvent event, boolean success) {
        return null;
    }

    @Override
    protected StoredMessage doGetMessage(StoredMessageId id, PageId pageId) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<StoredMessage> doGetMessageAsync(StoredMessageId id, PageId pageId) throws CradleStorageException {
        return null;
    }

    @Override
    protected StoredMessageBatch doGetMessageBatch(StoredMessageId id, PageId pageId) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<StoredMessageBatch> doGetMessageBatchAsync(StoredMessageId id, PageId pageId) throws CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<StoredMessage> doGetMessages(MessageFilter filter, BookInfo book) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<StoredMessage>> doGetMessagesAsync(MessageFilter filter, BookInfo book) throws CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<StoredMessageBatch> doGetMessageBatches(MessageFilter filter, BookInfo book) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<StoredGroupedMessageBatch> doGetGroupedMessageBatches(GroupedMessageFilter filter, BookInfo book) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<StoredMessageBatch>> doGetMessageBatchesAsync(MessageFilter filter, BookInfo book) throws CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<StoredGroupedMessageBatch>> doGetGroupedMessageBatchesAsync(GroupedMessageFilter filter, BookInfo book) throws CradleStorageException {
        return null;
    }

    @Override
    protected long doGetLastSequence(String sessionAlias, Direction direction, BookId bookId) throws IOException, CradleStorageException {
        return 0;
    }

    @Override
    protected long doGetFirstSequence(String sessionAlias, Direction direction, BookId bookId) throws IOException, CradleStorageException {
        return 0;
    }

    @Override
    protected Collection<String> doGetSessionAliases(BookId bookId) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected StoredTestEvent doGetTestEvent(StoredTestEventId id, PageId pageId) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<StoredTestEvent> doGetTestEventAsync(StoredTestEventId ids, PageId pageId) throws CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<StoredTestEvent> doGetTestEvents(TestEventFilter filter, BookInfo book) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<StoredTestEvent>> doGetTestEventsAsync(TestEventFilter filter, BookInfo book) throws CradleStorageException {
        return null;
    }

    @Override
    protected Collection<String> doGetScopes(BookId bookId) throws IOException, CradleStorageException {
        return null;
    }

    @Override
    public IntervalsWorker getIntervalsWorker(PageId pageId) {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<CounterSample>> doGetMessageCountersAsync(BookId bookId, String sessionAlias, Direction direction, FrameType frameType, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<CounterSample> doGetMessageCounters(BookId bookId, String sessionAlias, Direction direction, FrameType frameType, Interval interval) throws CradleStorageException, IOException {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<CounterSample>> doGetCountersAsync(BookId bookId, EntityType entityType, FrameType frameType, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<CounterSample> doGetCounters(BookId bookId, EntityType entityType, FrameType frameType, Interval interval) throws CradleStorageException, IOException {
        return null;
    }

    @Override
    protected CompletableFuture<Counter> doGetMessageCountAsync(BookId bookId, String sessionAlias, Direction direction, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected Counter doGetMessageCount(BookId bookId, String sessionAlias, Direction direction, Interval interval) throws CradleStorageException, IOException {
        return null;
    }

    @Override
    protected CompletableFuture<Counter> doGetCountAsync(BookId bookId, EntityType entityType, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected Counter doGetCount(BookId bookId, EntityType entityType, Interval interval) throws CradleStorageException, IOException {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<String>> doGetSessionAliasesAsync(BookId bookId, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<String> doGetSessionAliases(BookId bookId, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected CompletableFuture<CradleResultSet<String>> doGetSessionGroupsAsync(BookId bookId, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected CradleResultSet<String> doGetSessionGroups(BookId bookId, Interval interval) throws CradleStorageException {
        return null;
    }

    @Override
    protected PageInfo doUpdatePageComment(BookId bookId, String pageName, String comment) throws CradleStorageException {
        return null;
    }

    @Override
    protected PageInfo doUpdatePageName(BookId bookId, String pageName, String newPageName) throws CradleStorageException {
        return null;
    }
}
