# Github Client - tags for repositories

This web app allows you to tag your starred repositories.

## How to run this app

The following steps can install Google App Engine SDK automatically, but you may need to manually install the Google App Engine SDK if any problem occurs in your system.

### How to run locally

To run this application locally:

```
./gradle appengineRun
```

To enable hot-reload, execute this in another terminal while the application is running:

```
./gradle -t build
```

### Hot to run the database in local mode

```
gcloud beta emulators datastore env-init > set_vars.cmd && set_vars.cmd
gcloud beta emulators datastore start
```

To run without persisting data:

```
gcloud beta emulators datastore start --no-store-on-disk
```

### How to run tests

To run the tests in this application:

```
./gradle test
```

### How to deploy

To deploy in your Google account:

```
./gradle appengineDeploy
```

