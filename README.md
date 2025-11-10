# th2-cradle-admin 1.12.1

The th2-cradle-admin is a set of projects for managing books and pages in th2 storage.

## th2-cradle-admin-tool (th2 component)

The th2-cradle-admin-tool is th2 component which allows to manage books and pages via configuration automatically or via REST API manually

### Configuration

```yaml
apiVersion: th2.exactpro.com/v2
kind: Th2CoreBox
metadata:
  name: cradle-admin
spec:
  imageName: ghcr.io/th2-net/th2-cradle-admin-tool
  imageVersion: 1.11.1-dev
  type: th2-conn
  customConfig:
    ip: 0.0.0.0
    port: 8080
    page-recheck-interval: 60
    auto-book: true
    auto-pages:
      test_book:
        page-duration: "P1D"
        page-start-time: "1970-00-00T00:00:00.000Z"
  extendedSettings:
    service:
      enabled: true
      ingress:
        urlPaths:
          - /cradle-admin/
```

### REST API

#### View

* `/get-all-books` - returns all existed books
* `/get-book-info?book-id=test_book&with-pages=false&load-removed-pages` - returns information by arguments:
  * `book-id` - name (id) of requested book (required). You can specify multiple `book-id` arguments
  * `with-pages` - controls respond information about pages (optional)
  * `load-removed-pages` - this flag controls respond information about pages marked as removed (optional)

#### Book management 

* `/new-book?book-name=test_book&created-time=1970-00-00T00:00:00.000Z&full-name=test_book&desc=first_book&first-page-name=first_page` - creates new a book by arguments:
  * `book-name` - name (id) of new book (required)
  * `full-name` - extended book name (optional)
  * `desc` - description for book (optional)
  * `first-page-name` - name of the first page (optional). You can add page later

#### Page management

* `/new-page?book-id=test_book&page-name=first_page&page-start=1970-00-00T00:00:00.000Z&page-comment=my_first_page` - crates a new page by arguments:
  * `book-id` - name (id) where the new page will be created (required).
  * `page-name` - name of the new page (optional). If you miss this argument, name is generated automatically 
  * `page-start` - start time of the new page (required)
  * `page-comment` - comment for the new page (optional)
* `/update-page?book-id=test_book&page-name=first_page&new-page-comment=my_second_page&new-page-name=second_page` - update existed page by arguments:
  * `book-id` - name (id) where the new page will be created (required).
  * `page-name` - name of the updated page (required).
  * `new-page-name` - new name for updated page (optional). Only pages in future can be renamed.
  * `new-page-comment` - new comment for updated page (optional). Comment of any page can be updated  
* `/remove-page?book-id=test_book&page-name=first_page` - removes a page by arguments:
  * `book-id` - name (id) where the removed page is (required).
  * `page-name` - name of the removed page (required).

## Release notes:

### 1.12.1-dev

+ Produce multi-platform docker image
  + migrated to [amazoncorretto:11-alpine-jdk](https://hub.docker.com/layers/library/amazoncorretto/11-alpine-jdk) docker image as base
+ Updated:
  + th2-gradle-plugin: `0.3.10` (th2-bom: `4.14.2`)
  + Cradle API to: `5.7.0-dev`
  + common: `5.16.1-dev`
  + commons-cli: `1.10.0`
  + jackson-datatype-jsr310: `2.20.1`

### 1.12.0-dev
* Updated:
  * migrated to jetty: `11.0.24`
  * th2-gradle-plugin: `0.2.3` (th2-bom: `4.11.0`)
  * Cradle API to `5.4.4-dev`
* Added vulnerability suppressions
  * CVE-2024-6763 - The component doesn’t communicate with others components or client’s systems via HTTP and even hasn’t got credentials for such communication.

### 1.11.2-dev

* Included scripts for REST API interaction and installed required utils into docker image 
* Updated 
  * th2-gradle-plugin: `0.1.8` (th2-bom: `4.10.0`)
  * jackson-datatype-jsr310: `2.18.2`

### 1.11.1-dev
* Updated:
  * Cradle API to `5.4.3-dev` (executes insert and update operations on pages atomically)
  * th2-gradle-plugin: `0.1.3` (th2-bom: `4.8.0`)
  * common to `5.14.0-dev`
  * jetty: `9.4.56.v20240826`
  * commons-cli: `1.9.0`

### 1.11.0-dev
* Migrated to th2 gradle plugin `0.0.8`
* Updated common: `5.12.0-dev`

### 1.10.0-dev
* Migrated to th2 gradle plugin `0.0.6`
* Updated:
  * bom: `4.6.1`
  * common: `5.11.0-dev`
  * cradle: `5.3.0-dev`
  * jetty: `9.4.54.v20240208`
  * commons-cli: `1.7.0`