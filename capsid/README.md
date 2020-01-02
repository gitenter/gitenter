## Development environment setting

## Development

### STS

Although for deployment we should just `mvn package` and put the `.war` file in an existing Tomcat folder, for testing it is more convenient to run the Spring Boot application inside of STS.

To set it up, Go to `Run > Run configurations` and under the `Spring Boot` leave and Main type `enterovirus.capsid.CapsidApplication`. In Override Properties, you right click Add Row and set property as `server.port`, and value as `8888`. Then the site is accessible from `http://localhost:8888`.

## Deployment

Shell scripts in `capsid/src/main/resources/git-server-side-hooks` need to be executable. Make it works by e.g., `chmod +x update`.

## Testing

### Spring authorization

It ever happens that the website ask a spring authorization. I don't understand the reason yet (if recompile, the authorization is no longer needed, even if from a different browser).

```
http://localhost:8888 is requesting your username and password. The site says: “Spring”
```

If that happens, use user `user` and password printed in the console. E.g.

```
[main] INFO org.springframework.boot.autoconfigure.security.AuthenticationManagerConfiguration -

Using default security password: c543b70b-7aa5-4dcf-ab0b-eea37e792363
```

## API Doc

* http://localhost:8080/api-docs OpenAPI doc in JSON format
* http://localhost:8080/api-docs.yaml OpenAPI doc in YAML format
* http://localhost:8080/swagger-ui.html
