# cradle-admin-tool-http (1.11.1-dev)
Service which allows user to manage books/pages via RestAPI requests.
- The first page in a book can be created only if start time is more than current time.
- After the first page all new pages must have start time more than current time + `bookRefreshIntervalMillis` * 2
- Pages can be updated and removed if their start time is more than current time + `bookRefreshIntervalMillis` * 2

`bookRefreshIntervalMillis` is a configurable amount of time found in cradle storage settings



## Configuration
- **ip** - host where http cradle admin instance will be instantiated. Default value: `0.0.0.0`
- **port** - port on which http server will listen user requests. Default value: `8080`
- **page-recheck-interval** - interval in seconds which `PageManager` service checks if new page is required to create or not based on duration values presented in `auto-pages`. Default value: 60 seconds
- **auto-book** - if `true` than cradle-admin-tool creates books with first page for each value from `auto-pages` option when target book doesn't exist in cradle. 
  Creation book time is calculate by the `current time - 1 day` formula to cover events and messages published a bit earlier than cradle-admin-tool started. Default value: `true`.
  Please note you can create your own book via REST API later.
- **auto-pages** - defines rule for automatic pages creation for multiple books. If empty no pages will be created automatically.
  - **page-duration** - defines duration of the page for the book. Value uses the Java Duration format. You can read more about it [here](https://docs.oracle.com/javase/8/docsT/api/java/time/Duration.html#parse-java.lang.CharSequence-).
  - **page-start-time** - baseline date and time for every new page created by `PageManager` for this book.

### Configuration example:
```yaml
apiVersion: th2.exactpro.com/v1
kind: Th2GenericBox
metadata:
  name: cradle-admin
spec:
  image-name: your.image.repo:42/cradle-admin-tool-http
  image-version: 1.11.1-dev
  type: th2-box
  custom-config:
    ip: 198.168.0.2
    port: 8080
    auto-book: true
    auto-pages:
      book1:
        page-duration: PT60S
        page-start-time: 2023-03-27T12:00:00
      book2: 
        page-duration: PT60H
        page-start-time: 2023-03-27T12:00:00
      book3: 
        page-duration: PT60M
        page-start-time: 2023-03-27T12:00:00
    page-recheck-interval: 60 
```

## Scripts

### cradle-admin-tool-http/scripts/update-pages-comment.sh

```
Help:
 Description: this script provide ability to update comment for pages covered by time rage where page.started is included and page.ended is excluded
 Required utils: jq curl paste grep head
 Arguments:
  --mode (optional) - work mode. Default value is 'get'
     * 'append' - appends existed pages' comment by text specified using --comment. 'auto-page' default page comment is removed
       Final comment has JSON string array format, for example: '["<existed comment>","<specified comment>"]'
     * 'set' - sets text specified using --comment as pages' comment
       Final comment has JSON string array format, for example: '["<specified comment>"]'
     * 'reset' - resets pages' comment to 'auto-page' default page comment
       Final comment is 'auto-page'
     * 'get' - prints pages and their comments
  --cradle-admin-tool-url (required) - cradle admin tool URL
  --book (required) - th2 book for searching and updating pages
  --start-timestamp (conditional) - start timestamp for searching page to add --comment comment.
     - ['append','set','reset'] modes (required)
     - 'get' mode (optional) - default is min timestamp
  --end-timestamp (conditional) - end timestamp for searching page to add --comment comment.
     - ['append','set','reset'] modes (required)
     - 'get' mode (optional) - default is max timestamp
  --comment (conditional) - comment for adding to pages found from --start-timestamp to --end-timestamp. Required for ['append','set'] modes
```

## Release notes

### 1.11.1-dev

+ Updated:
  + Cradle API to `5.4.3-dev` (executes insert and update operations on pages atomically)
  + th2-gradle-plugin: `0.1.3` (bom: `4.8.0`)
  + common to `5.14.0-dev`
  + jetty: `9.4.56.v20240826`
  + commons-cli: `1.9.0`

### 1.8.0

+ Feature:
  + Added auto-book functionality

### 1.7.2

+ Bug fix:
  + Migrated to the cradle version where auto-page feature doesn't produce cassandra tombstone.
  
+ Updated:
  + Cradle API to `5.1.4-dev`
  + bom to `4.5.0`
  + common to `5.4.2-dev`

### 1.7.1

+ Vulnerability fix:
  + Excluded the `com.squareup.okhttp3:okhttp` dependency 

### 1.7.0

+ Feature:
  + Considers default value of page action reject threshold (2 min). This calculated parameter prevent page adding if its start time is before now + threshold value. 
    Cradle-admin uses now + threshold value * 2 (4 min) time to create auto-pages
    
+ Bug fix:
  + Cradle-admin creates auto-page in future even if the start time of a last page is much earlier than now + threshold

+ Updated:
  + Cradle API to `5.0.3-dev`
  + common to `5.2.1-dev`