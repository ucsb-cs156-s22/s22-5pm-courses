# proj01-s22-5pm-courses

[![codecov](https://codecov.io/gh/ucsb-cs156-s22/s22-5pm-courses/branch/main/graph/badge.svg?token=XVVkeMISkP)](https://codecov.io/gh/ucsb-cs156-s22/s22-5pm-courses)

* Heroku production app: <https://s22-5pm-courses.herokuapp.com/>

* Heroku dashboard URL: <https://dashboard.heroku.com/apps/s22-5pm-courses/>

* Team 1 Heroku QA Link: <https://s22-5pm-1-courses-qa.herokuapp.com/>
* Team 1 Heroku QA Dashboard Link: <https://dashboard.heroku.com/apps/s22-5pm-1-courses-qa>
* Team 2 Heroku QA Link: <https://s22-5pm-2-courses-qa.herokuapp.com/>
* Team 2 Heroku QA Dashboard Link: <https://dashboard.heroku.com/apps/s22-5pm-2-courses-qa>


Storybook is here:
* Production: <https://ucsb-cs156-w22.github.io/team04-w22-5pm-courses-docs/>
* QA:  <https://ucsb-cs156-w22.github.io/team04-w22-5pm-courses-docs-qa/>

The GitHub actions script to deploy the Storybook to QA requires some configuration; see [docs/github-actions.md](docs/github-actions.md) for details.

If these repos are not yet setup, see the setup steps in [`docs/storybook.md`](docs/storybook.md).

# Setup before running application

Before running the application for the first time,
you need to do the steps documented in [`docs/oauth.md`](docs/oauth.md).

Otherwise, when you try to login for the first time, you 
will likely see an error such as:

<img src="https://user-images.githubusercontent.com/1119017/149858436-c9baa238-a4f7-4c52-b995-0ed8bee97487.png" alt="Authorization Error; Error 401: invalid_client; The OAuth client was not found." width="400"/>

# Getting Started on localhost

* Open *two separate terminal windows*  
* In the first window, start up the backend with:
  ``` 
  mvn spring-boot:run
  ```
* In the second window:
  ```
  cd frontend
  npm install  # only on first run or when dependencies change
  npm start
  ```

Then, the app should be available on <http://localhost:8080>

If it doesn't work at first, e.g. you have a blank page on  <http://localhost:8080>, give it a minute and a few page refreshes.  Sometimes it takes a moment for everything to settle in.

If you see the following on localhost, make sure that you also have the frontend code running in a separate window.

```
Failed to connect to the frontend server... On Heroku, be sure that PRODUCTION is defined.  On localhost, open a second terminal window, cd into frontend and type: npm install; npm start";
```

# Getting Started on Heroku

On Heroku, you'll need to set the following configuration variable:

* Using the Heroku CLI:
  ```
  heroku config:set PRODUCTION=true --app <heroku app name>
  ```
* Or set it on the Heroku Dashboard:
  ![image](https://user-images.githubusercontent.com/1119017/149855768-7b56164a-98f7-4357-b877-da34b7bd9ea4.png)

You'll also need to follow the OAuth set up instructions here: [`docs/oauth.md`](docs/oauth.md).

If you get the following message on Heroku, it probably means that you failed to setup the `PRODUCTION` environment variable.

```
Failed to connect to the frontend server... On Heroku, be sure that PRODUCTION is defined.  On localhost, open a second terminal window, cd into frontend and type: npm install; npm start";
```

# Accessing swagger

To access the swagger API endpoints, use:

* <http://localhost:8080/swagger-ui/index.html>


# To run React Storybook

* cd into frontend
* use: npm run storybook
* This should put the storybook on http://localhost:6006
* Additional stories are added under frontend/src/stories

* For documentation on React Storybook, see: https://storybook.js.org/

# Accessing Database Console

* On localhost only: <http://localhost:8080/h2-console>  See also: [docs/h2-console.md](docs/h2-console.md)
* On Heroku, with CLI:
  - Use: `heroku psql --app app-name-here` 
  - Note that this requires that you have the psql CLI tool installed on your system.  
  - This does work on CSIL, but you may need `heroku login -i` in order to login on CSIL
  - Example:
   
    ```
    [pconrad@csilvm-03 ~]$ heroku psql --app demo-spring-react-example
    ›   Warning: heroku update available from 7.59.1 to 7.59.2.
    --> Connecting to postgresql-tapered-84555
    psql (13.4, server 13.5 (Ubuntu 13.5-2.pgdg20.04+1))
    SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, bits: 256, compression: off)
    Type "help" for help.

    demo-spring-react-example::DATABASE=> 
    ```
* On Heroku, without CLI: 
  - Upper right of dashboard, select "More" then "Run Console"
    
    <img alt="Heroku Dashboard; More; Run Console" src="https://user-images.githubusercontent.com/1119017/150204550-a1027ab8-6ce7-4770-b566-a43928f5c3a0.png" width="300" />
  - Enter `psql $DATABASE_URL` and click `Run`
   
    <img alt="Enter psql $DATABASE_URL and click Run" src="https://user-images.githubusercontent.com/1119017/150206174-43193825-1afd-49f4-aeaf-cfadf0c0c6f3.png" width="400" />
* Cheatsheet of `psql` commands: <https://www.geeksforgeeks.org/postgresql-psql-commands/>

