# cradle-admin-tool-http (1.6.0)
Service which allows user to manage books/pages via RestAPI requests.

## Configuration
- **ip** - host where http cradle admin instance will be instanciated. Default value: `0.0.0.0`
- **port** - port on which http server will listen user requests. Default value: `8080`
- **auto-pages** - defines lengths for pages for multiple books. If empty no pages will be create automatically. Default value: `empty_map`
- **page-recheck-interval** - interval in which `PageManager` service checks if new page is required to create or not based on duration values presented in `auto-pages`.
- **autoPagesStartTime** - baseline date and time for every new page created by `PageManager`.
