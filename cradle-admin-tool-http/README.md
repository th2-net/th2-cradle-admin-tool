# cradle-admin-tool-http (1.6.0)
Service which allows user to manage books/pages via RestAPI requests.

## Configuration
- **ip** - host where http cradle admin instance will be instanciated. Default value: `0.0.0.0`
- **port** - port on which http server will listen user requests. Default value: `8080`
- **auto-pages** - defines lengths for pages for multiple books. If empty no pages will be create automatically. Default value: `empty_map`. Values uses the Java Duration format. You can read more about it [here](https://docs.oracle.com/javase/8/docsT/api/java/time/Duration.html#parse-java.lang.CharSequence-).
- **page-recheck-interval** - interval in seconds which `PageManager` service checks if new page is required to create or not based on duration values presented in `auto-pages`. Default value: 60 seconds
- **auto-page-start-time** - baseline date and time for every new page created by `PageManager`.

### Configuration example:
```yaml
apiVersion: th2.exactpro.com/v1
kind: Th2GenericBox
metadata:
  name: cradle-admin
spec:
  image-name: your.image.repo:42/cradle-admin-tool-http
  image-version: 1.6.0
  type: th2-box
  custom-config:
    ip: 198.168.0.2
    port: 8080
    auto-pages:
        book1: PT60S
        book2: PT60S
        book3: PT60S
    page-recheck-interval: 60
    auto-page-start-time: 2023-03-27T12:00:00
```
