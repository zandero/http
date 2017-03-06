# http
Http utilities for easy request building and execution

Wrapper around Apache Http client 

# Setup
```xml
<dependency>      
     <groupId>com.zandero</groupId>      
     <artifactId>http</artifactId>      
     <version>1.0</version>      
</dependency>
```

## HttpUtils

### Step 1 - build your request

```java
HttpRequestBase req = HttpUtils.get("http://httpbin.org/get");	
```
		
### Step 2 - execute request 
```java	
HttpResponse res = HttpUtils.execute(req);
```