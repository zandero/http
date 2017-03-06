# http
Http utilities for easy request building and execution

Wrapper around Apache Http client 


## HttpUtils

### Step 1 - build your request

```java
HttpRequestBase req = HttpUtils.get("http://httpbin.org/get");	
```
		
### Step 2 - execute request 
```java	
HttpResponse res = HttpUtils.execute(req);
```