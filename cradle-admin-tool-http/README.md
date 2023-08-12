# cradle-admin-tool-http (1.7.0)
Service which allows user to manage books/pages via RestAPI requests.
- The first page in a book can be created only if start time is more than current time.
- After the first page all new pages must have start time more than current time + `bookRefreshIntervalMillis` * 2
- Pages can be updated and removed if their start time is more than current time + `bookRefreshIntervalMillis` * 2

`bookRefreshIntervalMillis` is a configurable amount of time found in cradle storage settings



## Configuration
- **ip** - host where http cradle admin instance will be instanciated. Default value: `0.0.0.0`
- **port** - port on which http server will listen user requests. Default value: `8080`
- **page-recheck-interval** - interval in seconds which `PageManager` service checks if new page is required to create or not based on duration values presented in `auto-pages`. Default value: 60 seconds
- **auto-pages** - defines rule for automatic pages creation for multiple books. If empty no pages will be created automatically. Default value: `empty_map`.
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
  image-version: 1.6.1
  type: th2-box
  custom-config:
    ip: 198.168.0.2
    port: 8080
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

## Release notes

### 1.7.0

+ Feature:
  + Considers default value of page action reject threshold (2 min). This calculated parameter prevent page adding if its start time is before now + threshold value. 
    Cradle-admin uses now + threshold value * 2 (4 min) time to create auto-pages
    
+ Bug fix:
  + Cradle-admin creates auto-page in future even if the start time of a last page is much earlier than now + threshold

+ Updated:
  + Cradle API to `5.0.3-dev`  
  + common to `5.2.1-dev`  